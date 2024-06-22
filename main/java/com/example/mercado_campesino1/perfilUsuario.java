package com.example.mercado_campesino1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class perfilUsuario extends AppCompatActivity {

    Button btncerrar;
    Button SubirArchivo;
    ImageButton btn;
    ImageButton btnMapa;
    TextView userNameTextView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        btnMapa = findViewById(R.id.btnmapa);
        btn = findViewById(R.id.verArchivos);
        SubirArchivo = findViewById(R.id.buttonSubir);
        btncerrar = findViewById(R.id.btnCerrar);
        userNameTextView = findViewById(R.id.textUsuario);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), mapa.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Archivos.class);
                startActivity(intent);
            }
        });

        SubirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubirArchivo.class);
                startActivity(intent);
            }
        });

        btncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(perfilUsuario.this, MainActivity.class));
                finish();
            }
        });

        // Obtener el ID del usuario actual
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Consultar Firestore para obtener los datos del usuario
        db.collection("Users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("user");
                    userNameTextView.setText(userName);
                } else {
                    Toast.makeText(perfilUsuario.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(perfilUsuario.this, "Error al consultar Firestore", Toast.LENGTH_SHORT).show();
            }
            });
        }
    }