package com.ferdyrodriguez.contactos;

/**
 * Created by ferdyrod on 10/19/16.
 */

public class Contacto {

    private String contactId;
    private String nombre;
    private String movil;
    private String telefono;
    private String email;

    public Contacto(String contactId, String nombre, String movil, String telefono, String email) {
        this.contactId = contactId;
        this.nombre = nombre;
        this.movil = movil;
        this.telefono = telefono;
        this.email = email;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String id) {
        this.contactId = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        nombre = nombre;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        movil = movil;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email;
    }


    @Override
    public String toString() {
        return "id=" + getContactId() + "\n" +
                "nombre=" + getNombre() + "\n" +
                "movil=" + getMovil() + "\n" +
                "telefono=" + getTelefono() + "\n" +
                "email=" + getEmail();
    }
}
