package com.example.zoftrfid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TagRegistrationActivity extends AppCompatActivity {

    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, resetIcon;
    private FloatingActionButton fabAdd;
    private LinearLayout fieldsContainer,fieldsContainer2;
    private Button btnVincular, btnVoltar;
    private EditText etProductCode, etDescription, etTags;

    private EditText etNotaNumero, etItensNota;

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
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implemente aqui a ação de voltar para a tela inicial
                goToInitialScreen();
            }
        });

        resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etProductCode.setText("");
                etDescription.setText("");
                etTags.setText("");
                etNotaNumero.setText("");
                etItensNota.setText("");// Reset também para Número da Nota
                // Manter o foco no campo Número da Nota se estiver visível
                if (fieldsContainer2.getVisibility() == View.VISIBLE) {
                    etNotaNumero.requestFocus();
                } else {
                    etProductCode.requestFocus();
                }

            }
        });

        // Monitorar alterações no campo de código do produto
        etProductCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    resetIcon.setVisibility(View.VISIBLE);
                } else {
                    resetIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Monitorar alterações no campo Número da Nota
        etNotaNumero.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    resetIcon.setVisibility(View.VISIBLE);
                } else {
                    resetIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
        btnVoltar = findViewById(R.id.btnVoltar);
        // Inicialização dos novos campos
        etNotaNumero = findViewById(R.id.etNotaNumero);
        etItensNota = findViewById(R.id.etItensNota);
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
                btnVincular.setVisibility(View.VISIBLE);
                etProductCode.requestFocus();
                // Oculta fieldsContainer2 se estiver visível
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
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Captura o número da nota inserido pelo usuário
                String numeroNota = input.getText().toString();
                // Configura o número da nota no campo correspondente
                etNotaNumero.setText(numeroNota);

                // Esconder o botão flutuante e outros elementos
                fabAdd.setVisibility(View.GONE);
                fieldsContainer2.setVisibility(View.VISIBLE);
                fieldsContainer.setVisibility(View.GONE);
                resetIcon.setVisibility(View.GONE);

                // Focar no campo Número da Nota
                etNotaNumero.requestFocus();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fabAdd.setVisibility(View.VISIBLE);
                fieldsContainer.setVisibility(View.GONE);
                btnVincular.setVisibility(View.GONE);
                // Oculta fieldsContainer2 se estiver visível
                fieldsContainer2.setVisibility(View.GONE);
            }
        });
        builder.show();
    }
    private void goToInitialScreen() {
        if (fieldsContainer.getVisibility() == View.VISIBLE) {
            fieldsContainer.setVisibility(View.GONE);
        }
        if (fieldsContainer2.getVisibility() == View.VISIBLE) {
            fieldsContainer2.setVisibility(View.GONE);
        }
        // Exibir o botão flutuante e ocultar o botão Voltar se estiver visível
        fabAdd.setVisibility(View.VISIBLE);
    }

}
