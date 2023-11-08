package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EditUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //toma el id del toolbar del xml y lo usa para cargar el menú de opciones(la flecha)
        Toolbar toolbar2 = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar2);
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
}