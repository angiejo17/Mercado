package com.example.mercado_campesino1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button irAregistro;
    Button irAinicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        irAregistro = findViewById(R.id.btn_registro);
        irAinicio = findViewById(R.id.btn_inicio);

        irAinicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAinicio();
            }

        });
        irAregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAregistro();
            }
        });
    }
    public void irAinicio(){
        Intent intent = new Intent(this, inicioSesion.class);
        startActivity(intent);
    }
    public void irAregistro(){
        Intent intent = new Intent(this, registro.class);
        startActivity(intent);
    }

}