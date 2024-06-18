package com.example.zoftrfid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ItemQueryActivity extends AppCompatActivity {
    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, iconRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_query);

        // Inicialização dos elementos da UI
        initViews();

        // Configuração dos cliques nos ícones do rodapé
        setupFooterIcons();
    }

    // Método para inicializar elementos da UI
    private void initViews() {
        homeIcon = findViewById(R.id.icon_home);
        searchIcon = findViewById(R.id.icon_search);
        queryIcon = findViewById(R.id.icon_query);
        inventoryIcon = findViewById(R.id.icon_inventory);
        iconRegistration = findViewById(R.id.icon_registration);
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
}