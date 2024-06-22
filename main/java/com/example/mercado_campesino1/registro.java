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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registro extends AppCompatActivity {
    EditText usuario;
    EditText correoRegister;
    EditText ContraRegister;
    EditText confirmarConra;
    Button btnRegister;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuario = findViewById(R.id.editTex_usuario);
        correoRegister = findViewById(R.id.editText_correo);
        ContraRegister = findViewById(R.id.editText_contra);
        confirmarConra = findViewById(R.id.editText_Conficontra);
        btnRegister = findViewById(R.id.btn_registrar);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {
        String user = usuario.getText().toString();
        String email = correoRegister.getText().toString();
        String password = ContraRegister.getText().toString();
        String confirmpassword = confirmarConra.getText().toString();

        if (!user.isEmpty() && !email.isEmpty() && !password.isEmpty() &&!confirmpassword.isEmpty()){
            if (isEmailValid(email)){
                if (password.equals(confirmpassword)){
                    if(password.length() >= 6){
                        createUser(user,email,password);
                    }
                    else{
                        Toast.makeText(this, "La contraseña debe tener almenos 6 caracteres >:/", Toast.LENGTH_SHORT).show();
                    }
                   
                }
                else{
                    Toast.makeText(this, "La contraseña no coinciden :/ ", Toast.LENGTH_SHORT).show();

                }

            }
            else{
                Toast.makeText(this, "El Correo no es valido :(  ", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Para continuar inserta todos los datos ", Toast.LENGTH_SHORT).show();
        }


    }

    private void createUser(String user, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   String id = mAuth.getCurrentUser().getUid();
                   Map<String, Object> map = new HashMap<>();
                   map.put("user", user);
                   map.put("email", email);
                   map.put("password", password);
                   mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent i = new Intent(registro.this, inicioSesion.class);
                                startActivity(i);
                                Toast.makeText(registro.this, "El suario se almaceno correctamente", Toast.LENGTH_SHORT).show();
                            }
                       }
                   });

               }
               else{
                   Toast.makeText(registro.this, "No se pudo registrar el usuario :C", Toast.LENGTH_SHORT).show();

               }
            }
        });
    }

    private boolean isEmailValid(String email) {
        String expression ="^[\\w-\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}