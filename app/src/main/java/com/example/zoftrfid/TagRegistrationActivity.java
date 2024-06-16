package com.example.zoftrfid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.Typeface;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TagRegistrationActivity extends AppCompatActivity {

    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, resetIcon, btnVoltar;
    private FloatingActionButton fabAdd;
    private LinearLayout fieldsContainer, fieldsContainer2;
    private Button btnVincular;
    private EditText etProductCode, etDescription, etTags;
    private EditText etNotaNumero, etItensNota;
    private boolean isNotaNumeroEmpty = true; // Variável para controlar se o campo Número da Nota está vazio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_registration);

        // Inicialização dos elementos da UI
        initViews();

        // Configuração dos cliques nos ícones do rodapé
        setupFooterIcons();

        // Configuração do clique no botão flutuante
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationPopup();
            }
        });

        // Configuração do clique no botão Vincular
        btnVincular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast();
            }
        });

        // Configuração do clique no botão Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implemente aqui a ação de voltar para a tela inicial
                goToInitialScreen();
            }
        });

        // Configuração do clique no ícone de reset
        resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });

        // Monitorar alterações no campo de código do produto
        etProductCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                Log.d("EditTextWatcher", "Text changed: " + text);

                // Verificar se há caractere de retorno de carro (\r) ou nova linha (\n)
                if (text.contains("\r") || text.contains("\n")) {
                    // Mostrar um Toast informando que um CR foi detectado
                    Toast.makeText(TagRegistrationActivity.this, "CR detectado", Toast.LENGTH_SHORT).show();
                }

                if (!text.isEmpty()) {
                    // Remover o foco da caixa de texto
                    etProductCode.clearFocus();

                    // Esconder o teclado, se estiver visível
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etProductCode.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etProductCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (etProductCode.getText().length() > 0 && !hasFocus) {
                // Se perdeu o foco, mostrar o Toast
                resetIcon.setVisibility(View.VISIBLE);
                showToast2();
            }
        });


        // Monitorar alterações no campo Número da Nota
        etNotaNumero.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleResetIconVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Configurar o clique no campo Número da Nota para abrir o pop-up
        etNotaNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotaEntradaPopup();
            }
        });

        // Configurar o clique no campo Código do Produto para abrir o pop-up
        etProductCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductCodePopup(); // Abrir pop-up sempre que a caixa de texto for clicada
            }
        });
    }

    // Método para inicializar elementos da UI
    private void initViews() {
        homeIcon = findViewById(R.id.icon_home);
        searchIcon = findViewById(R.id.icon_search);
        queryIcon = findViewById(R.id.icon_query);
        inventoryIcon = findViewById(R.id.icon_inventory);
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

        // Configurar o hint do etNotaNumero em itálico
        SpannableString spannableHint = new SpannableString(" Toque aqui para inserir");
        spannableHint.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etNotaNumero.setHint(spannableHint);

        // Configurar o hint do etProductCode em itálico
        SpannableString spannableProductHint = new SpannableString(" Toque aqui para inserir ou use o leitor");
        spannableProductHint.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableProductHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etProductCode.setHint(spannableProductHint);
    }

    // Método para configurar cliques nos ícones do rodapé
    private void setupFooterIcons() {
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TagRegistrationActivity.this, MainScreenActivity.class));
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TagRegistrationActivity.this, ProductSearchActivity.class));
            }
        });

        queryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TagRegistrationActivity.this, ItemQueryActivity.class));
            }
        });

        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TagRegistrationActivity.this, InventoryActivity.class));
            }
        });
    }

    // Método para exibir um Toast com a mensagem fornecida
    private void showToast() {
        Toast.makeText(this, "Não foi possível vincular", Toast.LENGTH_SHORT).show();
    }
    private void showToast2() {
        Toast.makeText(this, "Buscando dados...", Toast.LENGTH_SHORT).show();
    }


    // Método para exibir o pop-up de confirmação ao clicar no botão flutuante
    private void showConfirmationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja adicionar itens através de uma Nota de Entrada?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showNotaEntradaPopup();
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fabAdd.setVisibility(View.GONE);
                fieldsContainer.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.VISIBLE);
                btnVincular.setVisibility(View.VISIBLE);
                etProductCode.requestFocus();
                fieldsContainer2.setVisibility(View.GONE);
            }
        });
        builder.show();
    }

    // Método para exibir o pop-up da Nota de Entrada
    private void showNotaEntradaPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o número da Nota:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Apenas números permitidos
        builder.setView(input);

        // Configurar botão OK
        builder.setPositiveButton("OK", null); // Definir o botão OK como null inicialmente

        // Configurar o clique no botão Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Fechar o pop-up ao clicar em Cancelar
            }
        });

        // Criar o diálogo
        final AlertDialog dialog = builder.create();

        // Sobrescrever o clique do botão OK para validação personalizada
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String notaNumero = input.getText().toString().trim();
                        if (notaNumero.isEmpty()) {
                            Toast.makeText(TagRegistrationActivity.this, "Por favor, insira um número válido.", Toast.LENGTH_SHORT).show();
                        } else {
                            etNotaNumero.setText(notaNumero);
                            isNotaNumeroEmpty = false;
                            fabAdd.setVisibility(View.GONE);
                            fieldsContainer2.setVisibility(View.VISIBLE);
                            fieldsContainer.setVisibility(View.GONE);
                            resetIcon.setVisibility(View.VISIBLE); // Mostrar o ícone de reset
                            btnVoltar.setVisibility(View.VISIBLE);
                            etNotaNumero.requestFocus();
                            showToast2();
                            dialog.dismiss(); // Fechar o pop-up apenas se um número válido foi inserido
                        }
                    }
                });
            }
        });

        // Exibir o diálogo
        dialog.show();
    }

    // Método para exibir o pop-up de Código do Produto
    private void showProductCodePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o código do produto:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Permitir apenas números
        builder.setView(input);

        // Configurar botão OK
        builder.setPositiveButton("OK", null); // Definir o botão OK como null inicialmente

        // Configurar o clique no botão Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0); // Esconder o teclado
                dialog.dismiss(); // Fechar o pop-up ao clicar em Cancelar
            }
        });

        // Criar o diálogo
        final AlertDialog dialog = builder.create();

        // Sobrescrever o clique do botão OK para validação personalizada
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String productCode = input.getText().toString().trim();
                        if (productCode.isEmpty()) {
                            Toast.makeText(TagRegistrationActivity.this, "Por favor, insira um código válido.", Toast.LENGTH_SHORT).show();
                        } else {
                            etProductCode.setText(productCode);
                            fabAdd.setVisibility(View.GONE);
                            fieldsContainer.setVisibility(View.VISIBLE);
                            fieldsContainer2.setVisibility(View.GONE);
                            btnVoltar.setVisibility(View.VISIBLE);
                            resetIcon.setVisibility(View.VISIBLE); // Mostrar o ícone de reset
                            etProductCode.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0); // Esconder o teclado
                            dialog.dismiss(); // Fechar o pop-up apenas se um código válido foi inserido
                        }
                    }
                });
            }
        });

        // Exibir o diálogo
        dialog.show();
    }

    // Método para controlar a visibilidade do ícone de reset com base nos campos
    private void handleResetIconVisibility() {
        if (etProductCode.getText().length() > 0 || etNotaNumero.getText().length() > 0) {
            resetIcon.setVisibility(View.VISIBLE);
        } else {
            resetIcon.setVisibility(View.GONE);
        }
    }

    // Método para resetar todos os campos
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

    // Método para voltar para a tela inicial
    private void goToInitialScreen() {
        // Esconder os containers de campos
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

        // Exibir o botão flutuante
        fabAdd.setVisibility(View.VISIBLE);
    }
}


