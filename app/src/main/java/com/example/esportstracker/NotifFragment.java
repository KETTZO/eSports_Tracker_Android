package com.example.esportstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotifFragment extends Fragment {

    RecyclerView recycler,recycler2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.activity_main, container, false);

        View view = inflater.inflate(R.layout.fragment_notif, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String eventosUsuario = preferences.getString("eventsUser", "");

        //Toast.makeText(this, eventosUsuario, Toast.LENGTH_SHORT).show();
        JsonObject EventsJson = new JsonParser().parse(eventosUsuario).getAsJsonObject();

        JsonArray eventosSeguidos = EventsJson.getAsJsonArray("eventosSeguidos");

        JSONArray eventosSeguidosJSON = new JSONArray();

        for (JsonElement element : eventosSeguidos) {
            try {
                eventosSeguidosJSON.put(new JSONObject(element.toString()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        // Crear dos JSONArray donde almacenaremos los eventos
        JSONArray eventosDeHoy = new JSONArray();

        // Obtener la fecha actual en el mismo formato que las fechas en tu JSON
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaActual = sdf.format(new Date());

        // Recorrer tu JSON existente (que asumo que es un JSONArray)
        for (int i = 0; i < eventosSeguidosJSON.length(); i++) {
            try {
                JSONObject eventoObject = eventosSeguidosJSON.getJSONObject(i);

                // Obtener la fecha del evento en su formato actual (ejemplo: "2023-1-1")
                String fechaEventoOriginal = eventoObject.getString("fecha");

                // Formatear la fecha del evento al formato "yyyy-MM-dd"
                SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-M-d");
                Date fechaEvento = sdfOriginal.parse(fechaEventoOriginal);
                String fechaFormateada = sdf.format(fechaEvento);

                // Comprobar si la fecha del evento es igual a la fecha actual
                if (fechaFormateada.equals(fechaActual)) {
                    // La fecha del evento es la misma que la fecha actual
                    // Almacenar toda la información de este evento en el JSONArray de hoy
                    eventosDeHoy.put(eventoObject);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        //se llena el primer recycler
        recycler = (RecyclerView) view.findViewById(R.id.recyclerNotif);
        adaptadorNotif adapter= new adaptadorNotif(eventosDeHoy);
        recycler.setLayoutManager(new LinearLayoutManager(getContext() ));

        recycler.setAdapter(adapter);


        return view;

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_notif, container, false);
    }

    public void realizarBusqueda(String query) {
        // Aquí realizas la acción correspondiente en tu fragmento con la consulta
        // Por ejemplo, puedes actualizar la lista de elementos del fragmento con los resultados de búsqueda.
        Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
    }
}