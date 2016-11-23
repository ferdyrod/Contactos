package com.ferdyrodriguez.contactos;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ferdyrod on 11/14/16.
 */

public class ContactContract {

    public static final String AUTHORITY = "com.ferdyrodriguez.contactoscontentprovider";

    public static final String TABLE_NAME = "contactos";

    public static final String URI = "content://" + AUTHORITY + "/" + TABLE_NAME;

    public static final Uri CONTENT_URI = Uri.parse(URI);

    public static final	int ALL_CONTACTS =	1;
    public static final int CONTACT_ID = 2;
    public static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, ALL_CONTACTS);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME+"/#", CONTACT_ID);
    }

    public static abstract class Contacto implements BaseColumns {
        public static final String COL_NAME = "nombre";
        public static final String COL_CELLPHONE = "movil";
        public static final String COL_PHONE = "telefono";
        public static final String COL_EMAIL = "email";

    }

    public static final String SINGLE_MIME = "vnd.android.cursor.item/vnd." +
            AUTHORITY + TABLE_NAME;

    public static final String MULTIPLE_MIME = "vnd.android.cursor.dir/vnd." +
            AUTHORITY + TABLE_NAME;
}