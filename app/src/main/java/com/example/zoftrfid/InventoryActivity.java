package com.example.zoftrfid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private ImageView homeIcon, searchIcon, queryIcon, inventoryIcon, iconRegistration, voltarIcon;
    private boolean isReportSaved = false; // Flag para indicar se o relatório atual foi salvo

    private View fieldsContainer2; // Variável para armazenar o fieldsContainer2
    private View fabAddButton; // Variável para armazenar o fab_add


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

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
        voltarIcon = findViewById(R.id.voltarIcon);
        fieldsContainer2 = findViewById(R.id.fieldsContainer2); // Inicialização do fieldsContainer2
        fabAddButton = findViewById(R.id.fab_add); // Inicialização do fab_add

        // Inicialmente, mostrar o fab_add e a lista de registros
        fabAddButton.setVisibility(View.VISIBLE);


        // Inicialmente, ocultar o fieldsContainer2, voltarIcon e btn_save
        fieldsContainer2.setVisibility(View.GONE);
        voltarIcon.setVisibility(View.GONE);
        findViewById(R.id.btn_save).setVisibility(View.GONE);


        // Configuração do clique no botão fab_add
        fabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartInventoryDialog();
            }
        });
    }

    // Método para configurar cliques nos ícones do rodapé
    private void setupFooterIcons() {
        // Destacar e desabilitar o ícone de inventário
        highlightIconAndDisable(inventoryIcon);

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InventoryActivity.this, MainScreenActivity.class));
            }
        });

        iconRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InventoryActivity.this, TagRegistrationActivity.class));
            }
        });

        queryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InventoryActivity.this, ItemQueryActivity.class));
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InventoryActivity.this, ProductSearchActivity.class));
            }
        });

        voltarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // Método para destacar e desabilitar ícones do rodapé
    private void highlightIconAndDisable(@NonNull ImageView icon) {
        icon.setBackgroundResource(R.drawable.rounded_background); // Aplicar o fundo ovalado
        icon.setEnabled(false); // Desabilitar o ícone
        icon.setClickable(false); // Tornar o ícone não clicável
    }


    // Método para mostrar o diálogo de confirmação ao iniciar um novo inventário
    private void showStartInventoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja iniciar um novo Inventário?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startNewInventory();
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Método para iniciar um novo inventário
    private void startNewInventory() {
        fieldsContainer2.setVisibility(View.VISIBLE);
        voltarIcon.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
        Toast.makeText(this, "Buscando dados...", Toast.LENGTH_SHORT).show();

        fabAddButton.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (fieldsContainer2.getVisibility() == View.VISIBLE) {
            if (!isReportSaved) {
                showSaveConfirmationDialog();
            } else {
                // Se o relatório foi salvo, voltar ao estado inicial
                revertToInitialState();
            }
        } else {
            super.onBackPressed();
        }
    }

    // Método para mostrar o diálogo de confirmação ao sair sem salvar o relatório
    private void showSaveConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja salvar o registro antes de sair?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveReport();
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                revertChanges();
            }
        });
        builder.show();
    }

    // Método para salvar o relatório
    private void saveReport() {
        isReportSaved = true;
        Toast.makeText(this, "Registro salvo com sucesso!", Toast.LENGTH_SHORT).show();
        revertToInitialState();
    }

    // Método para reverter as mudanças ao clicar em "NÃO" no diálogo de confirmação
    private void revertChanges() {
        // Limpar campos, se necessário
        // Aqui você pode adicionar lógica para limpar os campos da tabela, se houver
        revertToInitialState();
    }

    // Método para voltar ao estado inicial
    private void revertToInitialState() {
        fieldsContainer2.setVisibility(View.GONE);
        findViewById(R.id.btn_save).setVisibility(View.GONE);
        voltarIcon.setVisibility(View.GONE);
        fabAddButton.setVisibility(View.VISIBLE);

    }
}
