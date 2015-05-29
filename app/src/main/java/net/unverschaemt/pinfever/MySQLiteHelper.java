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
    public static final String FRIENDS_COLUMN_ID = "_id";
    public static final String FRIENDS_COLUMN_USERNAME = "username";
    public static final String FRIENDS_COLUMN_SCORE = "score";
    public static final String FRIENDS_COLUMN_AVATAR = "avatar";

    public static final String TABLE_GAMES = "games";
    public static final String GAMES_COLUMN_ID = "_id";
    public static final String GAMES_COLUMN_STATE = "state";
    public static final String GAMES_COLUMN_ACTIVE_ROUND = "activeRound";

    public static final String TABLE_QUESTIONS = "questions";
    public static final String QUESTIONS_COLUMN_ID = "_id";
    public static final String QUESTIONS_COLUMN_TEXT = "text";
    public static final String QUESTIONS_COLUMN_ROUND = "round";
    public static final String QUESTIONS_COLUMN_ANSWER_LAT = "answerLat";
    public static final String QUESTIONS_COLUMN_ANSWER_LONG = "answerLong";
    public static final String QUESTIONS_COLUMN_STATE = "state";
    public static final String QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE = "participantWhoWon";

    public static final String TABLE_ROUNDS = "rounds";
    public static final String ROUNDS_COLUMN_ID = "_id";
    public static final String ROUNDS_COLUMN_CATEGORY = "category";
    public static final String ROUNDS_COLUMN_GAME = "game";

    public static final String TABLE_TURNINFORMATION = "turninformation";
    public static final String TURNINFORMATION_COLUMN_ID = "_id";
    public static final String TURNINFORMATION_COLUMN_QUESTION = "question";
    public static final String TURNINFORMATION_COLUMN_PARTICIPANT = "participant";
    public static final String TURNINFORMATION_COLUMN_LAT = "lat";
    public static final String TURNINFORMATION_COLUMN_LONG = "long";
    public static final String TURNINFORMATION_COLUMN_DISTANCE = "distance";

    public static final String TABLE_PARTICIPANTS = "participants";
    public static final String PARTICIPANTS_COLUMN_ID = "_id";
    public static final String PARTICIPANTS_COLUMN_PLAYER = "player";
    public static final String PARTICIPANTS_COLUMN_GAME = "game";
    public static final String PARTICIPANTS_COLUMN_STATE = "status";
    public static final String PARTICIPANTS_COLUMN_SCORE = "score";


    private static final String DATABASE_NAME = "pinfever.db";
    //TODO: change to version 1
    private static final int DATABASE_VERSION = 8;

    // Database creation sql statement

    private static final String CREATE_FRIENDS_DATABASE = "create table "
            + TABLE_FRIENDS + "(" + FRIENDS_COLUMN_ID
            + " text, " + FRIENDS_COLUMN_USERNAME
            + " text, " + FRIENDS_COLUMN_SCORE + " integer, " + FRIENDS_COLUMN_AVATAR + " text );";

    private static final String CREATE_GAMES_DATABASE = "create table "
            + TABLE_GAMES + "(" + GAMES_COLUMN_ID
            + " long primary key, " + GAMES_COLUMN_STATE
            + " integer, " + GAMES_COLUMN_ACTIVE_ROUND + " long );";

    private static final String CREATE_QUESTIONS_DATABASE = "create table " + TABLE_QUESTIONS + "("
            + QUESTIONS_COLUMN_ID + " long primary key, "
            + QUESTIONS_COLUMN_TEXT + " text, "
            + QUESTIONS_COLUMN_ROUND + " long, "
            + QUESTIONS_COLUMN_ANSWER_LAT + " float, "
            + QUESTIONS_COLUMN_ANSWER_LONG + " float, "
            + QUESTIONS_COLUMN_STATE + " integer, "
            + QUESTIONS_COLUMN_PARTICIPANT_WHO_ONE + " long );";

    private static final String CREATE_ROUNDS_DATABASE = "create table " + TABLE_ROUNDS + "("
            + ROUNDS_COLUMN_ID + " long primary key, "
            + ROUNDS_COLUMN_CATEGORY + " text, "
            + ROUNDS_COLUMN_GAME + " long );";

    private static final String CREATE_TURNINFORMATION_DATABASE = "create table " + TABLE_TURNINFORMATION + "("
            + TURNINFORMATION_COLUMN_ID + " long primary key, "
            + TURNINFORMATION_COLUMN_QUESTION + " long, "
            + TURNINFORMATION_COLUMN_PARTICIPANT + " long, "
            + TURNINFORMATION_COLUMN_LAT + " float, "
            + TURNINFORMATION_COLUMN_LONG + " float, "
            + TURNINFORMATION_COLUMN_DISTANCE + " float );";

    private static final String CREATE_PARTICIPANTS_DATABASE = "create table " + TABLE_PARTICIPANTS + "("
            + PARTICIPANTS_COLUMN_ID + " text primary key, "
            + PARTICIPANTS_COLUMN_PLAYER + " text, "
            + PARTICIPANTS_COLUMN_GAME + " long, "
            + PARTICIPANTS_COLUMN_STATE + " int, "
            + PARTICIPANTS_COLUMN_SCORE + " int );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_FRIENDS_DATABASE);
        database.execSQL(CREATE_GAMES_DATABASE);
        database.execSQL(CREATE_QUESTIONS_DATABASE);
        database.execSQL(CREATE_ROUNDS_DATABASE);
        database.execSQL(CREATE_TURNINFORMATION_DATABASE);
        database.execSQL(CREATE_PARTICIPANTS_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old dataObject");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TURNINFORMATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
        onCreate(db);
    }

    public void dropTables(SQLiteDatabase db) {
        Log.w(MySQLiteHelper.class.getName(),
                "Dropped All Tables");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TURNINFORMATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
        onCreate(db);
    }

}
