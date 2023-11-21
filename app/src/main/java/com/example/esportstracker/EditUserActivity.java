package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {


    private EditText user;
    private EditText name;
    private EditText pass;
    private EditText pass2;
    private ImageView imageView;
    byte[] imageData;
    byte[] OriginalimageData;
    boolean imageInfo = false;
    String OriginalUser, OriginalName, OriginalPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //toma el id del toolbar del xml y lo usa para cargar el menú de opciones(la flecha)
        Toolbar toolbar2 = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar2);

        Button update = (Button) findViewById(R.id.update);
        Button imagePicker = (Button) findViewById(R.id.imageUpdate);

        user = (EditText) findViewById(R.id.userUpdate);
        name = (EditText) findViewById(R.id.nameUpdate);
        pass = (EditText) findViewById(R.id.passUpdate);
        pass2 = (EditText) findViewById(R.id.pass2Update);
        imageView = findViewById(R.id.ImageUpdate);

        setInfoUser();
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Selecciona una imagen"), 3);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // on below line we are getting network info to get wifi network info.
                NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                // on below line displaying toast message when wi-fi is connected when wi-fi is disconnected
                if (wifiConnection.isConnected()) {
                    if (user.getText().toString().equals("") || name.getText().toString().equals("") || pass.getText().toString().equals("") || pass2.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(user.getText().toString().equals(OriginalUser) && name.getText().toString().equals(OriginalName) && pass.getText().toString().equals(OriginalPass) && !imageInfo){
                            Toast.makeText(getApplicationContext(), "Debe modificar al menos un campo", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(pass.getText().toString().equals(pass2.getText().toString())){

                                SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                                String usuario = preferences.getString("user","");

                                User newUser = new User();
                                newUser.setAlias(user.getText().toString().trim());
                                newUser.setEmail(usuario);
                                newUser.setPass(pass.getText().toString().trim());
                                newUser.setName(name.getText().toString().trim());
                                newUser.setAvatar(imageData);

                                CheckRegister checker = new CheckRegister();
                                if (checker.Check(newUser)) {
                                    ApiService apiService = RetrofitClient.getApiService();

                                    try {
                                        Call<Void> call = apiService.updateUser(newUser);
                                        // Resto del código
                                        call.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(EditUserActivity.this, "Información actualizada", Toast.LENGTH_SHORT).show();
                                                    imageInfo = false;
                                                } else {
                                                    int statusCode = response.code();
                                                    if (statusCode == 409) {
                                                        Toast.makeText(getApplicationContext(), "Este correo y/o usuario está registrado", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                        // Manejar el error de registro (por ejemplo, usuario ya registrado)
                                                        Log.d("ERROR", response.message());
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
                                else{
                                    Toast.makeText(getApplicationContext(), "Asegurese de ingresar los datos con el formato", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else Toast.makeText(EditUserActivity.this, "Intentalo cuando tengas conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {

        int id = menu.getItemId();

        if(id == R.id.back){
            Intent intent = new Intent(EditUserActivity.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades

            // Iniciar la nueva actividad
            startActivity(intent);
        }

        return super.onOptionsItemSelected(menu);
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

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                // Establece el Bitmap en el ImageView
                imageView.setImageBitmap(bitmap);

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

    public void setInfoUser(){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String usuario = preferences.getString("user","");

        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonElement> call = apiService.getUser(usuario);  // Cambia Void a tu tipo de respuesta
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();

                    if (jsonElement.isJsonObject()) {
                        // Convertir el JsonElement a JsonObject
                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        OriginalUser = jsonObject.get("aliasUser").getAsString();
                        OriginalPass = jsonObject.get("pass").getAsString();
                        OriginalName = jsonObject.get("name").getAsString();
                        user.setText(OriginalUser);
                        pass.setText(OriginalPass);
                        pass2.setText(OriginalPass);
                        name.setText(OriginalName);

                        JsonArray dataArray = jsonObject.getAsJsonObject("avatar")
                                .getAsJsonArray("data");

                        OriginalimageData = new byte[dataArray.size()];
                        byte[] byteArray = new byte[dataArray.size()];
                        for (int i = 0; i < dataArray.size(); i++) {
                            byteArray[i] = dataArray.get(i).getAsByte();
                            OriginalimageData[i] = dataArray.get(i).getAsByte();
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        imageView.setImageBitmap(bitmap);
                    }
                    //Log.d("UPDATE", jsonElement.toString());


                }
                else{
                    Toast.makeText(EditUserActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // Manejo de errores
                Log.d("TAG", "onFailure: ");
                t.printStackTrace(); // Imprime el error en la consola
                Toast.makeText(EditUserActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}