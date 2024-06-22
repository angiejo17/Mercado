package com.example.mercado_campesino1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class inicioSesion extends AppCompatActivity {

    EditText correoLogin;
    EditText contraLogin;
    Button btnLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        correoLogin = findViewById(R.id.editText_correoInicio);
        contraLogin = findViewById(R.id.editText_contraInicio);
        btnLogin = findViewById(R.id.btn_inicioInicio);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String emailLogin = correoLogin.getText().toString().trim();
        String passwordLogin = contraLogin.getText().toString().trim();

        if(emailLogin.isEmpty() || passwordLogin.isEmpty()){
            Toast.makeText(inicioSesion.this, "Por favor ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent i = new Intent(inicioSesion.this, perfilUsuario.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(inicioSesion.this, "El correo o Contraseña son incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(inicioSesion.this, perfilUsuario.class));
            finish();
        }
    }
}