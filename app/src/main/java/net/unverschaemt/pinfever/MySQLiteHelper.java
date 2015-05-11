package net.unverschaemt.pinfever;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kkoile on 11.05.15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_FRIENDS = "friends";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_AVATAR = "avatar";

    private static final String DATABASE_NAME = "pinfever.db";
    //TODO: change to version 1
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FRIENDS + "(" + COLUMN_ID
            + " long primary key, " + COLUMN_USERNAME
            + " text, " + COLUMN_SCORE + " integer, " + COLUMN_AVATAR + " integer );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        onCreate(db);
    }

}
