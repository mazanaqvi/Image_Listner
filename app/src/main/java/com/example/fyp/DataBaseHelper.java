package com.example.fyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.FontsContract;
import android.view.View;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

private static final String DATABASE_NAME = "REGULAR_USER_RECORD1";
private static final String TABLE_NAME = "REGULAR_USER_DATA";
//private static final String COL_1 = "Username";
//private static final String COL_2 = "password";
    public DataBaseHelper(@Nullable Context context){
        super(context,DATABASE_NAME,null,1);//, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"( Username Text primary key,password Text not null)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
     onCreate(db);

    }
    public  boolean insert_hard_coded_data() {
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues  = new ContentValues();
            contentValues.put("Username","zain");
            contentValues.put("password","abbas123");
            long result = db.insert(TABLE_NAME,null,contentValues);
            if(result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }
    public  boolean CheckUser(String username,String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from REGULAR_USER_DATA where username =? and password=?",new String[] {username,password});
        int count = cursor.getCount();
        db.close();
        cursor.close();
        if(count>0)
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }
}
