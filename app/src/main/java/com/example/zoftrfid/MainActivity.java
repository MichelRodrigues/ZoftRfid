package com.example.zoftrfid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private static final String TAG = "MainActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        finish(); // Encerra a atividade atual para evitar que o usuário volte para ela pelo botão de voltar do dispositivo
    }

    // Método que cria e configura a conexão HTTP
    private HttpURLConnection createConnection() throws Exception {
        URL url = new URL("http://177.234.156.125:9000/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        return conn;
    }

    private void authenticateUser(String username, String password) {
        executorService.execute(() -> {
            String result = "";

            try {
                // Usar o método createConnection para configurar a conexão HTTP
                HttpURLConnection conn = createConnection();

                // Criação do JSON a partir das credenciais do usuário
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("usuario", username);
                jsonInput.put("senha", password);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Ler a resposta da API
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    result = response.toString();
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro na autenticação", e);
            }

            final String finalResult = result;
            runOnUiThread(() -> processResult(finalResult));
        });
    }

    private void processResult(String result) {
        progressBar.setVisibility(View.GONE);
        try {
            // Processar o JSON de resposta
            JSONObject jsonResponse = new JSONObject(result);
            String token = jsonResponse.getString("token");

            // Salvar o token no SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("auth_token", token);
            editor.apply();

            Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_LONG).show();

            // Chamar a MainScreenActivity após login bem-sucedido
            goToMainScreen();

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar resposta JSON", e);
            Toast.makeText(MainActivity.this, "Erro ao fazer login. Verifique usuário, senha e conexão.", Toast.LENGTH_SHORT).show();
            // Reabilitar o botão "Enviar" em caso de erro
            btnLogin.setEnabled(true);
        }
    }
}
