package com.ferdyrodriguez.contactos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ContactDetailActivity extends AppCompatActivity {

    private static final String TAG = ContactDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long contactID = getIntent().getLongExtra(ContactListActivity.CONTACT_ID, -1);

        if (savedInstanceState == null) {
            ContactDetailFragment detailFragment = ContactDetailFragment.newInstance(contactID);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, detailFragment);
            ft.commit();
        }
    }

}
