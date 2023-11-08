package com.example.esportstracker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckRegister {

    Pattern pass = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
    Pattern alias = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$");
    Pattern name = Pattern.compile("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
    Pattern email = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    public Boolean Check(User user){

        boolean allCorrect = true;
        String error;

        Matcher matcher = pass.matcher(user.getPass());
        if(!matcher.matches())
            allCorrect = false;

        matcher = alias.matcher(user.getAlias());
        if(!matcher.matches())
            allCorrect = false;

        matcher = email.matcher(user.getEmail());
        if(!matcher.matches())
            allCorrect = false;

        matcher = name.matcher(user.getName());
        if(!matcher.matches())
            allCorrect = false;

        return allCorrect;
        //if(user.getAlias() == )
    }
}
