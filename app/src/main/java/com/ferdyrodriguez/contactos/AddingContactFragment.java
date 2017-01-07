package com.ferdyrodriguez.contactos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class AddingContactFragment extends Fragment {

    public static final String TAG = AddingContactFragment.class.getSimpleName();

    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;

    public AddingContactFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adding_fragment, container, false);

        if(getResources().getBoolean(R.bool.isTwoPane)){
            Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        } else {
            Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        nombre = (EditText) root.findViewById(R.id.ed_nombre);
        movil = (EditText) root.findViewById(R.id.ed_movil);
        telefono = (EditText) root.findViewById(R.id.ed_telefono);
        email = (EditText) root.findViewById(R.id.ed_email);

        FloatingActionButton saveFab = (FloatingActionButton) root.findViewById(R.id.addfab);
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
                try {
                    long contactID = addContact(name, cellphone, phone, correo);
                    if(getResources().getBoolean(R.bool.isTwoPane)){
                        ContactListFragment contactListFragment = (ContactListFragment) getActivity().getSupportFragmentManager()
                                .findFragmentById(R.id.fragmentList);
                        contactListFragment.updateList();
                        Toast.makeText(getActivity(), R.string.contact_saved, Toast.LENGTH_LONG).show();
                        showContactDetails(contactID);
                        contactListFragment.addFab.show();
                    }
                    else {
                        Intent returnIntent = new Intent();
                        getActivity().setResult(Activity.RESULT_OK, returnIntent);
                        getActivity().finish();
                    }
                } catch (Exception e){
                    Intent returnIntent = new Intent();
                    getActivity().setResult(Activity.RESULT_CANCELED, returnIntent);
                    getActivity().finish();
                }
            }
        });

        return root;
    }

    private long addContact(String name, String cellphone, String phone, String correo) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(ContactContract.Contacto.COL_NAME, name);
        values.put(ContactContract.Contacto.COL_CELLPHONE, cellphone);
        values.put(ContactContract.Contacto.COL_PHONE, phone);
        values.put(ContactContract.Contacto.COL_EMAIL, correo);
        Uri insert = getActivity().getApplicationContext().getContentResolver().insert(ContactContract.CONTENT_URI, values);
        Log.d(TAG, "Contacto Guardado!");
        long id = Long.valueOf(insert.getLastPathSegment());
        return id;
    }

    private void showContactDetails(long contactID) {
        ContactDetailFragment detailFragment = ContactDetailFragment.newInstance(contactID);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flDetailContainer, detailFragment);
        ft.commit();
    }


}