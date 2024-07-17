package com.example.zoftrfid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemQueryActivity extends AppCompatActivity {
    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, iconRegistration, ivSearchIcon, voltarIcon;
    private EditText etSearch, etPedidoNumero;
    private LinearLayout fieldsContainer2;
    private TableLayout itemTable;
    private boolean isSearchEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_query);

        // Inicialização dos elementos da UI
        initViews();

        // Configuração dos cliques nos ícones do rodapé
        setupFooterIcons();

        // Configuração dos eventos de foco e texto na caixa de pesquisa
        setupSearchBox();
    }

    // Método para inicializar elementos da UI
    private void initViews() {
        homeIcon = findViewById(R.id.icon_home);
        searchIcon = findViewById(R.id.icon_search);
        queryIcon = findViewById(R.id.icon_query);
        inventoryIcon = findViewById(R.id.icon_inventory);
        iconRegistration = findViewById(R.id.icon_registration);
        etSearch = findViewById(R.id.etSearch);
        ivSearchIcon = findViewById(R.id.ivSearchIcon);
        voltarIcon = findViewById(R.id.voltarIcon);
        fieldsContainer2 = findViewById(R.id.fieldsContainer2);
        etPedidoNumero = findViewById(R.id.etPedidoNumero);
        itemTable = findViewById(R.id.tblItensPedido);

        // Configura a entrada para aceitar apenas números
        etSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    // Método para configurar cliques nos ícones do rodapé
    private void highlightIconAndDisable(ImageView icon) {
        icon.setBackgroundResource(R.drawable.rounded_background); // Aplicar o fundo ovalado
        icon.setEnabled(false); // Desabilitar o ícone
        icon.setClickable(false); // Tornar o ícone não clicável
    }

    private void setupFooterIcons() {
        // Destacar e desabilitar o ícone de registro, pois estamos na TagRegistrationActivity
        highlightIconAndDisable(queryIcon);

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemQueryActivity.this, MainScreenActivity.class));
            }
        });

        iconRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemQueryActivity.this, TagRegistrationActivity.class));
            }
        });

        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemQueryActivity.this, InventoryActivity.class));
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemQueryActivity.this, ProductSearchActivity.class));
            }
        });
    }

    // Método para configurar eventos na caixa de pesquisa
    private void setupSearchBox() {
        etSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && isSearchEmpty) {
                    etSearch.setHint("Busque aqui o pedido de venda");
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSearchEmpty = s.length() == 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ivSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = etSearch.getText().toString().trim();
                etPedidoNumero.setText(searchText); // Define o número do pedido
                etPedidoNumero.setEnabled(false); // Desabilita a edição do número do pedido

                if (searchText.equals("123")) {
                    loadItemTableWithExample(); // Carrega a tabela de itens com o exemplo
                } else {
                    clearItemTable(); // Limpa a tabela de itens
                }

                // Restante da lógica permanece como está
                fieldsContainer2.setVisibility(View.VISIBLE);
                voltarIcon.setVisibility(View.VISIBLE);
                findViewById(R.id.searchContainer).setVisibility(View.GONE);
            }
        });


        voltarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldsContainer2.setVisibility(View.GONE);
                voltarIcon.setVisibility(View.GONE);
                findViewById(R.id.searchContainer).setVisibility(View.VISIBLE);
                etSearch.setText(""); // Limpa a caixa de pesquisa
                etPedidoNumero.setText(""); // Limpa o número do pedido
                etPedidoNumero.setEnabled(true); // Habilita a edição do número do pedido (se necessário)
                clearItemTable(); // Limpar a tabela de itens
            }
        });
    }

    // Método para carregar a tabela de itens com o exemplo dado
    private void loadItemTableWithExample() {
        itemTable.setVisibility(View.VISIBLE); // Garante que a tabela seja visível

        itemTable.removeAllViews(); // Limpa qualquer conteúdo existente na tabela

        String[][] exampleItems = {
                {"1", "1457650", "JBL, Caixa de Som", "✅"},
                {"2", "125673", "Gamepad para Celular", "✅"},
                {"3", "3541564", "Película de Nano Vidro", "✅"},
                {"4", "205689", "Carregador Universal Ultra", "❌"},
                {"5", "739845", "Cabo USB-C em nylon 1,5 m", "❌"},
                {"6", "45563", "Suporte de Mesa Para Celular 360", "✅"},
                {"7", "304346", "Capa Samsung Galaxy S20", "❌"},
                {"8", "143231", "Fone Bluetooth Sem Fio tws", "✅"},
                {"9", "13425", "Capa Para iPhone 15 Pro Max", "✅"},
                {"10", "43583", "Power Bank 20000mAh Portátil", "❌"}
        };

        for (String[] item : exampleItems) {
            TableRow row = new TableRow(this);

            TextView tv1 = new TextView(this);
            tv1.setText(item[0]);
            row.addView(tv1);

            TextView tv2 = new TextView(this);
            tv2.setText(item[1]);
            row.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setText(item[2]);
            row.addView(tv3);

            TextView tv4 = new TextView(this);
            tv4.setText(item[3]);
            row.addView(tv4);

            itemTable.addView(row);
        }
    }

    private void clearItemTable() {
        itemTable.setVisibility(View.GONE); // Oculta a tabela
        itemTable.removeAllViews(); // Remove todas as linhas da tabela
    }

}
