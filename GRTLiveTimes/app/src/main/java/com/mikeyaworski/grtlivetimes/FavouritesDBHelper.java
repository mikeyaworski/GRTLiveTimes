package com.mikeyaworski.grtlivetimes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouritesDBHelper extends SQLiteOpenHelper {
    // if you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    // database schema information
    public static final String DATABASE_NAME = "Favourites.db";
    public static final String TABLE_NAME = "favourites";

    public static final String ID_FIELD = "_id";
    public static final String STOP_NUMBER_FIELD = "stop_number";
    public static final String ROUTE_NUMBER_FIELD = "route_number";
    public static final String DESCRIPTION_FIELD = "description";

    // queries
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                ID_FIELD + " INTEGER PRIMARY KEY," +
                STOP_NUMBER_FIELD + " text," +
                ROUTE_NUMBER_FIELD + " text," +
                DESCRIPTION_FIELD + " text" +
                " )";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public FavouritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // return all data from the database
    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public boolean insertFavourite(String stop_number, String route_number, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STOP_NUMBER_FIELD, stop_number);
        contentValues.put(ROUTE_NUMBER_FIELD, route_number);
        contentValues.put(DESCRIPTION_FIELD, description);
        db.insert(TABLE_NAME, "", contentValues);
        return true;
    }

    public boolean updateFavourite(String id, String stop_number, String route_number, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STOP_NUMBER_FIELD, stop_number);
        contentValues.put(ROUTE_NUMBER_FIELD, route_number);
        contentValues.put(DESCRIPTION_FIELD, description);

        // update rows corresponding to the matching id value
        db.update(TABLE_NAME, contentValues, ID_FIELD + " = ?", new String[]{id});

        return true;
    }

    public Integer deleteFavourite(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete rows corresponding to the matching id value
        return db.delete(TABLE_NAME, ID_FIELD + " = ?", new String[] {id});
    }
}