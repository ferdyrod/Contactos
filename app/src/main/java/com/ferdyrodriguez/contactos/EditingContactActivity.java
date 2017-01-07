package com.ferdyrodriguez.contactos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class EditingContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long contactID = getIntent().getLongExtra(ContactListActivity.CONTACT_ID, -1);

        if (savedInstanceState == null) {
            EditingContactFragment editingContactFragment = EditingContactFragment.newInstance(contactID);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, editingContactFragment);
            ft.commit();
        }
    }
}


