package com.ferdyrodriguez.contactos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "Contactos";
    private static final int ADD_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;
    private static final String MY_PREF = "MyPrefs";
    private static final String FILE_ID = "ID";
    private ArrayAdapter<Contacto> adapter;
    private ListView myListView;
    private ArrayList<Contacto> datos;
    File[] ListaContactos;
    String carpeta = "Contactos";

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

        datos = leerFicheros();
        Log.d(TAG, "onCreate: "+ datos);

        myListView = (ListView) findViewById(R.id.contactListView);
        adapter = new ContactoAdapter(this, datos);
        myListView.setAdapter(adapter);

        registerForContextMenu(myListView);
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
                    exportContactInfo(datos);
                    Toast.makeText(this, "Contactos Exportados en la SD!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,"Hubo un problema, no se exportaron los contactos", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.addSampleContact:
                addingSampleContacts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportContactInfo(ArrayList<Contacto> datos) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                String newId = extras.getString("Id");
                String newNombre = extras.getString("Nombre");
                String newMovil = extras.getString("Movil");
                String newTelefono = extras.getString("Telefono");
                String newEmail = extras.getString("Email");
                Contacto newContact = new Contacto(newId, newNombre, newMovil, newTelefono, newEmail);
                datos.add(newContact);
                Log.d(TAG, "onActivityResult: datos = " + datos);
                adapter.notifyDataSetChanged();
            }
        } else if(requestCode == EDIT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                int position = extras.getInt("Position");
                Log.d(TAG, "onActivityResult: position " + position);
                String updatedId = extras.getString("Id");
                String updateNombre = extras.getString("Nombre");
                String updateMovil = extras.getString("Movil");
                String updateTelefono = extras.getString("Telefono");
                String updateEmail = extras.getString("Email");
                Contacto updatedContact = new Contacto(updatedId, updateNombre, updateMovil, updateTelefono, updateEmail);
                datos.set(position, updatedContact);
                adapter.notifyDataSetChanged();
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
                Contacto deleteContact = datos.get(info.position);
                String contact_id = deleteContact.getContactId();
                datos.remove(info.position);
                getFileToDelete(contact_id);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.op_edit:
                Contacto editContact = datos.get(info.position);
                String editContact_id = editContact.getContactId();
                String fileToEdit = getFileToEdit(editContact_id);
                Intent editIntent = new Intent(getApplicationContext(), EditingContact.class);
                Log.d(TAG, "onContextItemSelected: File_to_edit " + fileToEdit);
                Log.d(TAG, "onContextItemSelected: position " + info.position);
                Log.d(TAG, "onContextItemSelected: files" + Arrays.toString(ListaContactos));
                Log.d(TAG, "onContextItemSelected: datos " + datos.get(info.position));
                editIntent.putExtra("FILE_TO_EDIT", fileToEdit);
                editIntent.putExtra("POSITION", info.position);
                editIntent.putExtra("ContactID", editContact_id);
                startActivityForResult(editIntent, EDIT_REQUEST_CODE);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private String getFileToEdit(String position) {
        String filename = "contacto_"+position+".txt";
        Log.d(TAG, "getFileToEdit: " + filename);
        return filename;
    }

    private void getFileToDelete(String position) {
        String fileNameToDelete = "contacto_"+position+".txt";
        File myDir = getDir(carpeta, Context.MODE_PRIVATE);
        File fileToDelete = new File(myDir, fileNameToDelete);
        fileToDelete.delete();
    }

    private ArrayList<Contacto> leerFicheros() {
        String line;
        String[] contactId = new String[2];
        String[] nombre = new String[2];
        String[] movil = new String[2];
        String[] telefono = new String[2];
        String[] email = new String[2];

        File myDir = getDir(carpeta, Context.MODE_PRIVATE);
        ListaContactos = myDir.listFiles();
        ArrayList<Contacto> allContacts = new ArrayList<>();
        Log.d(TAG, "leerFicheros: Lista de Ficheros " + Arrays.toString(ListaContactos));
        for (int i = 0; i < ListaContactos.length; i++) {
            if (ListaContactos[i].isFile()) {
                Log.d(TAG, "leerFicheros: " + ListaContactos[i].getName());
                try  {
                    FileInputStream fis = new FileInputStream(ListaContactos[i]);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while((line = br.readLine()) != null) {
                        contactId = line.split("=");
                        nombre = (br.readLine()).split("=");
                        movil = (br.readLine()).split("=");
                        telefono = (br.readLine()).split("=");
                        email = (br.readLine()).split("=");
                    }
                    allContacts.add(new Contacto(contactId[1], nombre[1], movil[1], telefono[1], email[1]));
                    in.close();
                } catch (Exception ex) {
                    Log.e("Ficheros", "Error al leer fichero desde memoria interna");
                }
            }
        }
        return allContacts;

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
}
