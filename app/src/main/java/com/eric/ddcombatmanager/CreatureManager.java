package com.eric.ddcombatmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Eric on 06/05/2015.
 */
public class CreatureManager {
    private ArrayList<Creature> mCreatureList;
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
            mCreatureList = new ArrayList<Creature>();
            mCreatureList.add(new Creature("Joe", 3,111));
            mCreatureList.add(new Creature("Bob", 5, 12));
        }
    }

    public ArrayList<Creature> getCreatures() {
        return mCreatureList;
    }

    public void saveCreature(Creature c) {
        mDatabase.saveCreature(c);
    }

    public Creature getCreature(int creatureId) {
        return mCreatureList.get(creatureId);
    }
}
