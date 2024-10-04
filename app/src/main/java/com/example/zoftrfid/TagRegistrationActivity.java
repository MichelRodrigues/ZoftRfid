package com.example.zoftrfid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rfid.trans.ReaderHelp;

import org.json.JSONException;
import org.json.JSONObject;

import com.rfid.trans.OtgUtils;
import com.rfid.trans.ReadTag;
import com.rfid.trans.ReaderHelp;
import com.rfid.trans.ReaderParameter;
import com.rfid.trans.TagCallback;
import java.util.HashSet;

import android.media.ToneGenerator;
import android.media.AudioManager;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


import java.io.IOException;

import java.util.HashSet;
import java.util.Random;
import android.content.SharedPreferences;


public class TagRegistrationActivity extends AppCompatActivity {

    private static final String TOAST_CR_DETECTADO = "CR detectado";
    private static final String TOAST_INSERIR_CODIGO_VALIDO = "Por favor, insira um código válido.";
    private static final String TOAST_INSERIR_NUMERO_VALIDO = "Por favor, insira um número válido.";
    private static final String TOAST_NAO_FOI_POSSIVEL_VINCULAR = "Não foi possível vincular";
    private static final String TOAST_BUSCANDO_DADOS = "Buscando dados...";
    private static final String TOAST_NOTA_N_ENCONTRADA = "Nota não encontrada com esse número.";
    private static final String TOAST_ITENS_INSERIDOS = "Itens inseridos com sucesso!";

    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, resetIcon, iconRegistration, btnVoltar;
    private FloatingActionButton fabAdd;
    private LinearLayout fieldsContainer, fieldsContainer2;
    private Button btnVincular;
    private EditText etProductCode, etDescription, etTags;
    private EditText etNotaNumero;
    private boolean isNotaNumeroEmpty = true;
    private boolean isRowTouched = false;
    private OkHttpClient client;
    private boolean keyPress = false; // Variável para controlar o estado da tecla
    private boolean isStopThread = false;
    private TextView tvTotalTags;
    private ReaderHelp readerHelp;
    private int cardNumber = 0;  // Adicione esta variável para contar as tags
    private HashSet<String> readTags;  // Conjunto para armazenar tags lidas
    private SeekBar seekBarPower;
    private final int MAX_POWER = 33;  // Máximo valor de potência (33)
    private ToneGenerator toneGenerator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_registration);

        initViews();
        setupFooterIcons();
        setupListeners();

        client = new OkHttpClient();

        showToast("Aperte + para iniciar um novo cadastro");

        // Inicializa a instância do ReaderHelp
        readerHelp = new ReaderHelp();

        readTags = new HashSet<>();  // Inicializa o
        seekBarPower = findViewById(R.id.seekBarPower);

        seekBarPower.setProgress(MAX_POWER);

        // Configura o valor de potência inicial para o máximo assim que o app abrir
        setReaderPower(MAX_POWER);

        // Define o comportamento do SeekBar ao ser deslizado
        seekBarPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Chama a função SetRfPower com o valor do SeekBar (progress)
                setReaderPower(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Não precisamos de nada aqui por enquanto
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Quando o usuário soltar o SeekBar, você pode realizar outra ação, se necessário
            }
        });


        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        tvTotalTags = findViewById(R.id.tvTotal);
    }

    private void initViews() {
        homeIcon = findViewById(R.id.icon_home);
        searchIcon = findViewById(R.id.icon_search);
        queryIcon = findViewById(R.id.icon_query);
        inventoryIcon = findViewById(R.id.icon_inventory);
        iconRegistration = findViewById(R.id.icon_registration);
        resetIcon = findViewById(R.id.resetIcon);
        fabAdd = findViewById(R.id.fab_add);
        fieldsContainer = findViewById(R.id.fieldsContainer);
        fieldsContainer2 = findViewById(R.id.fieldsContainer2);
        btnVincular = findViewById(R.id.btnVincular);
        etProductCode = findViewById(R.id.etProductCode);
        etDescription = findViewById(R.id.etDescription);
        etTags = findViewById(R.id.etTags);
        btnVoltar = findViewById(R.id.voltarIcon);
        etNotaNumero = findViewById(R.id.etNotaNumero);

        SpannableString spannableHint = new SpannableString(" Toque aqui para inserir");
        spannableHint.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etNotaNumero.setHint(spannableHint);

        SpannableString spannableProductHint = new SpannableString(" Toque aqui para inserir ou use o leitor");
        spannableProductHint.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableProductHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etProductCode.setHint(spannableProductHint);
    }
    private void setReaderPower(int power) {
        try {
            // Bit7 = 0 indica que a configuração deve ser salva após o desligamento
            // Para fazer isso, usamos bitwise OR para garantir que o Bit7 seja 0
            int powerConfig = power & 0x7F;  // Garante que o Bit7 seja 0

            // Chama a função SetRfPower do ReaderHelp com o valor de potência ajustado
            int result = readerHelp.SetRfPower((byte) powerConfig);

            if (result == 0) {
                Log.d("RFID", "Potência ajustada com sucesso: " + power);
            } else {
                Log.e("RFID", "Erro ao ajustar potência, código: " + result);
            }
        } catch (Exception e) {
            Log.e("RFID", "Erro ao ajustar a potência do leitor RFID", e);
        }
    }

    private void setupFooterIcons() {
        highlightIconAndDisable(iconRegistration);

        homeIcon.setOnClickListener(v -> startActivity(new Intent(TagRegistrationActivity.this, MainScreenActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(TagRegistrationActivity.this, ProductSearchActivity.class)));
        queryIcon.setOnClickListener(v -> startActivity(new Intent(TagRegistrationActivity.this, ItemQueryActivity.class)));
        inventoryIcon.setOnClickListener(v -> startActivity(new Intent(TagRegistrationActivity.this, InventoryActivity.class)));
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showConfirmationPopup());
        btnVincular.setOnClickListener(v -> showToast(TOAST_NAO_FOI_POSSIVEL_VINCULAR));
        btnVoltar.setOnClickListener(v -> {
            if (isRowTouched) {
                goToPreviousScreen();
            } else {
                goToInitialScreen();
            }
        });
        resetIcon.setOnClickListener(v -> {
            resetFields();  // Chama resetFields que agora inclui a limpeza da tabela
        });

        etProductCode.addTextChangedListener(createTextWatcher());
        etProductCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (etProductCode.getText().length() > 0 && !hasFocus) {
                resetIcon.setVisibility(View.VISIBLE);
                fetchProductData(etProductCode.getText().toString().trim());

                etProductCode.setEnabled(false);
            }
        });




        etNotaNumero.addTextChangedListener(createTextWatcher());
        etNotaNumero.setOnClickListener(v -> showNotaEntradaPopup());
        etProductCode.setOnClickListener(v -> showProductCodePopup());
    }

    private TextWatcher createTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                Log.d("EditTextWatcher", "Text changed: " + text);

                if (text.contains("\r") || text.contains("\n")) {
                    showToast(TOAST_CR_DETECTADO);
                }

                if (!text.isEmpty()) {
                    etProductCode.clearFocus();
                    hideKeyboard(etProductCode);
                }

                handleResetIconVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showConfirmationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja adicionar itens através de uma Nota de Entrada?");
        builder.setPositiveButton("SIM", (dialog, which) -> showNotaEntradaPopup());
        builder.setNegativeButton("NÃO", (dialog, which) -> {
            fabAdd.setVisibility(View.GONE);
            fieldsContainer.setVisibility(View.VISIBLE);
            btnVoltar.setVisibility(View.VISIBLE);
            btnVincular.setVisibility(View.VISIBLE);
            etProductCode.setEnabled(true);
            etProductCode.requestFocus();
            etDescription.setFocusable(false); // Evita focar o campo diretamente com toque
            fieldsContainer2.setVisibility(View.GONE);
            etProductCode.setText("");
            isRowTouched = false;
            etTags.setShowSoftInputOnFocus(false); // Desativa o teclado ao tocar no campo
            etProductCode.setShowSoftInputOnFocus(false); // Desativa o teclado ao tocar no campo
            //etDescription.setShowSoftInputOnFocus(false); // Desativa o teclado ao tocar no campo;
        });
        builder.show();
    }

    private void showNotaEntradaPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o número da Nota:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            hideKeyboard(input);
            dialog.dismiss();
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(view -> {
                String notaNumero = input.getText().toString().trim();
                if (notaNumero.isEmpty()) {
                    showToast(TOAST_INSERIR_NUMERO_VALIDO);
                } else {
                    if (notaNumero.equals("123")) {
                        populateTableWithExampleData();
                        showToast(TOAST_ITENS_INSERIDOS);
                    } else {
                        clearTable();
                        showToast(TOAST_NOTA_N_ENCONTRADA);
                    }
                    etNotaNumero.setText(notaNumero);
                    isNotaNumeroEmpty = false;
                    fabAdd.setVisibility(View.GONE);
                    fieldsContainer2.setVisibility(View.VISIBLE);
                    fieldsContainer.setVisibility(View.GONE);
                    resetIcon.setVisibility(View.VISIBLE);
                    btnVoltar.setVisibility(View.VISIBLE);
                    etNotaNumero.requestFocus();
                    hideKeyboard(input);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void showProductCodePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o código do produto:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            hideKeyboard(input);
            dialog.dismiss();
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(view -> {
                String productCode = input.getText().toString().trim();
                if (productCode.isEmpty()) {
                    showToast(TOAST_INSERIR_CODIGO_VALIDO);
                } else {
                    etProductCode.setText(productCode);
                    fabAdd.setVisibility(View.GONE);
                    fieldsContainer.setVisibility(View.VISIBLE);
                    fieldsContainer2.setVisibility(View.GONE);
                    btnVoltar.setVisibility(View.VISIBLE);
                    resetIcon.setVisibility(View.VISIBLE);
                    etProductCode.requestFocus();
                    fetchProductData(productCode);
                    hideKeyboard(input);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
    private void fetchProductData(String codigoBarra) {
        etTags.requestFocus(); // Foca na caixa de texto etTags

        // Recuperar o token dos SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("authToken", null);

        if (token != null) {
            // Criar uma nova instância de Request para cada requisição
            String url = "http://177.234.156.125:9000/produto/" + codigoBarra;

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Log.e("FetchProductData", "Erro na requisição: " + e.getMessage());
                        showToast("Erro ao buscar dados do produto.");
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Log.d("FetchProductData", "Resposta do servidor: " + responseBody);

                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String descricao = jsonResponse.getString("descricaodetalhada");

                                runOnUiThread(() -> {
                                    String currentText = etDescription.getText().toString();
                                    etDescription.setText(currentText + "\n" + descricao);

                                    String requestNumber = null;
                                    showToast("Resposta " + null + " recebida");
                                });
                            } catch (JSONException e) {
                                Log.e("FetchProductData", "Erro ao processar JSON: " + e.getMessage());
                                runOnUiThread(() -> showToast("Erro ao processar dados do produto."));
                            }
                        } else {
                            runOnUiThread(() -> showToast("Erro na resposta do servidor."));
                        }
                    } finally {
                        // Fechar explicitamente a resposta para garantir que a conexão seja liberada
                        if (response.body() != null) {
                            response.body().close();
                        }
                    }
                }
            });

        } else {
            Log.d("TokenDebug", "Nenhum token encontrado.");
            showToast("Token de autenticação não encontrado.");
        }
    }





    private void handleResetIconVisibility() {
        if ((etProductCode.getText().length() > 0 || etNotaNumero.getText().length() > 0 || etTags.getText().length() > 0) && fabAdd.getVisibility() != View.VISIBLE) {
            resetIcon.setVisibility(View.VISIBLE);
        } else {
            resetIcon.setVisibility(View.GONE);
        }
        handleFocus();
    }

    private void resetFields() {
        if (isRowTouched) {
            etTags.setText("");
            etTags.requestFocus();
        } else {
            etProductCode.setText("");
            etDescription.setText("");
            etTags.setText("");
            etNotaNumero.setText("");
            isNotaNumeroEmpty = true;
            etProductCode.setEnabled(true);
            // Limpar a tabela
            clearTable();
        }

        handleFocus();

    }
    private void handleFocus() {
        Log.d("HandleFocus", "fieldsContainer visibility: " + fieldsContainer.getVisibility());
        Log.d("HandleFocus", "fieldsContainer2 visibility: " + fieldsContainer2.getVisibility());
        Log.d("HandleFocus", "isRowTouched: " + isRowTouched);

        if (fieldsContainer.getVisibility() == View.VISIBLE && fieldsContainer2.getVisibility() == View.GONE) {
            if (isRowTouched) {
                Log.d("HandleFocus", "Focusing on etTags");
                etTags.requestFocus();
            } else {
                Log.d("HandleFocus", "Focusing on etProductCode");
                etProductCode.requestFocus();
            }
        } else if (fieldsContainer2.getVisibility() == View.VISIBLE && fieldsContainer.getVisibility() == View.GONE) {
            Log.d("HandleFocus", "Focusing on etNotaNumero");
            etNotaNumero.requestFocus();
        }
    }

    // Método para apagar todos os itens da tabela, exceto o cabeçalho
    private void clearTable() {
        TableLayout tableLayout = findViewById(R.id.tblItensNota);
        int childCount = tableLayout.getChildCount();
        // Verifica se a tabela tem linhas para evitar exceções
        if (childCount > 1) {
            // Remove todas as linhas exceto a primeira (que assumimos ser o cabeçalho)
            tableLayout.removeViews(1, childCount - 1);
        }
    }

    private void goToInitialScreen() {
        etProductCode.setText("");
        etDescription.setText("");
        etTags.setText("");
        etNotaNumero.setText("");
        fieldsContainer.setVisibility(View.GONE);
        fieldsContainer2.setVisibility(View.GONE);
        resetIcon.setVisibility(View.GONE);
        btnVoltar.setVisibility(View.GONE);
        fabAdd.setVisibility(View.VISIBLE);

        etProductCode.setEnabled(false);
    }

    private void goToPreviousScreen() {
        etProductCode.setText("");
        etDescription.setText("");
        etTags.setText("");
        fieldsContainer.setVisibility(View.GONE);
        fieldsContainer2.setVisibility(View.VISIBLE);
        resetIcon.setVisibility(View.GONE);
        btnVoltar.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.GONE);
        resetIcon.setVisibility(View.VISIBLE);
        isRowTouched = false;
        etNotaNumero.requestFocus();
        etTags.setEnabled(true);

        etProductCode.setEnabled(false);
    }

    private void highlightIconAndDisable(ImageView icon) {
        icon.setBackgroundResource(R.drawable.rounded_background);
        icon.setEnabled(false);
        icon.setClickable(false);
    }

    // Método para popular a tabela com dados de exemplo
    private void populateTableWithExampleData() {
        clearTable(); // Limpa a tabela antes de popular com os dados de exemplo

        TableLayout tableLayout = findViewById(R.id.tblItensNota);

        String[][] exampleData = {
                {"1", "JBL, Caixa de Som, Bluetooth, Go - Preta", " 10", " "},
                {"2", "Gamepad para Celular - Controle Sem Fio Bluetooth", " 12", " "},
                {"3", "Película de Nano Vidro para Nintendo Switch", " 15", " "},
                {"4", "Carregador Universal Ultra Rápido Duo, 1 X USB-C Power Delivery 20W", " 20", " "},
                {"5", "Cabo USB-C em nylon 1,5 m EUAC 15NB Branco Intelbras", " 7", " "},
                {"6", "Suporte de Mesa Para Celular 360 graus Ajustável Articulado", " 5", " "},
                {"7", "Capa Samsung Galaxy S20 - Transparente", "30", " "},
                {"8", "Fone De Ouvido Bluetooth Sem Fio tws", " 11", " "},
                {"9", "Capa Para iPhone 15 Pro Max Liquid Crystal Clear Spigen - Original", " 15", " "},
                {"10", "Power Bank 20000mAh Portátil com Display LED para iPhone e Android", "3", " "}
        };

        for (int i = 0; i < exampleData.length; i++) {
            final int index = i;
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            // Define click listener for the row
            row.setOnClickListener(v -> {
                etProductCode.setText(generateRandomBarcode());
                etDescription.setText(exampleData[index][1]);
                fabAdd.setVisibility(View.GONE);
                fieldsContainer.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.VISIBLE);
                btnVincular.setVisibility(View.VISIBLE);
                etProductCode.setEnabled(false);
                etDescription.setEnabled(false);
                fieldsContainer2.setVisibility(View.GONE);
                isRowTouched = true;
                Log.d("RowClickListener", "isRowTouched set to true");

                handleResetIconVisibility();
                handleFocus();

            });

            // Create TextView for each cell and set fixed width
            TextView tvNumero = new TextView(this);
            tvNumero.setText(exampleData[i][0]);
            tvNumero.setPadding(dpToPx(0), dpToPx(8), dpToPx(8), dpToPx(8));
            tvNumero.setWidth(dpToPx(30)); // Set fixed width in dp
            row.addView(tvNumero);

            TextView tvDescricao = new TextView(this);
            tvDescricao.setText(exampleData[i][1]);
            tvDescricao.setPadding(dpToPx(0), dpToPx(8), dpToPx(8), dpToPx(8));
            tvDescricao.setWidth(dpToPx(200)); // Set fixed width in dp
            row.addView(tvDescricao);

            TextView tvQtd = new TextView(this);
            tvQtd.setText(exampleData[i][2]);
            tvQtd.setPadding(dpToPx(18), dpToPx(8), dpToPx(8), dpToPx(8));
            tvQtd.setWidth(dpToPx(50)); // Set fixed width in dp
            row.addView(tvQtd);

            TextView tvTAG = new TextView(this);
            tvTAG.setText(exampleData[i][3]);
            tvTAG.setPadding(dpToPx(13), dpToPx(8), dpToPx(8), dpToPx(8));
            tvTAG.setWidth(dpToPx(50)); // Set fixed width in dp
            row.addView(tvTAG);

            tableLayout.addView(row);
        }
    }

    // Auxiliary function to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Método para gerar código de barras aleatório
    private String generateRandomBarcode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
}
