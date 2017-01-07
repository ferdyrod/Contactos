package com.ferdyrodriguez.contactos;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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


public class EditingContactFragment extends Fragment {

    public static final String TAG = EditingContactFragment.class.getSimpleName();
    public static final String CONTACT_ID = "id";

    private long contactID;
    private Uri contactUri;
    private EditText nombre;
    private EditText movil;
    private EditText telefono;
    private EditText email;


    public EditingContactFragment(){}

    public static EditingContactFragment newInstance(long index) {
        EditingContactFragment fragmentEdit = new EditingContactFragment();
        Bundle args = new Bundle();
        args.putLong(CONTACT_ID, index);
        fragmentEdit.setArguments(args);
        return fragmentEdit;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            contactID = getArguments().getLong(CONTACT_ID);
            contactUri = Uri.parse(ContactContract.CONTENT_URI + "/" + contactID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.editing_fragment, container, false);

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
        getContactData(contactUri);

        FloatingActionButton saveFab = (FloatingActionButton) root.findViewById(R.id.editfab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    updateContact(contactUri);
                    if (getResources().getBoolean(R.bool.isTwoPane)) {
                        ContactListFragment contactListFragment = (ContactListFragment) getActivity().getSupportFragmentManager()
                                .findFragmentById(R.id.fragmentList);
                        contactListFragment.updateList();
                        Toast.makeText(getActivity(), R.string.contact_saved, Toast.LENGTH_LONG).show();
                        showContactDetails(contactID);
                        contactListFragment.addFab.show();
                    } else {
                        Intent returnIntent = new Intent();
                        Toast.makeText(getActivity(), R.string.contact_saved, Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK, returnIntent);
                        getActivity().finish();
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), R.string.contact_not_saved, Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }


    private void showContactDetails(long id){
        ContactDetailFragment detailFragment = ContactDetailFragment.newInstance(id);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flDetailContainer, detailFragment);
        ft.commit();
    }

    private void getContactData(Uri contactoUri) {

        String[] projection = {ContactContract.Contacto._ID,
                ContactContract.Contacto.COL_NAME,
                ContactContract.Contacto.COL_CELLPHONE,
                ContactContract.Contacto.COL_PHONE,
                ContactContract.Contacto.COL_EMAIL
        };

        Cursor cur = getActivity().getContentResolver().query(contactoUri, projection, null, null, null);

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

        getActivity().getContentResolver().update(contactoUri, values, null, null);
        Log.d(TAG, "Contact Saved!");
    }

}
