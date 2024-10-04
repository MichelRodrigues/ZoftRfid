package com.example.zoftrfid;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.trans.OtgUtils;
import com.rfid.trans.ReaderHelp;
import com.rfid.trans.ReaderParameter;

import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity {
    private ReaderHelp readerHelp;
    private final String comPort = "/dev/ttyHSL0";
    private final int logSwitch = 1;  // Defina o valor do logSwitch, conforme necessário
    public static int baud = 57600;  // Defina o baud rate inicial como 57600
    private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;
    private String baudRate = "115200bps";  // Exemplo de taxa de transmissão atual
    private String[] bandOptions = {"Chinese 2", "USA", "Korean", "European", "Chinese 1", "Todas"};
    private String[] baudRateOptions = {"115200bps", "57600bps"};


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        readerHelp = new ReaderHelp();

        TextView tvSettings = findViewById(R.id.tvSettings);

        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        // Inicializa o GPIO
        initGpio();


        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPref.getString("authToken", null);
        if (token == null) {
            // Exibir uma mensagem ou realizar uma ação quando o token não for encontrado
            Toast.makeText(this, "Sem conexão com o servidor", Toast.LENGTH_SHORT).show();
        }


        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreenActivity.this, TagRegistrationActivity.class));
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreenActivity.this, ProductSearchActivity.class));
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreenActivity.this, ItemQueryActivity.class));
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreenActivity.this, InventoryActivity.class));
            }
        });

        VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver = new VirtualKeyListenerBroadcastReceiver();
        registerReceiver(mVirtualKeyListenerBroadcastReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"), Context.RECEIVER_NOT_EXPORTED);
        displayDeviceInfo(tvSettings);
    }
    private static class VirtualKeyListenerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String reason = intent.getStringExtra("reason");
            if (Objects.equals(intent.getAction(), "android.intent.action.CLOSE_SYSTEM_DIALOGS") && reason != null) {
                String SYSTEM_HOME_KEY = "homekey";
                String SYSTEM_RECENT_APPS = "recentapps";
                if (reason.equals(SYSTEM_HOME_KEY)) {
                    Log.d("RFID", "Press HOME key");
                    // Desativa GPIO
                    // Exemplo: OtgUtils.set53GPIOEnabled(false);
                } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                    Log.d("RFID", "Press RECENT_APPS key");
                    // Ativa GPIO
                    // Exemplo: OtgUtils.set53GPIOEnabled(true);
                }
            }
        }
    }

    private void showPopup() {
        // Inflar o layout do pop-up
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_config, null);

        // Criar o AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupView);
        AlertDialog dialog = dialogBuilder.create();

        // Elementos do pop-up
        TextView tvBaudRate = popupView.findViewById(R.id.tvBaudRate);
        tvBaudRate.setText("Taxa de transmissão atual: " + baudRate);

        // Spinner da Banda
        Spinner spinnerBanda = popupView.findViewById(R.id.spinnerBanda);
        ArrayAdapter<String> bandaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bandOptions);
        spinnerBanda.setAdapter(bandaAdapter);

        // Spinner da Taxa de Transmissão
        Spinner spinnerTaxaTransmissao = popupView.findViewById(R.id.spinnerTaxaTransmissao);
        ArrayAdapter<String> baudAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, baudRateOptions);
        spinnerTaxaTransmissao.setAdapter(baudAdapter);

        // Botão para confirmar Banda
        Button btnConfirmBanda = popupView.findViewById(R.id.btnConfirmBanda);
        btnConfirmBanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBand = spinnerBanda.getSelectedItem().toString();
                // Logica para salvar/usar a banda selecionada
                showMessage("Banda selecionada: " + selectedBand);
            }
        });

        // Botão para confirmar Taxa de Transmissão
        Button btnConfirmTaxa = popupView.findViewById(R.id.btnConfirmTaxa);
        btnConfirmTaxa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBaudRate = spinnerTaxaTransmissao.getSelectedItem().toString();
                // Logica para salvar/usar a taxa selecionada
                showMessage("Taxa de transmissão selecionada: " + selectedBaudRate);
            }
        });

        // Botão Fechar
        Button btnClose = popupView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Fecha o pop-up
            }
        });

        // Mostrar o pop-up
        dialog.show();
    }

    private void showMessage(String message) {
        // Aqui você pode usar Toast ou qualquer outro método para mostrar a mensagem
        // Exemplo: Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        System.out.println(message);  // Exibindo no log do sistema
    }

    private void displayDeviceInfo(TextView tvSettings) {
        String deviceInfo = "Modelo: " + Build.MODEL + "\n" +
                "Fabricante: " + Build.MANUFACTURER + "\n" +
                "Versão do Android: " + Build.VERSION.RELEASE;

        //Toast.makeText(this, deviceInfo, Toast.LENGTH_LONG).show();

        // Verifica se o modelo começa com "RFID" e conecta
        if (Build.MODEL.startsWith("RFID")) {
            connectToSerialPort();
            tvSettings.setVisibility(View.VISIBLE); // Torna visível o TextView "Configurações"
        } else {
            tvSettings.setVisibility(View.GONE); // Esconde caso não comece com "RFID"
        }
    }

    private void connectToSerialPort() {
        try {
            if (readerHelp.Connect(comPort, 57600, logSwitch) == 0) {
                //showToast("Conectado a 57600 bits/s");
                baudRate = "57600bps";//trocar essa logica depois
                initRfid();
            } else if (readerHelp.Connect(comPort, 115200, logSwitch) == 0) {
                //showToast("Conectado a 115200 bits/s");
                baudRate = "115200bps";
                initRfid();
            } else {
                Toast.makeText(MainScreenActivity.this, "Falha ao conectar", Toast.LENGTH_SHORT).show();
                Log.e("RFID", "Erro na conexão com ambos os baud rates.");
            }
        } catch (Exception e) {
            Log.e("RFID", "Erro ao tentar conectar à porta serial", e);
            Toast.makeText(MainScreenActivity.this, "Erro ao tentar conectar à porta serial", Toast.LENGTH_SHORT).show();
        }
    }

    private void initRfid() {
        try {
            ReaderParameter parameter = readerHelp.GetInventoryPatameter();
            int readerType = readerHelp.GetReaderType();

            // Verifica se o readerType é válido
            if (readerType == -1) {
                throw new Exception("Falha ao obter o tipo de leitor");
            }

            if (readerType == 33 || readerType == 40 || readerType == 35 || readerType == 55 || readerType == 54) {
                parameter.Session = 1;
            } else if (readerType == 112 || readerType == 113 || readerType == 49) {
                parameter.Session = 254;
            } else if (readerType == 97 || readerType == 99 || readerType == 101 || readerType == 102) {
                parameter.Session = 1;
            } else {
                parameter.Session = 0;
            }
            //Toast.makeText(MainScreenActivity.this, "Rfid conectado", Toast.LENGTH_SHORT).show();
            readerHelp.SetInventoryPatameter(parameter);
            Log.d("RFID", "RFID inicializado com sucesso");


        } catch (Exception e) {
            Log.e("RFID", "Erro ao inicializar RFID", e);
            Toast.makeText(MainScreenActivity.this, "Erro ao inicializar o RFID", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(MainScreenActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void initGpio() {
        // Ativa GPIO conforme necessário
        // Substitua pelo método apropriado para o seu caso
        OtgUtils.set53GPIOEnabled(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        readerHelp.DisConnect();
        // Desativa GPIO e cancela o registro do BroadcastReceiver
        // Substitua pelo método apropriado para o seu caso
        OtgUtils.set53GPIOEnabled(false);
        unregisterReceiver(mVirtualKeyListenerBroadcastReceiver);
    }
}
