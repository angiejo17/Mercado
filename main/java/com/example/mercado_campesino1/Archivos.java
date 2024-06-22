package com.example.mercado_campesino1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Archivos extends AppCompatActivity {

    ImageView imageView;
    ImageButton btnperfil;
    ImageButton btnMapa;
    TextView textViewNombreImagen;

    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivos);

        imageView = findViewById(R.id.imageView_primero);
        btnperfil = findViewById(R.id.imageButton_perfil);
        btnMapa = findViewById(R.id.btnmapa);
        textViewNombreImagen = findViewById(R.id.textView_nombreImagen);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("imageInfo");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // No hay usuario autenticado, ir a la pantalla de inicio de sesión
            signOut(); // Cerrar sesión por si acaso
        } else {
            // Hay usuario autenticado, mostrar imágenes y datos correspondientes
            loadLatestImage();
        }

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), mapa.class);
                startActivity(intent);
            }
        });

        btnperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), perfilUsuario.class);
                startActivity(intent);
            }
        });
    }

    // Método para cargar la última imagen subida
    private void loadLatestImage() {
        // Obtener la referencia a la carpeta donde se guardan las imágenes
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");

        // Obtener la URL de la última imagen subida
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // Verificar si hay imágenes en la lista
                if (!listResult.getItems().isEmpty()) {
                    // Obtener la referencia de la última imagen subida
                    final StorageReference lastImageRef = listResult.getItems().get(listResult.getItems().size() - 1);

                    // Obtener la URL de descarga de la imagen
                    lastImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String imageUrl = uri.toString();

                            // Verificar si el usuario actual subió esta imagen
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ImagenInfo imageInfo = snapshot.getValue(ImagenInfo.class);
                                        if (imageInfo != null && imageInfo.getImageUrl().equals(imageUrl)) {
                                            // Mostrar la imagen utilizando Glide
                                            Glide.with(Archivos.this).load(imageUrl).into(imageView);

                                            // Obtener el nombre de la imagen (título) desde Firebase Realtime Database
                                            String title = imageInfo.getTitle();
                                            textViewNombreImagen.setText(title);
                                            return; // Salir del bucle
                                        }
                                    }

                                    // Si no se encontró la imagen subida por el usuario actual, limpiar la vista
                                    clearImageView();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("ArchivosActivity", "Error al obtener datos: " + databaseError.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ArchivosActivity", "Error al obtener la URL de la última imagen: " + e.getMessage());
                            Toast.makeText(Archivos.this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // No hay imágenes en Firebase Storage
                    Toast.makeText(Archivos.this, "No hay imágenes para mostrar", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ArchivosActivity", "Error al listar las imágenes: " + e.getMessage());
                Toast.makeText(Archivos.this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para limpiar la vista de la imagen y el nombre
    private void clearImageView() {
        imageView.setImageResource(android.R.color.transparent); // Limpiar la imagen
        textViewNombreImagen.setText(""); // Limpiar el nombre de la imagen
    }

    // Método para cerrar sesión
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Archivos.this, inicioSesion.class); // LoginActivity es tu actividad de inicio de sesión
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}