package com.ferdyrodriguez.contactos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.ferdyrodriguez.contactos.ContactListFragment.OnContactSelectedListener;


public class ContactListActivity extends AppCompatActivity implements OnContactSelectedListener {

    public static final String CONTACT_ID = "contact_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public void onContactSelected(long id) {
        if (getResources().getBoolean(R.bool.isTwoPane)) {
            ContactDetailFragment detailFragment = ContactDetailFragment.newInstance(id);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, detailFragment);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.putExtra (CONTACT_ID, id);
            startActivity(intent);
        }
    }

}
