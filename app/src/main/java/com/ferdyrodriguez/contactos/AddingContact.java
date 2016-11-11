package com.ferdyrodriguez.contactos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddingContact extends AppCompatActivity {

    private static final String TAG = "Contactos";
    private static final String MY_PREF = "MyPrefs";
    private static final String FILE_ID = "ID";

    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;

    String carpeta = "Contactos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_contact);

        nombre = (EditText) findViewById(R.id.ed_nombre);
        movil = (EditText) findViewById(R.id.ed_movil);
        telefono = (EditText) findViewById(R.id.ed_telefono);
        email = (EditText) findViewById(R.id.ed_email);




        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.addfab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
                int id_count = prefs.getInt(FILE_ID,1);

                String contactId = String.valueOf(id_count);
                String name = nombre.getText().toString();
                if( nombre.getText().toString().length() == 0 ) {
                    nombre.setError("First name is required!");
                    return;
                }
                String cellphone = movil.getText().toString();
                if( movil.getText().toString().length() == 0 ) {
                    movil.setError( "First name is required!" );
                    return;
                }
                String phone = telefono.getText().toString();
                String correo = email.getText().toString();
                Contacto contacto = new Contacto(contactId, name, cellphone, phone, correo);
                Log.d(TAG, "Contact: " + contacto.toString());
                Intent returnIntent = new Intent();
                try {
                    saveContact(contacto);
                    Log.d(TAG, "Contacto Guardado!");
                    returnIntent.putExtra("Id", contacto.getContactId());
                    returnIntent.putExtra("Nombre", contacto.getNombre());
                    returnIntent.putExtra("Movil", contacto.getMovil());
                    returnIntent.putExtra("Telefono", contacto.getTelefono());
                    returnIntent.putExtra("Email", contacto.getEmail());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } catch (Exception e){
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });
    }

    private void saveContact(Contacto contacto) {

        SharedPreferences prefs = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        int count = prefs.getInt(FILE_ID,1);

        String fichero = "contacto_"+ count +".txt";
        String datos = contacto.toString();

        FileOutputStream fOS;
        File myDir = getDir(carpeta, Context.MODE_PRIVATE);
        File ficheroContacto = new File (myDir, fichero);
        try {
            fOS = new FileOutputStream(ficheroContacto);
            fOS.write(datos.getBytes());
            fOS.close();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FILE_ID, count++);
            editor.commit();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
