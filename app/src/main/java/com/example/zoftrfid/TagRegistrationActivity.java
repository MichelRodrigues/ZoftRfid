package com.example.zoftrfid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TagRegistrationActivity extends AppCompatActivity {

    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, resetIcon;
    private FloatingActionButton fabAdd;
    private View fieldsContainer;
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
                fabAdd.setVisibility(View.GONE);
                fieldsContainer.setVisibility(View.VISIBLE);
                btnVincular.setVisibility(View.VISIBLE);
                // Foco na primeira caixa de texto
                etProductCode.requestFocus();
            }
        });

        // Configuração do clique no botão Vincular
        btnVincular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibir uma mensagem de Toast
                showToast("Não foi possível vincular");
            }
        });

        resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpar o conteúdo das caixas de texto
                etProductCode.setText("");
                etDescription.setText("");
                etTags.setText("");
                // Foco na primeira caixa de texto
                etProductCode.requestFocus();
            }
        });


        etProductCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método é chamado antes de qualquer alteração no texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método é chamado quando o texto está sendo alterado
                if (s.length() > 0) {
                    // Mostrar o ícone reset
                    resetIcon.setVisibility(View.VISIBLE);
                } else {
                    // Ocultar o ícone reset
                    resetIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método é chamado após o texto ter sido alterado
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
        btnVincular = findViewById(R.id.btnVincular); // ajuste aqui para o ID correto
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
}
