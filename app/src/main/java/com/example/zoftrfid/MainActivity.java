package com.example.zoftrfid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar os componentes
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Configurar o listener do botão
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Verificar se os campos estão vazios
            if (username.isEmpty() && password.isEmpty()) {
                // Ir diretamente para a próxima tela se ambos os campos estiverem vazios
                goToMainScreen();
            } else {
                // Executar a tarefa de autenticação se qualquer campo estiver preenchido
                new AuthenticateUserTask(this).execute(username, password);
            }
        });
    }

    // Método para navegar para a tela principal
    private void goToMainScreen() {
        Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
        startActivity(intent);
        finish(); // Encerra a atividade atual para evitar que o usuário volte para ela pelo botão de voltar do dispositivo
    }

    private static class AuthenticateUserTask extends AsyncTask<String, Void, String> {
        private final WeakReference<MainActivity> activityReference;

        AuthenticateUserTask(MainActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String result = "";

            try {
                URL url = new URL("http://177.234.156.125:9000/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Criação do JSON a partir das credenciais
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
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            try {
                // Processar o JSON de resposta
                JSONObject jsonResponse = new JSONObject(result);
                String token = jsonResponse.getString("token");

                if (!token.equals("0")) {
                    Toast.makeText(activity, "Login bem-sucedido! Token recebido: " + token, Toast.LENGTH_SHORT).show();
                    // Redirecionar para a tela principal
                    Intent intent = new Intent(activity, MainScreenActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao processar resposta JSON", e);
                Toast.makeText(activity, "Erro ao processar resposta JSON", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
