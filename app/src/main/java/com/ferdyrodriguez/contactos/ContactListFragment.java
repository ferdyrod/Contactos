package com.ferdyrodriguez.contactos;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import static android.app.Activity.RESULT_OK;


public class ContactListFragment extends Fragment {


    public static final String TAG = ContactListFragment.class.getSimpleName();
    private static final int ADD_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;

    private SimpleCursorAdapter newAdapter;
    private ListView myListView;
    private OnContactSelectedListener listener;
    public FloatingActionButton addFab;
    private int currentSelectedPos;

    public interface OnContactSelectedListener {

        void onContactSelected(long id);
    }
    public ContactListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listfragment, container, false);

        populateList(view);
        setHasOptionsMenu(true);
        addFab = (FloatingActionButton) view.findViewById(R.id.fab);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getResources().getBoolean(R.bool.isTwoPane)){
                    AddingContactFragment addingFragment = new AddingContactFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.flDetailContainer, addingFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    addFab.hide();
                } else {
                    Intent intent = new Intent(getActivity(), AddingContactActivity.class);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnContactSelectedListener) {
            listener = (OnContactSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ContactListFragment.OnContactSelectedListener");
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSampleContact:
                addingSampleContacts();
                return true;
            case R.id.exportToSD:
                exportContactInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getContactsCP() {
        String sortOrder = ContactContract.Contacto.COL_NAME + " ASC";
        return getActivity().getContentResolver().query(ContactContract.CONTENT_URI, null, null, null, sortOrder);
    }

    private void populateList(View view) {
        Cursor cursor = getContactsCP();

        myListView = (ListView) view.findViewById(R.id.contactListView);
        myListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        newAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.contact_item,
                cursor,
                new String[]{ContactContract.Contacto.COL_NAME, ContactContract.Contacto.COL_CELLPHONE},
                new int[]{R.id.txtName, R.id.txtMovil},
                0);
        myListView.setAdapter(newAdapter);
        myListView.setItemChecked(0, false);
        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                long contact_id = newAdapter.getItemId(position);
                listener.onContactSelected(contact_id);
                myListView.setItemChecked(position, true);
                currentSelectedPos = position;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        registerForContextMenu(myListView);
        myListView.setItemChecked(currentSelectedPos, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.op_delete:
                Uri uri = Uri.parse(ContactContract.CONTENT_URI + "/" + info.id);
                Log.d(TAG, "onContextItemSelected: uri " + uri);
                int rowDeleted = getActivity().getContentResolver().delete(uri,
                        null,
                        null);
                Log.d(TAG, "onContextItemSelected: rowDeleted " + rowDeleted);
                if (rowDeleted > 0) {
                    newAdapter.swapCursor(getContactsCP());
                } else {
                    Toast.makeText(getActivity(), R.string.contact_not_deleted, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.op_edit:
                if(getResources().getBoolean(R.bool.isTwoPane)){
                    myListView.setItemChecked(info.position, true);
                    addFab.hide();
                    EditingContactFragment editingContactFragment = EditingContactFragment.newInstance(info.id);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.flDetailContainer, editingContactFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Intent editIntent = new Intent(getActivity(), EditingContactActivity.class);
                    editIntent.putExtra(ContactListActivity.CONTACT_ID, info.id);
                    startActivityForResult(editIntent, EDIT_REQUEST_CODE);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                updateList();
            }
        } else if(requestCode == EDIT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                updateList();
            }
        }
    }

    public void updateList(){
        newAdapter.swapCursor(getContactsCP());
    }


    public void exportContactInfo() {

        String[] projection = {
                ContactContract.Contacto.COL_NAME,
                ContactContract.Contacto.COL_CELLPHONE,
                ContactContract.Contacto.COL_PHONE,
                ContactContract.Contacto.COL_EMAIL
        };

        Cursor cur = getActivity().getContentResolver().query(ContactContract.CONTENT_URI,
                projection,
                null,
                null,
                null);

        String fileName = "Contactos.txt";
        try {
            File sd_path = Environment.getExternalStorageDirectory();
            Log.d(TAG, "exportContactInfo: path " + sd_path);
            File f = new File(sd_path.getAbsolutePath(), fileName);
            OutputStreamWriter fout =new OutputStreamWriter(
                    new FileOutputStream(f));
            while(cur.moveToNext()){
                String name = cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_NAME));
                String cellphone = cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_CELLPHONE));
                String phone = cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_PHONE));
                String email = cur.getString(cur.getColumnIndex(ContactContract.Contacto.COL_EMAIL));
                String contacto = "["+ name + "," + cellphone + "," + phone + "," +  email + "]\n";
                fout.write(contacto);
            }
            fout.close();
            cur.close();
            Toast.makeText(getActivity(), R.string.contacts_exported, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(TAG, "Error al escribir fichero a tarjeta SD");
        }
    }

    public void addingSampleContacts() {
        String[][] sampleContacts = new String[][]{
                {"Jose Chavez ", "654829426", "9493943432", "jchavez@gmail.com"},
                {"Madelyn Lovo", "656409813", "9493943542", "mlovo@hotmail.com"},
                {"Diego Aviles", "632958315", "949343552", "daviles@gmail.com"},
                {"Felipe Caja", "782817377", "949354672", "fcaja@yahoo.es"},
                {"Stephan Savic", "235896433", "915784532", "ssavic@atleticomadrid.es"},
                {"Sofia Hernandez", "764537904", "934684532", "shern@gamil.com"},
                {"Natalia Gomez", "832894673", "949543432", "ngomez@outlook.com"},
                {"Karina Melgar", "765849305", "983229432", "kmelgar@yahoo.es"},
                {"Teresa Valle", "758237584", "976494532", "tvalle@tvalle.com"},
                {"Andres Velasquez", "7462549474", "92854532", "avelasquez@pwc.com"},
                {"Mariane Miranda", "8837264736", "949457532", "mmiranda@gmail.com"}
        };

        for (String[] sampleContact : sampleContacts) {
            String name = sampleContact[0];
            String movil = sampleContact[1];
            String telefono = sampleContact[2];
            String email = sampleContact[3];

            ContentValues values = new ContentValues();

            values.clear();
            values.put(ContactContract.Contacto.COL_NAME, name);
            values.put(ContactContract.Contacto.COL_CELLPHONE, movil);
            values.put(ContactContract.Contacto.COL_PHONE, telefono);
            values.put(ContactContract.Contacto.COL_EMAIL, email);

            getActivity().getContentResolver().insert(ContactContract.CONTENT_URI, values);

            newAdapter.swapCursor(getContactsCP());
        }
    }
}
