package com.example.esportstracker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckEvent {

    Pattern hora = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    Pattern fecha = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$");
    Pattern juego = Pattern.compile("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
    Pattern duelo = Pattern.compile("^(.+)\\s+vs\\s+(.+)$");

    public Boolean Check(evento evento){

        boolean allCorrect = true;
        String error;

        Matcher matcher = hora.matcher(evento.getHora());
        if(!matcher.matches())
            allCorrect = false;

        matcher = fecha.matcher(evento.getFecha());
        if(!matcher.matches())
            allCorrect = false;

       matcher = juego.matcher(evento.getJuego());
        if(!matcher.matches())
            allCorrect = false;

        matcher = duelo.matcher(evento.getDuelo());
        if(!matcher.matches())
            allCorrect = false;

        return allCorrect;
        //if(user.getAlias() == )
    }
}
