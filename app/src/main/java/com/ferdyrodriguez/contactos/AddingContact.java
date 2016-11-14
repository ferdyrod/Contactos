package com.ferdyrodriguez.contactos;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class AddingContact extends AppCompatActivity {

    private static final String TAG = "Contactos";
    private static final String AUTHORITY = "com.ferdyrodriguez.contactoscontentprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/contactos");
    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;

    private static final String DB_COL_NAME = "nombre";
    private static final String DB_COL_CELLPHONE = "movil";
    private static final String DB_COL_PHONE = "telefono";
    private static final String DB_COL_EMAIL = "email";


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
                Intent returnIntent = new Intent();
                try {
                    ContentValues values = new ContentValues();
                    values.clear();
                    values.put(DB_COL_NAME, name);
                    values.put(DB_COL_CELLPHONE, cellphone);
                    values.put(DB_COL_PHONE, phone);
                    values.put(DB_COL_EMAIL, correo);
                    getApplicationContext().getContentResolver().insert(CONTENT_URI, values);
                    Log.d(TAG, "Contacto Guardado!");
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } catch (Exception e){
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });
    }
}
