package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.esportstracker.db.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText user;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cargarPreferencias();
        setContentView(R.layout.activity_login);



        user=(EditText) findViewById(R.id.userLogin);
        pass=(EditText) findViewById(R.id.passLogin);


        Button one= (Button) findViewById(R.id.button);
        Button two = (Button) findViewById(R.id.button3);
        Button admin = (Button) findViewById(R.id.AdminButton);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button) {

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    // on below line we are getting network info to get wifi network info.
                    NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    // on below line displaying toast message when wi-fi is connected when wi-fi is disconnected
                    if (!wifiConnection.isConnected()) {
                        if (user.getText().toString().equals("") || pass.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                        else{
                            SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                            String usuario = preferences.getString("user","");
                            String contra = preferences.getString("pass","");

                            if (usuario != "" && contra != ""){
                                Intent intent = new Intent(Login.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                                // Iniciar la nueva actividad
                                startActivity(intent);
                            }
                        }
                    } else {

                        if (user.getText().toString().equals("") || pass.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                        else {
                            User newUser = new User();
                            newUser.setEmail(user.getText().toString().trim());
                            newUser.setPass(pass.getText().toString().trim());

                            ApiService apiService = RetrofitClient.getApiService();
                            try {
                                Call<Void> call = apiService.loginUser(newUser);
                                // Resto del código
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            guardarPreferencias();
                                            // Registro exitoso, realizar acciones adicionales si es necesario
                                            Toast.makeText(getApplicationContext(), "Usuario Loggeado", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Login.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                                            // Iniciar la nueva actividad
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Credenciales incorrecctas", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        // Manejar el error de la solicitud (por ejemplo, problemas de red)
                                        Toast.makeText(getApplicationContext(), "Ocurrió un error al iniciar sesión", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }
                }
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button3) {
                    // Crear un Intent para abrir la nueva actividad
                    Intent intent = new Intent(Login.this, Register.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                    // Iniciar la nueva actividad
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "Contraseña y/o usuario incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.AdminButton) {
                    // Crear un Intent para abrir la nueva actividad
                    Intent intent = new Intent(Login.this, LoginAdmin.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                    // Iniciar la nueva actividad
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "Contraseña y/o usuario incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
/*
    public void onClick(View v) {

        if (v.getId() == R.id.button) {
            if (user.getText().toString() == "admin" && pass.getText().toString() == "admin") {

                // Crear un Intent para abrir la nueva actividad
                Intent intent = new Intent(Login.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                // Opcional: Puedes pasar datos a la nueva actividad utilizando el método putExtra
                intent.putExtra("clave", "valor"); // Reemplaza 'clave' y 'valor' con tus datos

                // Iniciar la nueva actividad
                startActivity(intent);
            }
            else if(user.getText().toString() != "admin" || pass.getText().toString() != "admin"){
                Toast.makeText(getApplicationContext(), "Contraseña y/o usuario incorrectos", Toast.LENGTH_SHORT).show();
            }

        }
        if (v.getId() == R.id.button3) {
            // Crear un Intent para abrir la nueva actividad
            Intent intent = new Intent(Login.this, Register.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
            // Opcional: Puedes pasar datos a la nueva actividad utilizando el método putExtra
            intent.putExtra("clave", "valor"); // Reemplaza 'clave' y 'valor' con tus datos

            // Iniciar la nueva actividad
            startActivity(intent);
        }
    }*/

    private void guardarPreferencias(){

        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String usuario = user.getText().toString();
        String contra = pass.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", usuario);
        editor.putString("pass", contra);
        editor.commit();
    }

    private void cargarPreferencias(){

        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String usuario = preferences.getString("user","");
        String contra = preferences.getString("pass","");

        if (usuario != "" && contra != ""){
            User newUser = new User();
            newUser.setEmail(usuario);
            newUser.setPass(contra);

            ApiService apiService = RetrofitClient.getApiService();
            try {
                Call<Void> call = apiService.loginUser(newUser);
                // Resto del código
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                            // Iniciar la nueva actividad
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



}