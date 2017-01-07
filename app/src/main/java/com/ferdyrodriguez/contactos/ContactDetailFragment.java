package com.ferdyrodriguez.contactos;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ContactDetailFragment extends Fragment {

    public static final String CONTACT_ID = "id";

    private Uri contactUri;
    private TextView nombre;
    private TextView movil;
    private TextView telefono;
    private TextView email;

    public ContactDetailFragment(){}

    public static ContactDetailFragment newInstance(long index) {
        ContactDetailFragment fragmentDetail = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putLong(CONTACT_ID, index);
        fragmentDetail.setArguments(args);
        return fragmentDetail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            long contactID = getArguments().getLong(CONTACT_ID);
            contactUri = Uri.parse(ContactContract.CONTENT_URI + "/" + contactID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detailfragment, container, false);

        if(getResources().getBoolean(R.bool.isTwoPane)){
            Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
            ContactListFragment contactListFragment = (ContactListFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.fragmentList);
            if(contactListFragment.addFab.getVisibility() == View.GONE){
                contactListFragment.addFab.show();
            }
        } else {
            Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        nombre = (TextView) root.findViewById(R.id.txt_nombre);
        movil = (TextView) root.findViewById(R.id.txt_movil);
        telefono = (TextView) root.findViewById(R.id.txt_telefono);
        email = (TextView) root.findViewById(R.id.txt_email);

        getContactData(contactUri);

        return root;
    }

    private void getContactData(Uri uri) {

        String[] projection = {ContactContract.Contacto._ID,
                ContactContract.Contacto.COL_NAME,
                ContactContract.Contacto.COL_CELLPHONE,
                ContactContract.Contacto.COL_PHONE,
                ContactContract.Contacto.COL_EMAIL
        };

        Cursor cur = getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cur != null) {
            cur.moveToFirst();
            nombre.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_NAME)));
            movil.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_CELLPHONE)));
            telefono.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_PHONE)));
            email.setText(cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_EMAIL)));
            cur.close();
        }

    }
}
