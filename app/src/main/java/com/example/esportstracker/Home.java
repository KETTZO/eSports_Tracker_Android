package com.example.esportstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.esportstracker.db.DBHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home extends Fragment implements adaptador.OnItemClickListener {

    RecyclerView recycler;

    DBHelper dbHelper; // Declaración de la instancia de DBHelper

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // on below line we are getting network info to get wifi network info.
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // on below line displaying toast message when wi-fi is connected when wi-fi is disconnected
        if (wifiConnection.isConnected()) {
            getEventTracking();
            queveEventSubmit();

            ApiService apiService = RetrofitClient.getApiService();
            Call<JsonElement> call = apiService.getEvent();  // Cambia Void a tu tipo de respuesta

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        JSONArray jsonArrayResult = new JSONArray();

                        for (JsonElement element : jsonArray) {
                            try {
                                jsonArrayResult.put(new JSONObject(element.toString()));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Log.d("info traida", jsonArrayResult.toString());
                        //Toast.makeText(getActivity(), "array", Toast.LENGTH_SHORT).show();
                        recycler = (RecyclerView) view.findViewById(R.id.recycler);
                        adaptador adapter = new adaptador(jsonArrayResult);
                        adapter.setOnItemClickListener(Home.this); // Establece el fragmento como el listener
                        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

                        recycler.setAdapter(adapter);

                        insertEventSQLLite(jsonArray);

                    } else {
                        //Toast.makeText(getActivity(), "nada", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    // Manejo de errores
                    Log.d("TAG", "onFailure: ");
                    t.printStackTrace(); // Imprime el error en la consola
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "Modo sin conexión", Toast.LENGTH_SHORT).show();
            DBHelper dbHelper = new DBHelper(getContext()); // Reemplaza "getContext()" por el contexto adecuado
            JSONArray eventsArray = dbHelper.getAllEventsAsJSONArray();

            Log.d("info almacenada", eventsArray.toString());

            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            adaptador adapter = new adaptador(eventsArray);
            adapter.setOnItemClickListener(Home.this); // Establece el fragmento como el listener
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));

            recycler.setAdapter(adapter);

        }

        return view;
    }

    @Override
    public void onItemClick(int position, JSONObject evento) {
        // Maneja el clic en la tarjeta aquí
        // Puedes acceder al elemento en la posición 'position' en tu adaptador.

        //String mensaje = "Se hizo clic en la tarjeta en la posición " + position;
        //Toast.makeText(getActivity(), evento.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), EventoActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
        // Iniciar la nueva actividad
        intent.putExtra("evento", evento.toString());
        startActivity(intent);
    }

    public void realizarBusqueda(String query) {
        // Aquí realizas la acción correspondiente en tu fragmento con la consulta
        // Por ejemplo, puedes actualizar la lista de elementos del fragmento con los resultados de búsqueda.
        //Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();

        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonElement> call = apiService.getEventFiltered(query);  // Cambia Void a tu tipo de respuesta
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    JSONArray jsonArrayResult = new JSONArray();

                    for (JsonElement element : jsonArray) {
                        try {
                            jsonArrayResult.put(new JSONObject(element.toString()));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //Toast.makeText(getActivity(), "array", Toast.LENGTH_SHORT).show();
                    adaptador adapter= new adaptador(jsonArrayResult);
                    adapter.setOnItemClickListener(Home.this); // Establece el fragmento como el listener
                    recycler.setLayoutManager(new LinearLayoutManager(getContext() ));

                    recycler.setAdapter(adapter);


                }
                else{
                    //Toast.makeText(getActivity(), "nada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // Manejo de errores
                Log.d("TAG", "onFailure: ");
                t.printStackTrace(); // Imprime el error en la consola
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getEventTracking(){

        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String usuario = preferences.getString("user","");

        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonElement> call = apiService.GetEventTracking(usuario);  // Cambia Void a tu tipo de respuesta
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();

                    /*SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("eventsUser", jsonElement.toString());
                    editor.commit();*/
                    SharedPreferences preferences = requireActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("eventsUser", jsonElement.toString());
                    editor.commit(); // apply() se usa para guardar asincrónicamente
                }
                else{
                    //Toast.makeText(getActivity(), "nada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // Manejo de errores
                Log.d("TAG", "onFailure: ");
                t.printStackTrace(); // Imprime el error en la consola
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void insertEventSQLLite(JsonArray events){
        // Inicializa la instancia de DBHelper en el método onCreate o en algún otro lugar adecuado.
        dbHelper = new DBHelper(getContext()); // Reemplaza "getContext()" por el contexto adecuado

        dbHelper.clearTable("event_v4");

        //SharedPreferences preferences = requireActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        //String email = preferences.getString("user","");

        // Recorre el JsonArray
        for (JsonElement jsonElement : events) {
            if (jsonElement.isJsonObject()) {
                // Convierte el elemento a un JsonObject
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // Accede a los atributos del objeto
                String id = jsonObject.get("_id").getAsString();
                String hora = jsonObject.get("hora").getAsString();
                String fecha = jsonObject.get("fecha").getAsString();
                String duelo = jsonObject.get("duelo").getAsString();
                String juego = jsonObject.get("juego").getAsString();
                String description = jsonObject.get("desc").getAsString();

                JsonObject imageObject = jsonObject.getAsJsonObject("image");
                JsonArray dataArray = imageObject.getAsJsonArray("data");

                byte[] imageBytes = new byte[dataArray.size()];
                for (int i = 0; i < dataArray.size(); i++) {
                    imageBytes[i] = (byte) dataArray.get(i).getAsInt();
                }

                // Llama al método insertEvent para insertar el evento en la base de datos
                boolean result = dbHelper.insertEvent("", id, hora, fecha, duelo, juego, description, imageBytes);

                if (result) {
                    Log.d("database", "insertado");
                } else {
                    Log.d("database", "no insertado");
                }
            }
        }
    }

    private void queveEventSubmit(){
        DBHelper dbHelper = new DBHelper(getContext()); // Reemplaza "getContext()" por el contexto adecuado
        JSONArray eventsArray = dbHelper.getAllEventsAsJSONArray();

        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject event = eventsArray.getJSONObject(i);
                String email = event.getString("email");
                String id = event.getString("_id");

                if (!email.equals("")) {
                    ApiService apiService = RetrofitClient.getApiService();

                    JsonObject gsonObject = new JsonObject();
                    gsonObject.addProperty("email", email);
                    gsonObject.addProperty("event", id);

                    try {
                        Call<Void> call = apiService.SetEvenTracking(gsonObject);
                        // Resto del código
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Los eventos offline se agregaron", Toast.LENGTH_SHORT).show();
                                } else {

                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Manejar el error de la solicitud (por ejemplo, problemas de red)
                                Toast.makeText(getActivity(), "Ocurrió un error al intentar trackear evento", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dbHelper.clearTable("event_v4");
    }
}

