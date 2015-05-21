package com.eric.ddcombatmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eric on 06/05/2015.
 */
public class CreatureManager {
    private HashMap<String, Creature> mCreatureList;
    private static CreatureManager sInstance;
    private Context mContext;

    public static CreatureManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new CreatureManager(c);
        }
        return sInstance;
    }


    private DatabaseHelper mDatabase;

    CreatureManager (Context context) {
        mContext = context;
        mDatabase = new DatabaseHelper(mContext);
        mCreatureList = mDatabase.getCreatures();
        Log.d("Eric", "size is " + mCreatureList.size());
        if (mCreatureList.size() == 0) {
            mCreatureList = new HashMap<String, Creature>();
            mCreatureList.put("Joe", new Creature("Joe", 3,111));
            mCreatureList.put("Bob", new Creature("Bob", 5, 12));
        }
    }

    public ArrayList<Creature> getCreatures() {
        ArrayList<Creature> creatures = new ArrayList<Creature>();
        for (Creature c : mCreatureList.values()) {
            creatures.add(c);
        }
        return creatures;
    }

    public ArrayList<String> getCreatureNames() {
        ArrayList<String> creatures = new ArrayList<String>();
        for (Creature c : mCreatureList.values()) {
            creatures.add(c.mName);
        }
        return creatures;
    }

    /**
     * Returns a copy of the given creature. The copy is identical save the name, which will be
     * c.name1, c.name2, etc
     * @param c
     * @return
     */
    public Creature duplicateCreature(Creature c) {
        String s = c.mName;
        String last = s.substring(s.length()-1);
        int num = 1;
        int usableLength = s.length();
        try {
            num = Integer.valueOf(last);
            num++;
            usableLength--;
        } catch (NumberFormatException ex) {
            Log.d("Eric","exception occured");
        }
        String first = s.substring(0, usableLength);
        String newName = first + num;
        Creature newCreature = new Creature(c);
        newCreature.mName = newName;
        Log.d("Eric","first " + first);
        Log.d("Eric","Last " + last);
        Log.d("Eric","newName " + newName);
        return newCreature;
    }

    public void saveCreature(Creature c) {
        if (mCreatureList.get(c.mName) == null) {
            mCreatureList.put(c.mName, c);
            mDatabase.saveCreature(c);
        } else {
            mCreatureList.put(c.mName,c);
            mDatabase.updateCreature(c);
        }
    }

    public void removeCreature(Creature c) {
        if (mCreatureList.remove(c.mName) != null) {
            mDatabase.removeCreature(c);
        }
    }

    public Creature getCreature(String name) {
        return mCreatureList.get(name);
    }
}
