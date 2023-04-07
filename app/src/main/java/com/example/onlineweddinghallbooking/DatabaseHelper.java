package com.example.onlineweddinghallbooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    long result;

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "FILTER_ACTIVITY_INSTANCE";
    private static final String COL1 = "ID";
    private static final String COL2 = "Sort_By";
    private static final String COL3 = "Minimum_Price";
    private static final String COL4 = "Maximum_Price";
    private static final String COL5 = "Minimum_Seating_Capacity";
    private static final String COL6 = "Maximum_Seating_Capacity";
    private static final String COL7 = "Minimum_Parking_Capacity";
    private static final String COL8 = "Maximum_Parking_Capacity";
    private static final String COL9 = "Radius";
    private static final String COL10 = "Availability";



    public DatabaseHelper(Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String create_table = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, " + COL3 + " TEXT, "
                + COL4 + " TEXT, " + COL5 + " TEXT, " + COL6 + " TEXT, " + COL7 + " TEXT, " + COL8 + " TEXT, " + COL9 + " TEXT, " + COL10 + " TEXT)";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String sortBy, String minimumPrice, String maximumPrice, String minimumSeatingCapacity, String maximumSeatingCapacity,
                           String minimumParkingCapacity, String maximumParkingCapacity, String radius, String availability)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, sortBy);
        contentValues.put(COL3, minimumPrice);
        contentValues.put(COL4, maximumPrice);
        contentValues.put(COL5, minimumSeatingCapacity);
        contentValues.put(COL6, maximumSeatingCapacity);
        contentValues.put(COL7, minimumParkingCapacity);
        contentValues.put(COL8, maximumParkingCapacity);
        contentValues.put(COL9, radius);
        contentValues.put(COL10, availability);

        result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getData()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = sqLiteDatabase.rawQuery(query, null);

        return data;
    }

    public  void truncateTable()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, null, null);
    }
}
