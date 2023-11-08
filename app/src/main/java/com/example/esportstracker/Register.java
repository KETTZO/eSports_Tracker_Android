package com.example.esportstracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.esportstracker.db.DBHelper;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;
import java.io.ByteArrayInputStream;

public class Register extends AppCompatActivity {

    private EditText user;
    private EditText pass;
    private EditText email;
    private EditText pass2;
    private EditText name;

    DBHelper DB;
    boolean imageInfo = false;
    byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user=(EditText) findViewById(R.id.Registerusuario);
        pass=(EditText) findViewById(R.id.Registerpass1);
        pass2=(EditText) findViewById(R.id.Registerpass2);
        email=(EditText) findViewById(R.id.Registeremail);
        name=(EditText) findViewById(R.id.NameRegister);



        Button register= (Button) findViewById(R.id.registrarse);
        Button login = (Button) findViewById(R.id.login_register);
        Button imagePicker = (Button) findViewById(R.id.selectImageBtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.login_register) {
                    // Crear un Intent para abrir la nueva actividad
                    Intent intent = new Intent(Register.this, Login.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades

                    // Iniciar la nueva actividad
                    startActivity(intent);
                }
            }
        });

        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               intent.setType("image/");
               startActivityForResult(intent.createChooser(intent, "Selecciona una imagen"), 3);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.registrarse) {

                    if (user.getText().toString().equals("") || email.getText().toString().equals("") || pass.getText().toString().equals("") || pass2.getText().toString().equals("") || !imageInfo)
                        Toast.makeText(getApplicationContext(), "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                    else {

                        if(pass.getText().toString().equals(pass2.getText().toString())){

                           User newUser = new User();
                            newUser.setAlias(user.getText().toString().trim());
                            newUser.setEmail(email.getText().toString().trim());
                            newUser.setPass(pass.getText().toString().trim());
                            newUser.setName(name.getText().toString().trim());
                            newUser.setAvatar(imageData);

                            CheckRegister checker = new CheckRegister();
                            if (checker.Check(newUser)) {
                                ApiService apiService = RetrofitClient.getApiService();

                                try {
                                    Call<Void> call = apiService.registerUser(newUser);
                                    // Resto del código
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                // Registro exitoso, realizar acciones adicionales si es necesario
                                                Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Register.this, Login.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                                                // Iniciar la nueva actividad
                                                startActivity(intent);
                                            } else {
                                                int statusCode = response.code();
                                                if (statusCode == 409) {
                                                    Toast.makeText(getApplicationContext(), "Este correo y/o usuario está registrado", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                    // Manejar el error de registro (por ejemplo, usuario ya registrado)
                                                    Toast.makeText(getApplicationContext(), "Imagen demasiado grande", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            // Manejar el error de la solicitud (por ejemplo, problemas de red)
                                            Toast.makeText(getApplicationContext(), "Ocurrió un error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Asegurese de ingresar los datos con el formato", Toast.LENGTH_SHORT).show();
                            /*
                            //ApiService apiService = retrofit.create(ApiService.class);;
                            */
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            imageInfo = true;
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                imageData = getBytes(inputStream); // Convierte InputStream en byte[]

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}