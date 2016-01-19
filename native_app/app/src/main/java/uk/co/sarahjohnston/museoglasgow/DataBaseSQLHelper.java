package uk.co.sarahjohnston.museoglasgow;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataBaseSQLHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/uk.co.sarahjohnston.museoglasgow/databases/";
    private static String DB_NAME = "museums.db";
    private SQLiteDatabase museumDataBase;
    private final Context myContext;

    private static final String TABLE_NAME = "museums_info";

    //Database table column names
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "MuseumName";
    private static final String COLUMN_IMAGE = "MainPhoto";
    private static final String COLUMN_DESC = "Description";
    private static final String COLUMN_STREET1 = "StreetAddress1";
    private static final String COLUMN_STREET2 = "StreetAddress2";
    private static final String COLUMN_CITY = "City";
    private static final String COLUMN_COUNTY = "County";
    private static final String COLUMN_POSTCODE = "PostCode";

    private static final String COLUMN_LAT = "Latitude";
    private static final String COLUMN_LON = "Longitude";
    private static final String COLUMN_SUN1 = "Sunday_open";
    private static final String COLUMN_SUN2 = "Sunday_close";



    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_IMAGE, COLUMN_DESC, COLUMN_STREET1, COLUMN_STREET2, COLUMN_CITY, COLUMN_COUNTY, COLUMN_POSTCODE, COLUMN_LAT, COLUMN_LON, ""};

    private String openingHours;


    public DataBaseSQLHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDatabase() {
        createDB();
    }

    public void openDatabase() throws SQLiteException {
        String dbPath = DB_PATH + DB_NAME;
        museumDataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private void createDB() {
        boolean dbExist = DBExists();

        if (!dbExist) {
            Log.d("Message: ", "No database found going to add database");
            this.getReadableDatabase();
            Log.d("Message: ", "New database created now going to copy data");
            copyDBFromResource();

        }
        else {
            Log.d("Message: ", "Database found no further action needed");
        }
    }

    private boolean DBExists() {
        SQLiteDatabase db = null;

        try {
            String database_path = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(database_path, null, SQLiteDatabase.OPEN_READWRITE);
            db.setLocale(Locale.getDefault());
            db.setVersion(1);
        } catch (SQLiteException e) {
            Log.e("SqlHelper", "database not found");
        }

        if (db != null) {
            db.close();
        }

        return db != null;
    }

    private void copyDBFromResource() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String dbFilePath = DB_PATH + DB_NAME;

        try {
            inputStream = myContext.getAssets().open(DB_NAME);

            outputStream = new FileOutputStream(dbFilePath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Log.d("Message: ", "Done copying data over to database");

        } catch (IOException e) {
            throw new Error ("problem copying database from resources");
        }
    }

    public int getDatabaseCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }


    public List<Museum> getAllMuseums() {
        List<Museum> museums = new ArrayList<Museum>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                museums.add(new Museum(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return museums;

    }

    public Museum getMuseum(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Museum museum = new Museum(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        museum.setAddress(cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        museum.set_description(cursor.getString(3));
        museum.set_location(Double.parseDouble(cursor.getString(9)), Double.parseDouble(cursor.getString(10)));

        List<String> opening = new ArrayList<>();
        for (int i = 11; i < 26; i++) {
            opening.add(cursor.getString(i));
        }
        museum.set_openingHours(opening);

        db.close();
        cursor.close();
        return museum;
    }




}
