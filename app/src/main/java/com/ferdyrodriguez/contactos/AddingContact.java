package com.ferdyrodriguez.contactos;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class AddingContact extends AppCompatActivity {

    private static final String TAG = "Contactos";
    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;



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
                    values.put(ContactContract.Contacto.COL_NAME, name);
                    values.put(ContactContract.Contacto.COL_CELLPHONE, cellphone);
                    values.put(ContactContract.Contacto.COL_PHONE, phone);
                    values.put(ContactContract.Contacto.COL_EMAIL, correo);
                    getApplicationContext().getContentResolver().insert(ContactContract.CONTENT_URI, values);
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
