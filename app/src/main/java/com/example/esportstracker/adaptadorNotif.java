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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class adaptadorNotif extends RecyclerView.Adapter<adaptadorNotif.ViewHolderDatos>{

    //String[] notificaciones = { "Acaba de comenzar la partida de " + "T1 vs G9", "Acaba de comenzar la partida de " + "Cloud9 vs DWG", "Acaba de comenzar la partida de " + "Rainbow 7 vs Fnatic"};

    JSONArray notifArray;

    public adaptadorNotif(JSONArray juegosArray) {
        this.notifArray = juegosArray;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notif,parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {

        try {
            JSONObject notif = notifArray.getJSONObject(position);



            String Diff = TimeDifference(notif.getString("hora"));
            if(!Diff.equals("")){
                holder.notificacion.setText("Acaba de comenzar la partida de " + notif.getString("duelo"));
                holder.tiempo.setText("hace " + Diff);

            }
            else{
                holder.notificacion.setText("Hoy tienes el partido de " + notif.getString("duelo"));
                holder.tiempo.setText("A las " + notif.getString("hora"));
            }

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
        /*
        holder.notificacion.setText(notificaciones[position]);

        // Configura el OnClickListener para el elemento de vista de la tarjeta
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });*/



    }

    @Override
    public int getItemCount() {
        //return ListDatos.size();
        return  notifArray.length();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView notificacion;
        TextView tiempo;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            notificacion = itemView.findViewById(R.id.notificacion);
            tiempo = itemView.findViewById(R.id.tiempo);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, JSONObject notif);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private String TimeDifference(String horaObjetivo){
        // Obtén la hora actual
        Calendar currentTime = Calendar.getInstance();

        // Crea un formato para la hora en formato "HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // Define la hora objetivo en formato "HH:mm"
        //String horaObjetivo = "02:00"; // Hora objetivo menor que la hora actual

        try {
            // Parsea la hora objetivo en un objeto Date
            Date horaObjetivoDate = sdf.parse(horaObjetivo);

            // Crea un Calendar con la hora actual y la fecha del 1 de enero del año actual
            Calendar horaObjetivoCalendar = Calendar.getInstance();
            horaObjetivoCalendar.set(Calendar.HOUR_OF_DAY, horaObjetivoDate.getHours());
            horaObjetivoCalendar.set(Calendar.MINUTE, horaObjetivoDate.getMinutes());
            horaObjetivoCalendar.set(Calendar.SECOND, 0); // Reinicia los segundos a 0

            Log.d("HORA", horaObjetivoCalendar.getTime().toString() + " actual: " + currentTime.getTime().toString());

            // Compara si la hora objetivo es menor que la hora actual
            if (horaObjetivoCalendar.before(currentTime)) {
                // Calcula la diferencia en milisegundos (hora actual - hora objetivo)
                long differenceMillis = currentTime.getTimeInMillis() - horaObjetivoCalendar.getTimeInMillis();

                // Calcula la diferencia en minutos
                long differenceMinutes = differenceMillis / (60 * 1000);
                // Calcula la diferencia en horas
                long differenceHours = differenceMillis / (60 * 60 * 1000);

                String timeDifference;
                if (differenceHours >= 1) {
                    timeDifference = differenceHours + " horas";
                    return timeDifference;
                } else {
                    timeDifference = differenceMinutes + " minutos";
                    return timeDifference;
                }

                // Ahora timeDifference contiene la diferencia en minutos u horas
            } else {
                // La hora objetivo no es menor que la hora actual, no se muestra la diferencia
                // Puedes manejar esto de acuerdo a tus necesidades
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}