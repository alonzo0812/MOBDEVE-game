package com.example.mobdevemain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DPS = "dps";
    private static final String COLUMN_STAGE = "stage";
    private static final String COLUMN_ENEMY = "enemy";
    private static final String COLUMN_GOLD = "gold";
    private static final String COLUMN_ENEMY_HEALTH = "enemy_health";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_DPS + " REAL DEFAULT 25,"
                + COLUMN_STAGE + " INTEGER DEFAULT 1,"
                + COLUMN_ENEMY + " INTEGER DEFAULT 1,"
                + COLUMN_ENEMY_HEALTH + " REAL DEFAULT 100,"
                + COLUMN_GOLD + " REAL DEFAULT 50)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Add a new user to the database
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password); // You can hash the password here
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Return true if the insert is successful
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("users", "username=?", new String[]{username});
        db.close();
        return rowsDeleted > 0; // Return true if a user was deleted
    }

    // Check if username and password match
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Get user progress
    public Cursor getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);
    }

    // Update user progress
    public void updateUserProgress(String username, double dps, int stage, int enemy, double enemyHealth, double gold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DPS, dps);
        values.put(COLUMN_STAGE, stage);
        values.put(COLUMN_ENEMY, enemy);
        values.put(COLUMN_ENEMY_HEALTH, enemyHealth);
        values.put(COLUMN_GOLD, gold);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
    }

}
