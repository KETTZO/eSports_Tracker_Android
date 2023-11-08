package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.esportstracker.databinding.ActivityMainBinding;
import com.example.esportstracker.db.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import android.widget.Toolbar;



public class MainActivity extends AppCompatActivity {

    ArrayList<String> ListDatos;

    ActivityMainBinding binding;
    RecyclerView recycler;

    String actualFragmentTag;

    DBHelper dbHelper; // Declaración de la instancia de DBHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        setContentView(binding.getRoot());

        Toolbar toolbar2 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar2);

        replaceFragment(new Home(), "home");

        /*recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ListDatos = new ArrayList<String>();
        for (int i=0; i<50;i++){
            ListDatos.add("Dato #"+i+"");
        }

        adaptador adapter= new adaptador(ListDatos);
        recycler.setAdapter(adapter);*/


        /*
        recycler = (RecyclerView) findViewById(R.id.recycler);
        adaptador adapter= new adaptador();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);*/

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.homeBar){
                replaceFragment(new Home(), "home");
            }
            else if(id == R.id.calendar){
                replaceFragment(new CalendarFragment(), "calendar");
            }
            else if (id == R.id.notif) {
                replaceFragment(new NotifFragment(),"notif");
            }
            /*
            switch (item.getItemId()){
                case R.id.homeBar:
                    replaceFragment(new Home());
                    break;
                case R.id.calendar:
                    replaceFragment(new Home());
                    break;
                case R.id.notif:
                    replaceFragment(new Home());
                    break;
            }*/

            return true;
        });

    }

    private void replaceFragment(Fragment fragment, String tag){

        actualFragmentTag = tag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment, tag);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Cierra el teclado virtual
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(actualFragmentTag);

                if (currentFragment != null) {
                    if (currentFragment instanceof Home) {
                        Home homeFragment = (Home) currentFragment;
                        //llamar a un método del fragmento para pasarle la consulta
                        homeFragment.realizarBusqueda(query);
                    } else if (currentFragment instanceof CalendarFragment) {
                        CalendarFragment otherFragment = (CalendarFragment) currentFragment;
                        otherFragment.realizarBusqueda(query);
                    }
                    else if (currentFragment instanceof NotifFragment) {
                        NotifFragment otherFragment = (NotifFragment) currentFragment;
                        otherFragment.realizarBusqueda(query);
                    }

                /*// llamar a un método del fragmento para pasarle la consulta
                Home fragment = (Home) getSupportFragmentManager().findFragmentByTag(actualFragmentTag);
                if (fragment != null) {
                    fragment.realizarBusqueda(query);
                }*/
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {

        int id = menu.getItemId();

        if(id == R.id.homeBar){
            Toast.makeText(this, "clickeas home bb", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.account) {
            Intent intent = new Intent(MainActivity.this, EditUserActivity.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
            startActivity(intent);
        } else if(id == R.id.logout) {

            SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user", "");
            editor.putString("pass", "");
            editor.putString("eventsUser", "");
            editor.commit();

            DBHelper dbHelper = new DBHelper(MainActivity.this); // Reemplaza "getContext()" por el contexto adecuado
            dbHelper.clearTable("event_v4");

            Intent intent = new Intent(MainActivity.this, Login.class); // Reemplaza 'ActivityOriginal' y 'NuevaActividad' con los nombres correctos de tus actividades
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menu);
    }


}