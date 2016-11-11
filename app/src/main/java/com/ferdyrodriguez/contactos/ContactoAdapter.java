package com.ferdyrodriguez.contactos;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ferdyrod on 10/24/16.
 */

public class ContactoAdapter extends ArrayAdapter<Contacto> {

    Activity context;
    private ArrayList<Contacto> data;


    ContactoAdapter(Activity context, ArrayList<Contacto> data) {
        super(context, R.layout.contact_item, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        Contacto contacto = getItem(position);

        ViewTag viewTag;

        if(item == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            item = inflater.inflate(R.layout.contact_item, null);

            viewTag = new ViewTag();
            viewTag.nombre = (TextView)item.findViewById(R.id.txtName);
            viewTag.movil = (TextView)item.findViewById(R.id.txtMovil);
            item.setTag(viewTag);
        }
        else
        {
            viewTag = (ViewTag)item.getTag();
        }

        viewTag.nombre.setText(contacto.getNombre());
        viewTag.movil.setText(contacto.getMovil());
        return(item);
    }
}

class ViewTag{
    TextView nombre;
    TextView movil;
}