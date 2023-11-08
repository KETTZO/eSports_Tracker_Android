package com.example.esportstracker;

import android.content.pm.LauncherApps;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class adaptadorNotic extends RecyclerView.Adapter<adaptadorNotic.ViewHolderDatos>{

    /*String[] equipos = {"T1 vs G9", "Cloud9 vs DWG", "Rainbow 7 vs Fnatic"};
    String[] juegos = {"League of legends", "CSGO", "Valorant"};
    String[] horarios = {"3-10-2023 7:00PM", "3-10-2023 7:00PM", "3-10-2023 7:00PM"};*/

    JSONArray notifEventosArray;

    public adaptadorNotic(JSONArray juegosArray) {
        this.notifEventosArray = juegosArray;
        Log.d("3", juegosArray.toString());
    }


    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event,parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {


        try {
            JSONObject notif = notifEventosArray.getJSONObject(position);

            holder.juego.setText(notif.getString("juego"));
            holder.equipo.setText(notif.getString("duelo"));
            holder.horario.setText(notif.getString("fecha") + " " + notif.getString("hora"));

            // Configura el OnClickListener para el elemento de vista de la tarjeta
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(position, notif);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }




    }

    @Override
    public int getItemCount() {
        //return ListDatos.size();
        return  notifEventosArray.length();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView equipo;
        TextView juego;
        TextView horario;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            equipo = itemView.findViewById(R.id.equipo);
            juego = itemView.findViewById(R.id.juego);
            horario = itemView.findViewById(R.id.horario);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, JSONObject notif);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }



}

