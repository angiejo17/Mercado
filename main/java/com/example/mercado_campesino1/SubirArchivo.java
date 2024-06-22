package com.example.mercado_campesino1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

public class SubirArchivo extends AppCompatActivity {

    Button btnPublicar;
    EditText nombreArchivo, descripcionArchivo, precioArchivo;
    ImageView articulosPhoto;
    Uri mImageUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_archivo);

        nombreArchivo = findViewById(R.id.editText_nombre);
        descripcionArchivo = findViewById(R.id.editText_Descripcion);
        precioArchivo = findViewById(R.id.editText_precio);
        articulosPhoto = findViewById(R.id.articulos_photo);
        btnPublicar = findViewById(R.id.btn_Publicar);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("imageInfo");

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
                clearForm();
            }
        });

        articulosPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void clearForm() {
        nombreArchivo.setText("");
        descripcionArchivo.setText("");
        precioArchivo.setText("");
        articulosPhoto.setImageDrawable(null);
        mImageUri = null;
    }

    private void clickPost() {
        String title = nombreArchivo.getText().toString().trim();
        String description = descripcionArchivo.getText().toString().trim();
        if (!title.isEmpty() && !description.isEmpty()) {
            if (mImageUri != null) {
                putBytes(title, description);
            } else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void putBytes(final String title, final String description) {
        // Generar un nombre único para la imagen utilizando la fecha actual
        final String imageName = new Date().getTime() + ".jpg";

        // Obtener una referencia al directorio donde se guardarán las imágenes
        final StorageReference imagesRef = storageReference.child("images/" + imageName);

        // Obtener los datos de la imagen comprimida
        byte[] data = compressImage(((BitmapDrawable) articulosPhoto.getDrawable()).getBitmap());

        // Subir la imagen a Firebase Storage
        UploadTask uploadTask = imagesRef.putBytes(data);

        // Manejar el éxito y el fracaso de la tarea de carga
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SubirArchivo.this, "Error al subir imagen: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Obtener la URL de descarga de la imagen
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String downloadUrl = uri.toString();

                        // Guardar los datos adicionales (nombre y descripción) en Firebase Realtime Database
                        saveImageInfoToDatabase(imageName, title, description, downloadUrl);

                        // Mostrar mensaje de éxito y navegar a la actividad Archivos
                        Toast.makeText(SubirArchivo.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SubirArchivo.this, Archivos.class);
                        startActivity(intent);
                        finish(); // Finaliza la actividad actual si es necesario
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubirArchivo.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                // Aquí puedes manejar el progreso de la subida si es necesario
            }
        });
    }

    private void saveImageInfoToDatabase(String imageName, String title, String description, String imageUrl) {
        // Crear un nuevo objeto de datos para guardar en la base de datos
        ImagenInfo imageInfo = new ImagenInfo(imageName, title, description, imageUrl);

        // Guardar los datos en la base de datos
        databaseReference.push().setValue(imageInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SubirArchivo", "Datos guardados correctamente en la base de datos");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SubirArchivo", "Error al guardar los datos en la base de datos: " + e.getMessage());
                    }
                });
    }

    private byte[] compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(mImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                articulosPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.d("Error", "Se produjo un error: " + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
