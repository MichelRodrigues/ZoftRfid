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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TagRegistrationActivity extends AppCompatActivity {

    private static final String TOAST_CR_DETECTADO = "CR detectado";
    private static final String TOAST_INSERIR_CODIGO_VALIDO = "Por favor, insira um código válido.";
    private static final String TOAST_INSERIR_NUMERO_VALIDO = "Por favor, insira um número válido.";
    private static final String TOAST_NAO_FOI_POSSIVEL_VINCULAR = "Não foi possível vincular";
    private static final String TOAST_BUSCANDO_DADOS = "Buscando dados...";

    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, resetIcon, iconRegistration, btnVoltar;
    private FloatingActionButton fabAdd;
    private LinearLayout fieldsContainer, fieldsContainer2;
    private Button btnVincular;
    private EditText etProductCode, etDescription, etTags;
    private EditText etNotaNumero, etItensNota;
    private boolean isNotaNumeroEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_registration);

        initViews();
        setupFooterIcons();
        setupListeners();
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
        etItensNota = findViewById(R.id.etItensNota);

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
        btnVoltar.setOnClickListener(v -> goToInitialScreen());
        resetIcon.setOnClickListener(v -> resetFields());

        etProductCode.addTextChangedListener(createTextWatcher());
        etProductCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (etProductCode.getText().length() > 0 && !hasFocus) {
                resetIcon.setVisibility(View.VISIBLE);
                showToast(TOAST_BUSCANDO_DADOS);
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
            etProductCode.requestFocus();
            fieldsContainer2.setVisibility(View.GONE);
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
                    etNotaNumero.setText(notaNumero);
                    isNotaNumeroEmpty = false;
                    fabAdd.setVisibility(View.GONE);
                    fieldsContainer2.setVisibility(View.VISIBLE);
                    fieldsContainer.setVisibility(View.GONE);
                    resetIcon.setVisibility(View.VISIBLE);
                    btnVoltar.setVisibility(View.VISIBLE);
                    etNotaNumero.requestFocus();
                    showToast(TOAST_BUSCANDO_DADOS);
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
        if (etProductCode.getText().length() > 0 || etNotaNumero.getText().length() > 0) {
            resetIcon.setVisibility(View.VISIBLE);
        } else {
            resetIcon.setVisibility(View.GONE);
        }
    }

    private void resetFields() {
        etProductCode.setText("");
        etDescription.setText("");
        etTags.setText("");
        etNotaNumero.setText("");
        etItensNota.setText("");
        isNotaNumeroEmpty = true;

        if (fieldsContainer2.getVisibility() == View.VISIBLE) {
            etNotaNumero.requestFocus();
        } else {
            etProductCode.requestFocus();
        }
    }

    private void goToInitialScreen() {
        etProductCode.setText("");
        etDescription.setText("");
        etTags.setText("");
        etNotaNumero.setText("");
        etItensNota.setText("");
        fieldsContainer.setVisibility(View.GONE);
        fieldsContainer2.setVisibility(View.GONE);
        resetIcon.setVisibility(View.GONE);
        btnVoltar.setVisibility(View.GONE);
        etProductCode.requestFocus();
        fabAdd.setVisibility(View.VISIBLE);
    }

    private void highlightIconAndDisable(ImageView icon) {
        icon.setBackgroundResource(R.drawable.rounded_background);
        icon.setEnabled(false);
        icon.setClickable(false);
    }
}
