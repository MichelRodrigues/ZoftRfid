package com.example.zoftrfid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_registration);

        initViews();
        setupFooterIcons();
        setupListeners();

        showToast("Aperte + para iniciar um novo cadastro");
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
                showToast(TOAST_BUSCANDO_DADOS);
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
            fieldsContainer2.setVisibility(View.GONE);
            etProductCode.setText("");
            isRowTouched = false;
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
                    hideKeyboard(input);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
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
