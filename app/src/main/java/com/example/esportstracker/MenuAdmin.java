package com.example.esportstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuAdmin extends AppCompatActivity {

    private Button datePickerButton;
    private Button timePickerButton;
    boolean imageInfo = false;
    byte[] imageData;
    private EditText duelo;
    private EditText juego;
    private EditText desc;
    private String fecha = "";
    private String hora = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);

        duelo =(EditText) findViewById(R.id.dueloEvent);
        juego =(EditText) findViewById(R.id.juegoEvent);
        desc =(EditText) findViewById(R.id.descEvent);

        Button imagePicker = (Button) findViewById(R.id.imagePickerButton);
        Button event= (Button) findViewById(R.id.createEvent);

        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Selecciona una imagen"), 3);
            }
        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (duelo.getText().toString().equals("") || juego.getText().toString().equals("") || desc.getText().toString().equals("") || hora.equals("") || fecha.equals("") || !imageInfo)
                    Toast.makeText(getApplicationContext(), "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
                    Date fechaFormated = null;
                    try {
                        fechaFormated = dateFormat.parse(fecha);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Date currentDate = new Date();
                    if(!(fechaFormated.compareTo(currentDate) >= 0)){
                        Toast.makeText(getApplicationContext(), "La fecha debe ser igual o posterior a la actual", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        evento newEvent = new evento();
                        newEvent.setDesc(desc.getText().toString());
                        newEvent.setDuelo(duelo.getText().toString());
                        newEvent.setJuego(juego.getText().toString());
                        newEvent.setFecha(fecha);
                        newEvent.setHora(hora);
                        newEvent.setImage(imageData);

                        Log.d("MiApp", newEvent.getHora());
                        CheckEvent checker = new CheckEvent();
                        if (checker.Check(newEvent)) {
                            ApiService apiService = RetrofitClient.getApiService();

                            try {
                                Call<Void> call = apiService.setEvent(newEvent);
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            // Registro exitoso, realizar acciones adicionales si es necesario
                                            Toast.makeText(getApplicationContext(), "Evento registrado", Toast.LENGTH_SHORT).show();
                                            timePickerButton.setText("Hora");
                                            datePickerButton.setText("fecha");
                                            hora = "";
                                            fecha = "";
                                            duelo.setText("");
                                            juego.setText("");
                                            desc.setText("");
                                        } else {
                                            // Manejar el error de registro (por ejemplo, usuario ya registrado)
                                            Toast.makeText(getApplicationContext(), "Imagen demasiado grande", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        // Manejar el error de la solicitud (por ejemplo, problemas de red)
                                        Toast.makeText(getApplicationContext(), "Ocurrió un error al registrar el evento", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else Toast.makeText(getApplicationContext(), "Ingrese la información en el formato correcto", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (View, year, month, dayOfMonth) -> {
                    // La fecha seleccionada se maneja aquí
                    // Puedes mostrarla en un TextView o realizar acciones adicionales
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    fecha = selectedDate;
                    datePickerButton.setText(selectedDate);
                },
                2023, 0, 1 // Año, mes (0-11), día predeterminados
        );
        datePickerDialog.show();
    }

    public void showTimePickerDialog(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (View, hourOfDay, minute) -> {
                    // La hora seleccionada se maneja aquí
                    // Puedes mostrarla en un TextView o realizar acciones adicionales
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    hora = selectedTime;
                    timePickerButton.setText(selectedTime);
                },
                12, 0, false // Hora, minuto, formato 12 horas (true) o 24 horas (false)
        );
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            imageInfo = true;
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                imageData = getBytes(inputStream); // Convierte InputStream en byte[]

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}