package com.ferdyrodriguez.contactos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditingContact extends AppCompatActivity {

    private static final String TAG = "Contactos";

    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;

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

        final Uri contactoUri = extras.getParcelable("CONTACT_URI");
        
        getContactData(contactoUri);

        Log.d(TAG, "onCreate: uri " + contactoUri);
        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.editfab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                try {
                    updateContact(contactoUri);
                    Toast.makeText(getApplicationContext(), "Información del Contacto actualizada", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } catch (Exception e) {
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });

    }

    private void getContactData(Uri contactoUri) {

        String[] projection = {ContactContract.Contacto._ID,
                ContactContract.Contacto.COL_NAME,
                ContactContract.Contacto.COL_CELLPHONE,
                ContactContract.Contacto.COL_PHONE,
                ContactContract.Contacto.COL_EMAIL
        };

        Cursor cur = getContentResolver().query(contactoUri, projection, null, null, null);

        if (cur != null) {
            cur.moveToFirst();
            nombre.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_NAME)));
            movil.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_CELLPHONE)));
            telefono.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_PHONE)));
            email.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_EMAIL)));

            cur.close();
        }

    }

    private void updateContact(Uri contactoUri) {
        String newName;
        String newMovil;
        String newTelefono;
        String newEmail;

        newName = nombre.getText().toString();
        newMovil = movil.getText().toString();
        newTelefono = telefono.getText().toString();
        newEmail = email.getText().toString();

        ContentValues values = new ContentValues();
        values.clear();
        values.put(ContactContract.Contacto.COL_NAME, newName);
        values.put(ContactContract.Contacto.COL_CELLPHONE, newMovil);
        values.put(ContactContract.Contacto.COL_PHONE, newTelefono);
        values.put(ContactContract.Contacto.COL_EMAIL, newEmail);

        getApplicationContext().getContentResolver().update(contactoUri, values, null, null);
    }
}


