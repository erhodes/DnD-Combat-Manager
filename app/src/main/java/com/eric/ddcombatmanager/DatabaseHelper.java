package com.eric.ddcombatmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

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
    private static final String CREATURE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CREATURE_TABLE_NAME + "(" +
            KEY_NAME + " STRING PRIMARY KEY, " +
            KEY_INITIATIVE + " String DESC, " +
            KEY_INITIATIVE_MOD + " String, " +
            KEY_CURRENT_HEALTH + " int, " +
            KEY_MAX_HEALTH + " int);";
    private static final String ENCOUNTER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + EncounterEntry.TABLE_NAME + "(" +
            EncounterEntry.COLUMN_TITLE + " STRING PRIMARY KEY);";
    private static final String COMBATANT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CombatantEntry.TABLE_NAME + "(" +
            CombatantEntry.COLUMN_ENCOUNTER_NAME + " String, " +
            CombatantEntry.COLUMN_DISPLAY_NAME + " String, " +
            CombatantEntry.COLUMN_BASE_NAME + " String, " +
            CombatantEntry.COLUMN_INITIATIVE + " int, " +
            CombatantEntry.COLUMN_CURRENT_HEALTH + " int, " +
            "foreign key(" + CombatantEntry.COLUMN_ENCOUNTER_NAME + ") references " + EncounterEntry.TABLE_NAME + "(" + EncounterEntry.COLUMN_TITLE + ") on delete cascade," +
            "primary key(" + CombatantEntry.COLUMN_DISPLAY_NAME + ", " + CombatantEntry.COLUMN_ENCOUNTER_NAME + "));";



    public static abstract class EncounterEntry implements BaseColumns {
        public static final String TABLE_NAME = "encounters";
        public static final String COLUMN_TITLE = "name";
    }

    public static abstract class CombatantEntry implements  BaseColumns {
        public static final String TABLE_NAME = "combatants";
        public static final String COLUMN_ENCOUNTER_NAME ="encounter";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_BASE_NAME = "base_name";
        public static final String COLUMN_INITIATIVE = "initiative";
        public static final String COLUMN_CURRENT_HEALTH = "current_health";
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATURE_TABLE_CREATE);
        db.execSQL(ENCOUNTER_TABLE_CREATE);
        db.execSQL(COMBATANT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public HashMap<String, Creature> getCreatures() {
        HashMap<String, Creature> result = new HashMap<String, Creature>();
        String[] projection = new String[]{KEY_NAME, KEY_INITIATIVE_MOD, KEY_MAX_HEALTH};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CREATURE_TABLE_NAME, projection, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (!cursor.isLast()) {
                cursor.moveToNext();
                Creature c = new Creature();
                c.mName = cursor.getString(0);
                c.mInitiativeMod = cursor.getInt(1);
                c.mMaxHealth = cursor.getInt(2);
                result.put(c.mName, c);
            }
        }
        cursor.close();
        db.close();

        return result;
    }

    public Creature getCreature(String name) {
        String[] projection = new String[]{KEY_NAME, KEY_INITIATIVE_MOD, KEY_MAX_HEALTH};
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_NAME + " IS '" + name + "'";
        Cursor cursor = db.query(CREATURE_TABLE_NAME, projection, selection, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Creature c = new Creature();
            c.mName = cursor.getString(0);
            c.mInitiativeMod = cursor.getInt(1);
            c.mMaxHealth = cursor.getInt(2);
            return c;
        }
        cursor.close();
        db.close();

        return null;
    }
    public void saveCreature(Creature c) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,c.mName);
        values.put(KEY_INITIATIVE_MOD, c.mInitiativeMod);
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
        values.put(KEY_MAX_HEALTH, c.mMaxHealth);

        String selection = KEY_NAME + " LIKE ?";
        String[] selectionArgs = {c.mName};

        db.update(CREATURE_TABLE_NAME, values,
                selection, selectionArgs);
        db.close();
    }

    public void saveEncounter(Encounter e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues encounterValues = new ContentValues();
        encounterValues.put(EncounterEntry.COLUMN_TITLE, e.mName);
        db.insert(EncounterEntry.TABLE_NAME, null, encounterValues);

        // save the individual combatants
        for (Combatant c : e.getCombatants()) {
            ContentValues combatantValues = new ContentValues();
            combatantValues.put(CombatantEntry.COLUMN_ENCOUNTER_NAME, e.mName);
            combatantValues.put(CombatantEntry.COLUMN_DISPLAY_NAME, c.mDisplayName);
            combatantValues.put(CombatantEntry.COLUMN_BASE_NAME, c.mName);
            combatantValues.put(CombatantEntry.COLUMN_INITIATIVE, c.mInitiative);
            combatantValues.put(CombatantEntry.COLUMN_CURRENT_HEALTH, c.mCurrentHealth);
            db.insert(CombatantEntry.TABLE_NAME, null, combatantValues);
        }
        db.close();
    }

    public void updateEncounter(Encounter e) {
        SQLiteDatabase db = getWritableDatabase();

        for (Combatant c : e.getCombatants()) {
            ContentValues combatantValues = new ContentValues();
            //combatantValues.put(CombatantEntry.COLUMN_ENCOUNTER_NAME, e.mName);
            //combatantValues.put(CombatantEntry.COLUMN_DISPLAY_NAME, c.mDisplayName);
            combatantValues.put(CombatantEntry.COLUMN_BASE_NAME, c.mName);
            combatantValues.put(CombatantEntry.COLUMN_INITIATIVE, c.mInitiative);
            combatantValues.put(CombatantEntry.COLUMN_CURRENT_HEALTH, c.mCurrentHealth);
            String selection = CombatantEntry.COLUMN_ENCOUNTER_NAME + " is ? and " + CombatantEntry.COLUMN_DISPLAY_NAME + " is ?";
            String[] selectionArgs = {e.mName, c.mDisplayName};
            db.update(CombatantEntry.TABLE_NAME, combatantValues, null, null);
        }
        db.close();
    }

    /**
     * Get a list of all encounters. Faster than loading them all up.
     * @return
     */
    public ArrayList<String> getEncounterList() {
        ArrayList<String> result = new ArrayList<String>();
        String[] projection = new String[]{EncounterEntry.COLUMN_TITLE};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(EncounterEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (!cursor.isLast()) {
                cursor.moveToNext();
                result.add(cursor.getString(0));
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public Encounter getEncounter(String name) {
        String[] projection = new String[] {CombatantEntry.COLUMN_BASE_NAME,
                CombatantEntry.COLUMN_INITIATIVE, CombatantEntry.COLUMN_CURRENT_HEALTH,
                CombatantEntry.COLUMN_DISPLAY_NAME};
        SQLiteDatabase db = getReadableDatabase();
        String selection = CombatantEntry.COLUMN_ENCOUNTER_NAME + " LIKE ?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query(CombatantEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Encounter result = new Encounter(name);
        if (cursor.getCount() > 0) {
            while (!cursor.isLast()) {
                cursor.moveToNext();

                Creature creature = getCreature(cursor.getString(0));

                Combatant c = new Combatant(creature);
                c.mInitiative = cursor.getInt(1);
                c.mCurrentHealth = cursor.getInt(2);
                c.mDisplayName = cursor.getString(3);
                result.addCombatant(c);
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public void removeEncounter(Encounter e) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = EncounterEntry.COLUMN_TITLE + " LIKE ?";
        String[] selectionArgs = {e.mName};

        db.delete(EncounterEntry.TABLE_NAME,
                selection, selectionArgs);
        db.close();
    }

    public void insertCombatant(Combatant c, Encounter e) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CombatantEntry.COLUMN_BASE_NAME,c.mName);
        values.put(CombatantEntry.COLUMN_ENCOUNTER_NAME, e.mName);
        values.put(CombatantEntry.COLUMN_CURRENT_HEALTH, c.mCurrentHealth);
        values.put(CombatantEntry.COLUMN_INITIATIVE, c.mInitiative);

        db.insert(CombatantEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void updateCombatant(Combatant c, Encounter e) {

    }
    public void removeCombatant(Combatant c, Encounter e) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = CombatantEntry.COLUMN_ENCOUNTER_NAME + " LIKE ? and " + CombatantEntry.COLUMN_DISPLAY_NAME + " is ?";
        String[] selectionArgs = {e.mName, c.mName};

        db.delete(CombatantEntry.TABLE_NAME,
                selection, selectionArgs);
        db.close();
    }
}
