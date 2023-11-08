package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esportstracker.db.DBHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoActivity extends AppCompatActivity {

    ImageView portada;
    TextView VS;
    TextView lorem;
    TextView juegoEvent;
    TextView fechaEvent;
    TextView horaEvent;
    JSONObject evento;
    Bitmap bitmap;

    DBHelper dbHelper; // Declaración de la instancia de DBHelper

    //Button track = (Button) findViewById(R.id.button);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        Button track = (Button) findViewById(R.id.button);

        /*try {
            alreadyTracked(evento.getString("_id"), track);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/

        portada = findViewById(R.id.portada);
        VS = findViewById(R.id.VS);
        lorem = findViewById(R.id.lorem);
        juegoEvent = findViewById(R.id.juegoEvent);
        fechaEvent = findViewById(R.id.fechaEvent);
        horaEvent = findViewById(R.id.horaEvent);

        // Recupera el JSON como una cadena del Intent
        String jsonString = getIntent().getStringExtra("evento");
        // Convierte la cadena JSON de nuevo a un JSONObject
        try {
            evento = new JSONObject(jsonString);
            // Ahora puedes acceder a los valores del JSONObject
            //Toast.makeText(this, evento.getString("_id"), Toast.LENGTH_SHORT).show();
            alreadyTracked(evento.getString("_id"), track);
            alreadyTrackedLocally(evento.getString("_id"), track);

            //String nombre = jsonObject.getString("nombre");
            //int edad = jsonObject.getInt("edad");
            JSONArray imageArray = evento.getJSONObject("image").getJSONArray("data");
            byte[] imageBytes = new byte[imageArray.length()];
            for (int i = 0; i < imageArray.length(); i++) {
                imageBytes[i] = (byte) imageArray.getInt(i);
            }
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            portada.setImageBitmap(bitmap);
            VS.setText(evento.getString("duelo"));
            lorem.setText(evento.getString("desc"));
            juegoEvent.setText(evento.getString("juego"));
            fechaEvent.setText(evento.getString("fecha"));
            horaEvent.setText(evento.getString("hora"));

        } catch (JSONException e) {
            Log.d("error", "onCreate: ");
            e.printStackTrace();
        }


        Toolbar toolbar2 = findViewById(R.id.toolbarEvento);
        setSupportActionBar(toolbar2);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // on below line we are getting network info to get wifi network info.
                NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiConnection.isConnected()) {
                    SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                    // Crear un objeto JSON
                    JsonObject jsonObject = new JsonObject();
                    // Agregar dos variables al objeto JSON
                    jsonObject.addProperty("email", preferences.getString("user", ""));
                    try {
                        jsonObject.addProperty("event", evento.getString("_id"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                    ApiService apiService = RetrofitClient.getApiService();
                    try {
                        Call<Void> call = apiService.SetEvenTracking(jsonObject);
                        // Resto del código
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Ahora estás siguiendo el evento", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EventoActivity.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Ya estás siguiendo este evento", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Manejar el error de la solicitud (por ejemplo, problemas de red)
                                Toast.makeText(getApplicationContext(), "Ocurrió un error al intentar trackear evento", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                else{

                    dbHelper = new DBHelper(EventoActivity.this);

                    SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                    try {
                        boolean updated = dbHelper.updateEventEmailById(evento.getString("_id"), preferences.getString("user", ""));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Toast.makeText(getApplicationContext(), "Ahora estás siguiendo el evento", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EventoActivity.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
                    startActivity(intent);
                }
            }
        });

    }

    private void alreadyTracked(String currentEvent, Button track) {

        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String eventosUsuario = preferences.getString("eventsUser", "");

        //Toast.makeText(this, eventosUsuario, Toast.LENGTH_SHORT).show();
        JsonObject jsonObject = new JsonParser().parse(eventosUsuario).getAsJsonObject();

        if (jsonObject.has("eventosSeguidos")) {
            JsonArray eventosSeguidos = jsonObject.getAsJsonArray("eventosSeguidos");

            for (JsonElement event : eventosSeguidos) {
                JsonObject eventoObject = event.getAsJsonObject();
                // Ahora puedes acceder a las propiedades del evento, por ejemplo:
                String idEvento = eventoObject.get("_id").getAsString();
                //Toast.makeText(this, "current: " + currentEvent + " loop: " + idEvento, Toast.LENGTH_SHORT).show();
                if(idEvento.equals(currentEvent)){
                    //Toast.makeText(this, "entra", Toast.LENGTH_SHORT).show();
                    track.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void alreadyTrackedLocally(String currentEvent, Button track) {

        DBHelper dbHelper = new DBHelper(this); // Reemplaza 'this' con el contexto adecuado

        Cursor cursor = dbHelper.getRegistroPorId(currentEvent);

        if (cursor.moveToFirst()) {
            // El cursor contiene los datos del registro encontrado
            int idIndex = cursor.getColumnIndex("email");
            if (idIndex >= 0) {
                String email = cursor.getString(idIndex);

                if(!email.equals("")){
                    track.setVisibility(View.INVISIBLE);
                }
            }


        }

// No olvides cerrar el cursor cuando hayas terminado de usarlo
        cursor.close();
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
            Intent intent = new Intent(EventoActivity.this, MainActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades

            // Iniciar la nueva actividad
            startActivity(intent);
        }

        return super.onOptionsItemSelected(menu);
    }
}