package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAdmin extends AppCompatActivity {

    private EditText user;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        Button login = (Button) findViewById(R.id.AdminLogin);

        user =(EditText) findViewById(R.id.AdminUser);
        pass =(EditText) findViewById(R.id.AdminPass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user.getText().toString().equals("") || pass.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                else{
                    User newUser = new User();
                    newUser.setEmail(user.getText().toString().trim());
                    newUser.setPass(pass.getText().toString().trim());

                    ApiService apiService = RetrofitClient.getApiService();
                    try {
                        Call<Void> call = apiService.loginAdmin(newUser);
                        // Resto del código
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    // Registro exitoso, realizar acciones adicionales si es necesario
                                    Toast.makeText(getApplicationContext(), "Usuario Loggeado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginAdmin.this, MenuAdmin.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                                    // Iniciar la nueva actividad
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Credenciales incorrecctas", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Manejar el error de la solicitud (por ejemplo, problemas de red)
                                Toast.makeText(getApplicationContext(), "Ocurrió un error al inicar sesión", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }
            }
        });
    }
}