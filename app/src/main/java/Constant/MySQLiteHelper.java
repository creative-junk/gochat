package Constant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "crysoftUsers";

    //create table sample
    private static final String TABLE_SPECIAL_USERS = "special_users";

    //create column of table sample
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SPECIAL_USERS_TABLE = "CREATE TABLE " + TABLE_SPECIAL_USERS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_NAME + " TEXT" + ")";

        db.execSQL(CREATE_SPECIAL_USERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS sample"+ TABLE_SPECIAL_USERS);

        onCreate(db);

    }


    public void insertSpecialUsers(String name){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);


        db.insert(TABLE_SPECIAL_USERS, null, values);
        db.close();
    }

    public boolean search(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SPECIAL_USERS, null, " name=?",
                new String[]{name}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return false;
        }

        return true;
    }

}
