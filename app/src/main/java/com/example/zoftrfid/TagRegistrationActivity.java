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
    private LinearLayout fieldsContainer;
    private Button btnVincular;
    private EditText etProductCode, etDescription, etTags;

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
                showToast("Não foi possível vincular");
            }
        });

        resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etProductCode.setText("");
                etDescription.setText("");
                etTags.setText("");
                etProductCode.requestFocus();
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
        btnVincular = findViewById(R.id.btnVincular);
        etProductCode = findViewById(R.id.etProductCode);
        etDescription = findViewById(R.id.etDescription);
        etTags = findViewById(R.id.etTags);
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
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
            }
        });
        builder.show();
    }

    // Método para exibir o pop-up da Nota de Entrada
    private void showNotaEntradaPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o número da Nota:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Função indisponível no momento");
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fabAdd.setVisibility(View.VISIBLE);
                fieldsContainer.setVisibility(View.GONE);
                btnVincular.setVisibility(View.GONE);
            }
        });
        builder.show();
    }
}
