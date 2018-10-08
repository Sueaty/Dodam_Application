package com.example.sm_pc.myapplication.setting.Baby;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    //database Name
    public static final String DATABASE_NAME = "Baby.db";
    public static final String TABLE_NAME = "Baby_Table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "EXPECTDATE";
    public static final String COL_3 = "COMPAREDAY";
    public static final String COL_4 = "TODAY";
    public static final String COL_5 = "DDAY";

    //default constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // implementation of method 'onCreate & onUpgrade'
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EXPECTDATE TEXT, COMPAREDAY TEXT, TODAY TEXT, DDAY INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String expectdate, String compareday, String today, String dday){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, expectdate);
        contentValues.put(COL_3, compareday);
        contentValues.put(COL_4, today);
        contentValues.put(COL_5, dday);
        /*
        [INSERT method]
        ** if insert is completed - returns the row ID of newly inserted row
        ** if insert failed - returns -1
        */

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1){ return false; }
        else{ return true; }
    }

    // function to query to get all data
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();

        //instance of Cursor class
        /*
        [Cursor] interface provides random read-write access to the result set returned by a db query
         */
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        // * stands for all
        return res;
    }

    public boolean updateData(String id, String expectdate, String compareday, String today, String dday){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, expectdate);
        contentValues.put(COL_3, compareday);
        contentValues.put(COL_4, today);
        contentValues.put(COL_5, dday);

        db.update(TABLE_NAME, contentValues, "id = ?", new String[]{id});
        return true;
    }

    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }



}
