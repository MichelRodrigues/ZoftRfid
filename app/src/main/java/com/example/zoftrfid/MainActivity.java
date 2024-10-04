package com.example.zoftrfid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_MEDIA_PERMISSIONS = 2;
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private static final String TAG = "MainActivity";
    private final OkHttpClient client = new OkHttpClient();
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions();

        // Inicializar os componentes
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Configurar o listener do botão
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Ocultar o teclado
            hideKeyboard();

            // Desabilitar o botão "Enviar" para evitar múltiplos cliques
            btnLogin.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            // Verificar se os campos estão vazios
            if (username.isEmpty() && password.isEmpty()) {
                // Chamar a MainScreenActivity se ambos os campos estiverem vazios
                goToMainScreen();
            } else {
                // Executar a tarefa de autenticação se os campos não estiverem vazios
                authenticateUser(username, password);
            }
        });
    }

    // Método para ocultar o teclado
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View view = getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    // Método para navegar para a tela principal
    private void goToMainScreen() {

            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
            startActivity(intent);
            finish(); // Encerra a atividade atual para evitar que o usuário volte para ela pelo botão de voltar


    }

    private void authenticateUser(String username, String password) {
        // Criação do JSON a partir das credenciais do usuário
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("usuario", username);
            jsonInput.put("senha", password);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar JSON", e);
            return;
        }

        // Criação da requisição POST
        RequestBody body = RequestBody.create(jsonInput.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://177.234.156.125:9000/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erro na autenticação", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Erro ao fazer login. Verifique sua conexão.", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body() != null ? response.body().string() : "";

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        // Processar o JSON de resposta
                        JSONObject jsonResponse = new JSONObject(result);
                        String token = jsonResponse.getString("token");

                        // Salvar o token no SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("authToken", token);
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_LONG).show();

                        // Chamar a MainScreenActivity após login bem-sucedido
                        goToMainScreen();

                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar resposta JSON", e);
                        Toast.makeText(MainActivity.this, "Erro ao fazer login. Verifique usuário, senha e conexão.", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                    }
                });
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void verifyStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
            String[] permissions = {
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.READ_MEDIA_AUDIO
            };

            // Verificar se as permissões de mídia foram concedidas
            boolean allPermissionsGranted = true;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                // Solicitar permissões de mídia para Android 13+
                ActivityCompat.requestPermissions(this, permissions, REQUEST_MEDIA_PERMISSIONS);
            }
        } else {  // Android 12 e inferior
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            // Verificar se as permissões de armazenamento foram concedidas
            boolean allPermissionsGranted = true;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                // Solicitar permissões de armazenamento para Android 12 e inferior
                ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE || requestCode == REQUEST_MEDIA_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
            } else {
                // Permissão negada
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
