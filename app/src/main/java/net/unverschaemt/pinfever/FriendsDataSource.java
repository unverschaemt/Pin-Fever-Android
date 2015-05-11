package net.unverschaemt.pinfever;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_USERNAME, MySQLiteHelper.COLUMN_SCORE,
            MySQLiteHelper.COLUMN_AVATAR };

    public FriendsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User createFriend(long id, String username, int score, int avatar) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, id);
        values.put(MySQLiteHelper.COLUMN_USERNAME, username);
        values.put(MySQLiteHelper.COLUMN_SCORE, score);
        values.put(MySQLiteHelper.COLUMN_AVATAR, avatar);
        database.insert(MySQLiteHelper.TABLE_FRIENDS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToFriend(cursor);
        cursor.close();
        return newUser;
    }

    public void deleteFriend(User user) {
        long id = user.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_FRIENDS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<User> getAllFriends() {
        List<User> friends = new ArrayList<User>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User friend = cursorToFriend(cursor);
            friends.add(friend);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return friends;
    }

    private User cursorToFriend(Cursor cursor) {
        User friend = new User();
        friend.setId(cursor.getLong(0));
        friend.setUserName(cursor.getString(1));
        friend.setScore(cursor.getInt(2));
        friend.setAvatar(cursor.getInt(3));
        return friend;
    }
} 
