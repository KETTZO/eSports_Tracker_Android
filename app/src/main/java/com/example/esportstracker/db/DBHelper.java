package com.example.esportstracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.example.esportstracker.evento;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "agenda.db";
    private static final String TABLE_USER = "user_v4";
    private static final String TABLE_EVENT = "event_v4";
    private static final String TABLE_ADMIN = "admin_v4";
    private static final String TABLE_EVENT_TRACK = "event_v4";


    /*public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }*/

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" +
                "email TEXT PRIMARY KEY," +
                "alias TEXT NOT NULL," +
                "pass TEXT NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ADMIN + "(" +
                "alias TEXT NOT NULL PRIMARY KEY," +
                "pass TEXT NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EVENT + "(" +
                "_id TEXT PRIMARY KEY," +
                "hora TEXT NOT NULL," +
                "fecha TEXT NOT NULL," +
                "duelo TEXT NOT NULL," +
                "juego TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "image BLOB NOT NULL," +
                "email TEXT NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EVENT_TRACK + "(" +
                "idEventTrack PRIMARY KEY AUTOINCREMENT," +
                "idEvent INTEGER NOT NULL," +
                "email TEXT NOT NULL," +
                "date DATETIME NOT NULL," +
                "FOREIGN KEY (idEvent) REFERENCES " + TABLE_EVENT + "(_id)," +
                "FOREIGN KEY (email) REFERENCES " + TABLE_USER + "(email))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // Aquí debes escribir las sentencias SQL para modificar la estructura de la base de datos.
            // Por ejemplo, eliminar tablas o agregar nuevas columnas.

            // En este ejemplo, eliminamos todas las tablas existentes y las volvemos a crear.
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_TRACK);

            onCreate(sqLiteDatabase);
        }
    }


    public boolean insertUser (String email, String alias, String pass){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("alias", alias);
        contentValues.put("pass",pass);
        long result = MyDB.insert(TABLE_USER, null, contentValues);

        if (result == -1) return false;
        else
            return true;
    }

    public boolean insertEvent (String email, String id, String hora, String fecha, String duelo, String juego, String description, byte[] image){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", id);
        contentValues.put("hora", hora);
        contentValues.put("fecha",fecha);
        contentValues.put("duelo",duelo);
        contentValues.put("juego",juego);
        contentValues.put("description",description);
        contentValues.put("image", image);
        contentValues.put("email",email);
        long result = MyDB.insert(TABLE_EVENT, null, contentValues);

        if (result == -1) return false;
        else
            return true;
    }

    public boolean checkEmail(String email){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursos = MyDB.rawQuery("SELECT * FROM user where email = ?", new String[] {email} );

        if (cursos.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean checkEmailPass(String email, String pass){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursos = MyDB.rawQuery("SELECT * FROM user where email = ? and pass = ?", new String[] {email, pass} );

        if (cursos.getCount() > 0)
            return true;
        else
            return false;
    }

    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
    }

    public boolean updateEventEmailById(String eventId, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Crea un objeto ContentValues con el nuevo valor del email
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("email", newEmail);

        // Especifica la cláusula WHERE para identificar el registro que se actualizará
        String whereClause = "_id = ?";
        String[] whereArgs = {eventId};

        // Realiza la actualización
        int rowsAffected = db.update(TABLE_EVENT, updatedValues, whereClause, whereArgs);

        // Si rowsAffected > 0, al menos un registro fue actualizado exitosamente
        return rowsAffected > 0;
    }

    public Cursor getRegistroPorId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                // Lista de columnas que deseas recuperar
                "_id",
                "email",
                // ... Agrega aquí el nombre de las columnas que necesites
        };

        String selection = "_id = ?";
        String[] selectionArgs = { id };

        // Realiza la consulta
        Cursor cursor = db.query(
                TABLE_EVENT,       // Nombre de la tabla
                projection,      // Columnas a recuperar
                selection,       // Cláusula WHERE
                selectionArgs,   // Argumentos de la cláusula WHERE
                null,            // Agrupar las filas
                null,            // Filtrar por grupos de filas
                null             // Orden de las filas
        );

        return cursor;
    }

    public JSONArray getAllEventsAsJSONArray() {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();

        String[] columns = {"_id", "hora", "fecha", "duelo", "juego", "description", "image", "email"};

        Cursor cursor = db.query(TABLE_EVENT, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                JSONObject event = new JSONObject();
                try {
                    int idIndex = cursor.getColumnIndex("_id");
                    if (idIndex >= 0) {
                        event.put("_id", cursor.getString(idIndex));
                    }

                    int horaIndex = cursor.getColumnIndex("hora");
                    if (horaIndex >= 0) {
                        event.put("hora", cursor.getString(horaIndex));
                    }

                    int fechaIndex = cursor.getColumnIndex("fecha");
                    if (fechaIndex >= 0) {
                        event.put("fecha", cursor.getString(fechaIndex));
                    }

                    int dueloIndex = cursor.getColumnIndex("duelo");
                    if (dueloIndex >= 0) {
                        event.put("duelo", cursor.getString(dueloIndex));
                    }

                    int juegoIndex = cursor.getColumnIndex("juego");
                    if (juegoIndex >= 0) {
                        event.put("juego", cursor.getString(juegoIndex));
                    }

                    int descIndex = cursor.getColumnIndex("description");
                    if (descIndex >= 0) {
                        event.put("desc", cursor.getString(descIndex));
                    }

                    int imageIndex = cursor.getColumnIndex("image");
                    if (imageIndex >= 0) {
                        byte[] imageBytes = cursor.getBlob(imageIndex);

                        // Crea un objeto JSON "image" con un campo "type" y un campo "data"
                        JSONObject imageObject = new JSONObject();
                        imageObject.put("type", "Buffer");

                        // Convierte los bytes de la imagen en un JSONArray
                        JSONArray imageData = new JSONArray();
                        for (byte imageByte : imageBytes) {
                            imageData.put((int) imageByte);
                        }

                        imageObject.put("data", imageData);

                        // Agrega el objeto "image" al evento
                        event.put("image", imageObject);
                    }

                    int emailIndex = cursor.getColumnIndex("email");
                    if (emailIndex >= 0) {
                        event.put("email", cursor.getString(emailIndex));
                    }

                    jsonArray.put(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            cursor.close();
        }

        return jsonArray;
    }
}
