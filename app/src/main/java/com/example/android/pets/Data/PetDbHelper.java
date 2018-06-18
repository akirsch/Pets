package com.example.android.pets.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.Data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    // string constants for database version number and name
    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "shelter.db";

    private final static String TEXT_TYPE = "TEXT ";
    private final static String INTEGER_TYPE = "INTEGER ";
    private final static String NOT_NULL = "NOT NULL ";
    private final static String COMMA_SEP = ",";
    private final static String PRIMARY_KEY = "PRIMARY KEY ";
    private final static String AUTOINCREMENT = "AUTOINCREMENT ";


    public PetDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private static final String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetEntry.TABLE_NAME + " " +
            "(" + PetEntry.COLUMN_ID  + " " + INTEGER_TYPE  + PRIMARY_KEY  + AUTOINCREMENT + COMMA_SEP +
            PetEntry.COLUMN_PET_NAME  + " " + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            PetEntry.COLUMN_PET_BREED + " " + TEXT_TYPE + COMMA_SEP +
            PetEntry.COLUMN_PET_GENDER + " " + INTEGER_TYPE + NOT_NULL + "DEFAULT " + 0 + COMMA_SEP +
            PetEntry.COLUMN_PET_WEIGHT + " " + INTEGER_TYPE + ");";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;


}
