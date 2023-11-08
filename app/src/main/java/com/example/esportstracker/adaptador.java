package com.example.esportstracker;

import android.content.pm.LauncherApps;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class adaptador extends RecyclerView.Adapter<adaptador.ViewHolderDatos> {
    JSONArray juegosArray;

    public adaptador(JSONArray juegosArray) {
        this.juegosArray = juegosArray;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        try {
            JSONObject juego = juegosArray.getJSONObject(position);

            //String nombre = juego.getString("duelo");
            //String desc = juego.getString("desc");
            //byte[] imageBytes = juego.getString("image");/* Obtén tu byte[] de imagen desde tus datos */;

            /*
            if (juego.has("image") && !juego.isNull("image")) {
                // "image" existe y no es nulo en "juego"
                JSONObject imageObject = juego.getJSONObject("image");

                if (imageObject.has("data") && !imageObject.isNull("data")) {
                    // "data" existe y no es nulo en "image"
                    JSONArray dataArray = imageObject.getJSONArray("data");

                    // Ahora puedes acceder a los elementos en "data"
                } else {
                    // "data" no existe o es nulo en "image"
                }
            }*/

            JSONArray imageArray = juego.getJSONObject("image").getJSONArray("data");
            byte[] imageBytes = new byte[imageArray.length()];
            for (int i = 0; i < imageArray.length(); i++) {
                imageBytes[i] = (byte) imageArray.getInt(i);
            }

            holder.juego.setText(juego.getString("duelo"));
            holder.desc.setText(juego.getString("juego"));
            holder.fecha.setText(juego.getString("fecha"));
            holder.hora.setText(juego.getString("hora"));

            String id = juego.getString("_id");
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.image.setImageBitmap(bitmap);

            // Configura el OnClickListener para el elemento de vista de la tarjeta
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(position, juego);
                    }
                }
            });
        } catch (JSONException e) {
            Log.d("5", "error:");
            e.printStackTrace();

        }
    }

    @Override
    public int getItemCount() {
        return juegosArray.length();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        ImageView image;
        TextView juego;
        TextView desc;
        TextView fecha;
        TextView hora;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            juego = itemView.findViewById(R.id.juego);
            desc = itemView.findViewById(R.id.desc);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, JSONObject evento);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

/*public class adaptador extends RecyclerView.Adapter<adaptador.ViewHolderDatos>{

    //ArrayList<String> ListDatos;
    String[] juegos = {"Fortnite", "Valorant", "The Witcher"};
    String[] descs = {"Juego Battle Royale", "Juego 5 contra 5", "Juego de mundo abierto"};
    int[] imagenes = {R.drawable.hk, R.drawable.hk, R.drawable.hk};

    public void getEvents(evento event){

    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        //holder.asignarDatos(ListDatos.get(position));

        holder.juego.setText(juegos[position]);
        holder.image.setImageResource(imagenes[position]);
        holder.desc.setText(descs[position]);


        // Configura el OnClickListener para el elemento de vista de la tarjeta
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        //return ListDatos.size();
        return  juegos.length;
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        ImageView image;
        TextView juego;
        TextView desc;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            juego = itemView.findViewById(R.id.juego);
            desc = itemView.findViewById(R.id.desc);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}*/
