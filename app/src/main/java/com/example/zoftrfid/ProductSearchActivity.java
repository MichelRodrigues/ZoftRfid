package com.example.zoftrfid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ProductSearchActivity extends AppCompatActivity {
    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, iconRegistration;
    private Button btnIniciar, btnLimpar;
    private EditText etSearch, etDescription;
    private ImageView ivSearchIcon, voltarIcon;
    private LinearLayout fieldsContainer;

    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        // Inicialização dos elementos da UI
        initViews();

        // Configuração dos cliques nos ícones do rodapé
        setupFooterIcons();

        // Configuração do comportamento do botão "Iniciar"
        setupUIBehaviors();
    }

    // Método para inicializar elementos da UI
    private void initViews() {
        homeIcon = findViewById(R.id.icon_home);
        searchIcon = findViewById(R.id.icon_search);
        queryIcon = findViewById(R.id.icon_query);
        inventoryIcon = findViewById(R.id.icon_inventory);
        iconRegistration = findViewById(R.id.icon_registration);

        btnIniciar = findViewById(R.id.btnIniciar);
        btnLimpar = findViewById(R.id.btnLimpar);
        etSearch = findViewById(R.id.etSearch);
        ivSearchIcon = findViewById(R.id.ivSearchIcon);
        voltarIcon = findViewById(R.id.voltarIcon);
        fieldsContainer = findViewById(R.id.fieldsContainer);
        etDescription = findViewById(R.id.etDescription);

        // Bloquear a edição da caixa de descrição
        etDescription.setEnabled(false);
    }

    // Método para destacar o ícone e desabilitá-lo
    private void highlightIconAndDisable(ImageView icon) {
        icon.setBackgroundResource(R.drawable.rounded_background); // Aplicar o fundo ovalado
        icon.setEnabled(false); // Desabilitar o ícone
        icon.setClickable(false); // Tornar o ícone não clicável
    }

    private void setupFooterIcons() {
        // Destacar e desabilitar o ícone de registro, pois estamos na TagRegistrationActivity
        highlightIconAndDisable(searchIcon);

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductSearchActivity.this, MainScreenActivity.class));
            }
        });

        iconRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductSearchActivity.this, TagRegistrationActivity.class));
            }
        });

        queryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductSearchActivity.this, ItemQueryActivity.class));
            }
        });

        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductSearchActivity.this, InventoryActivity.class));
            }
        });
    }

    private void setupUIBehaviors() {
        // Alternar entre "Iniciar" e "Parar" ao pressionar o botão "Iniciar"
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    // Código para parar a ação, se necessário
                    btnIniciar.setText("Iniciar");
                } else {
                    // Código para iniciar a ação, se necessário
                    btnIniciar.setText("Parar");
                }
                isRunning = !isRunning; // Alternar o estado
            }
        });

        // Exibir o ícone voltarIcon quando etSearch contiver "123"
        // Exibir o ícone voltarIcon quando etSearch contiver "123"
        ivSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = etSearch.getText().toString().trim();
                if (searchText.equals("123")) {
                    voltarIcon.setVisibility(View.VISIBLE);
                    fieldsContainer.setVisibility(View.VISIBLE);
                    etDescription.setText("Power Bank 20000mAh Portátil com Display LED para iPhone e Android");
                } else {
                    voltarIcon.setVisibility(View.GONE);
                    fieldsContainer.setVisibility(View.GONE);
                    etDescription.setText(""); // Limpar o texto se não for "123"
                }

                // Fechar o teclado e retirar o foco de etSearch
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                etSearch.clearFocus();
            }
        });


        // Limpar etSearch e etDescription ao pressionar o botão "Limpar"
        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                etDescription.setText("");
                voltarIcon.setVisibility(View.GONE);
                fieldsContainer.setVisibility(View.GONE);
            }
        });

        // Ocultar fieldsContainer, limpar etSearch e etDescription ao pressionar voltarIcon
        voltarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                etDescription.setText("");
                voltarIcon.setVisibility(View.GONE);
                fieldsContainer.setVisibility(View.GONE);
            }
        });
    }
}
