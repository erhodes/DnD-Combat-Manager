package com.eric.ddcombatmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 06/05/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "D&D_Database";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATURE_TABLE_NAME = "creatures";
    private static final String KEY_NAME = "name";
    private static final String KEY_INITIATIVE = "initiative";
    private static final String KEY_INITIATIVE_MOD = "initiative_modifier";
    private static final String KEY_CURRENT_HEALTH = "current_health";
    private static final String KEY_MAX_HEALTH = "max_health";
    private static final String CREATURE_TABLE_CREATE = "CREATE TABLE " + CREATURE_TABLE_NAME + " (" +
            KEY_NAME + " STRING PRIMARY KEY, " +
            KEY_INITIATIVE + " String DESC, " +
            KEY_INITIATIVE_MOD + " String, " +
            KEY_CURRENT_HEALTH + " int, " +
            KEY_MAX_HEALTH + " int)";
    private static final String SQL_GET_CREATURES = "SELECT * FROM creatures;";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATURE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public HashMap<String, Creature> getCreatures() {
        HashMap<String, Creature> result = new HashMap<String, Creature>();
        String[] projection = new String[]{KEY_NAME, KEY_INITIATIVE, KEY_INITIATIVE_MOD, KEY_CURRENT_HEALTH, KEY_MAX_HEALTH};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CREATURE_TABLE_NAME, projection, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (!cursor.isLast()) {
                cursor.moveToNext();
                Creature c = new Creature();
                c.mName = cursor.getString(0);
                c.mInitiative = cursor.getInt(1);
                c.mInitiativeMod = cursor.getInt(2);
                c.mCurrentHealth = cursor.getInt(3);
                c.mMaxHealth = cursor.getInt(4);
                result.put(c.mName, c);
            }
        }
        cursor.close();
        db.close();

        return result;
    }

    public void saveCreature(Creature c) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,c.mName);
        values.put(KEY_INITIATIVE_MOD, c.mInitiativeMod);
        values.put(KEY_INITIATIVE, c.mInitiative);
        values.put(KEY_CURRENT_HEALTH, c.mCurrentHealth);
        values.put(KEY_MAX_HEALTH, c.mMaxHealth);

        db.insert(CREATURE_TABLE_NAME, null, values);
        db.close();
    }

    public void removeCreature(Creature c) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = KEY_NAME + " LIKE ?";
        String[] selectionArgs = {c.mName};

        db.delete(CREATURE_TABLE_NAME,
                selection, selectionArgs);
        db.close();
    }

    public void updateCreature(Creature c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INITIATIVE_MOD, c.mInitiativeMod);
        values.put(KEY_INITIATIVE, c.mInitiative);
        values.put(KEY_CURRENT_HEALTH, c.mCurrentHealth);
        values.put(KEY_MAX_HEALTH, c.mMaxHealth);

        String selection = KEY_NAME + " LIKE ?";
        String[] selectionArgs = {c.mName};

        db.update(CREATURE_TABLE_NAME, values,
                selection, selectionArgs);
        db.close();
    }
}
