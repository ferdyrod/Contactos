package com.ferdyrodriguez.contactos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditingContact extends AppCompatActivity {

    private static final String TAG = "Contactos";
    private static final String MY_PREF = "MyPrefs";
    private static final String FILE_ID = "ID";

    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;
    private Contacto updatedContact;

    String[] getContactId = new String[2];
    String[] getName = new String[2];
    String[] getMovil = new String[2];;
    String[] getPhone = new String[2];;
    String[] getEmail = new String[2];;
    String carpeta = "Contactos";

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_contact);

        nombre = (EditText) findViewById(R.id.ed_nombre);
        movil = (EditText) findViewById(R.id.ed_movil);
        telefono = (EditText) findViewById(R.id.ed_telefono);
        email = (EditText) findViewById(R.id.ed_email);

        extras = getIntent().getExtras();

        final String fileToEdit = extras.getString("FILE_TO_EDIT");
        final int position = extras.getInt("POSITION");

        readFile(fileToEdit);
        nombre.setText(getName[1]);
        movil.setText(getMovil[1]);
        telefono.setText(getPhone[1]);
        email.setText(getEmail[1]);

        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.editfab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Position", position);
                try {
                    updateContact(fileToEdit);
                    returnIntent.putExtra("Id", updatedContact.getContactId());
                    returnIntent.putExtra("Nombre", updatedContact.getNombre());
                    returnIntent.putExtra("Movil", updatedContact.getMovil());
                    returnIntent.putExtra("Telefono", updatedContact.getTelefono());
                    returnIntent.putExtra("Email", updatedContact.getEmail());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } catch (Exception e) {
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });

    }

    private void updateContact(String fileToEdit) {
        File myDir = getDir(carpeta, Context.MODE_PRIVATE);
        String filename = myDir.getAbsolutePath().concat("/" + fileToEdit);
        String newName;
        String newMovil;
        String newTelefono;
        String newEmail;

        String contactID = extras.getString("ContactID");
        newName = nombre.getText().toString();
        newMovil = movil.getText().toString();
        newTelefono = telefono.getText().toString();
        newEmail = email.getText().toString();

        updatedContact = new Contacto(contactID, newName, newMovil, newTelefono, newEmail);
        String datos = updatedContact.toString();
        try {
            FileOutputStream fOS = new FileOutputStream(filename);
            fOS.write(datos.getBytes());
            fOS.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "updateContact: No se puede escribir en el archivo");
        } catch (IOException e){
            Log.d(TAG, "updateContact: No se puede escribir en el archivo");
        }

    }

    private void readFile(String fileToEdit) {
        File myDir = getDir(carpeta, Context.MODE_PRIVATE);
        String filename = myDir.getAbsolutePath().concat("/" + fileToEdit);
        String line;

        try {
            FileInputStream fis = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                getContactId = line.split("=");
                getName = (br.readLine()).split("=");
                getMovil = (br.readLine()).split("=");
                getPhone = (br.readLine()).split("=");
                getEmail = (br.readLine()).split("=");
            }
            in.close();
        } catch (IOException e) {
            Log.d(TAG, "No se pudo leer el archivo");
        }
    }
}


