package com.ferdyrodriguez.contactos;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Contactos";
    private static final int ADD_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;
    private static final String AUTHORITY = "com.ferdyrodriguez.contactoscontentprovider";
    private static final String DB_COL_NAME = "nombre";
    private static final String DB_COL_CELLPHONE = "movil";
    private static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/contactos");

    private SimpleCursorAdapter newAdapter;
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddingContact.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });

        Cursor cursor = getContactsCP();

        myListView = (ListView) findViewById(R.id.contactListView);
        newAdapter = new SimpleCursorAdapter(this,
                R.layout.contact_item,
                cursor,
                new String[]{DB_COL_NAME, DB_COL_CELLPHONE},
                new int[]{R.id.txtName, R.id.txtMovil},
                0);
        myListView.setAdapter(newAdapter);
        getContactsCP();

        registerForContextMenu(myListView);
    }

    private Cursor getContactsCP() {
        Cursor cur = getContentResolver().query(CONTENT_URI, null, null, null, null);
        return cur;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.exportToSD:
                boolean sdAvailable;
                boolean sdWriteAccess;
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    sdAvailable = true;
                    sdWriteAccess = true;
                }
                else if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    sdAvailable = true;
                    sdWriteAccess = false;
                } else {
                    sdAvailable = false;
                    sdWriteAccess = false;
                }

                if(sdAvailable && sdWriteAccess){
 //                   exportContactInfo(datos);
                    Toast.makeText(this, "Contactos Exportados en la SD!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,"Hubo un problema, no se exportaron los contactos", Toast.LENGTH_LONG).show();
                }
                return true;
 //           case R.id.addSampleContact:
  //              addingSampleContacts();
  //              return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                newAdapter.swapCursor(getContactsCP());
            }
        } else if(requestCode == EDIT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                newAdapter.swapCursor(getContactsCP());
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.op_delete:
                Uri uri = Uri.parse(CONTENT_URI + "/" + info.id);
                Log.d(TAG, "onContextItemSelected: uri " + uri);
                int rowDeleted = getApplicationContext().getContentResolver().delete(uri,
                        null,
                        null);
                Log.d(TAG, "onContextItemSelected: rowDeleted " + rowDeleted);
                if (rowDeleted > 0){
                    newAdapter.swapCursor(getContactsCP());
                } else {
                    Toast.makeText(this, "Hubo un problema borrando el contacto", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.op_edit:
                Uri contactUri = Uri.parse(CONTENT_URI + "/" + info.id);
                Intent editIntent = new Intent(getApplicationContext(), EditingContact.class);
                editIntent.putExtra("CONTACT_URI", contactUri);
                startActivityForResult(editIntent, EDIT_REQUEST_CODE);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

/*    private void exportContactInfo(ArrayList<Contacto> datos) {
        String fileName = "Contactos.txt";
        try {
            File sd_path = Environment.getExternalStorageDirectory();
            File f = new File(sd_path.getAbsolutePath(), fileName);
            OutputStreamWriter fout =new OutputStreamWriter(
                    new FileOutputStream(f));
            fout.write(datos.toString());
            fout.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
        }
    }



    private void addingSampleContacts() {
        String[][] sampleContacts = new String[][]{
                {"1", "Jose Chavez ", "654829426", "9493943432", "jchavez@gmail.com"},
                {"2", "Madelyn Lovo", "656409813", "9493943542", "mlovo@hotmail.com"},
                {"3", "Diego Aviles", "632958315", "949343552", "daviles@gmail.com"},
                {"4", "Felipe Caja", "782817377", "949354672", "fcaja@yahoo.es"},
                {"5", "Stephan Savic", "235896433", "915784532", "ssavic@atleticomadrid.es"},
                {"6", "Sofia Hernandez", "764537904", "934684532", "shern@gamil.com"},
                {"7", "Natalia Gomez", "832894673", "949543432", "ngomez@outlook.com"},
                {"8", "Karina Melgar", "765849305", "983229432", "kmelgar@yahoo.es"},
                {"9", "Teresa Valle", "758237584", "976494532", "tvalle@tvalle.com"},
                {"10", "Andres Velasquez", "7462549474", "92854532", "avelasquez@pwc.com"},
                {"11", "Mariane Miranda", "8837264736", "949457532", "mmiranda@gmail.com"}
        };
        SharedPreferences prefs = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);

        for (int i = 0; i < sampleContacts.length; i++) {
            int count = prefs.getInt(FILE_ID, 1);
            String contactId = sampleContacts[i][0];
            String name = sampleContacts[i][1];
            String movil = sampleContacts[i][2];
            String telefono = sampleContacts[i][3];
            String email = sampleContacts[i][4];
            Log.d(TAG, "addingSampleContacts: id " + contactId);
            Log.d(TAG, "addingSampleContacts: name " + name);
            Log.d(TAG, "addingSampleContacts: name " + movil);
            Log.d(TAG, "addingSampleContacts: name " + telefono);
            Log.d(TAG, "addingSampleContacts: name " + email);
            Contacto contacto = new Contacto(contactId, name, movil, telefono, email);

            String contact = contacto.toString();
            FileOutputStream fOS;
            File myDir = getDir(carpeta, Context.MODE_PRIVATE);
            String fichero = "contacto_" + count + ".txt";
            File ficheroContacto = new File(myDir, fichero);
            try {
                fOS = new FileOutputStream(ficheroContacto);
                fOS.write(contact.getBytes());
                fOS.close();
                SharedPreferences.Editor editor = prefs.edit();
                Log.d(TAG, "saveContact: count++ : " + count++);
                editor.putInt(FILE_ID, count++);
                editor.commit();
                Log.d(TAG, "saveContact: count " + prefs.getInt(FILE_ID, 1));
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            datos.add(contacto);
            adapter.notifyDataSetChanged();
        }
    }
    */
}
