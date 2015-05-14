package com.eric.ddcombatmanager;

/**
 * Created by Eric on 03/05/2015.
 */
public class Creature {
    int mInitiative, mInitiativeMod, mMaxHealth, mCurrentHealth;
    String mName;

    public Creature() {}

    public Creature(String name, int init, int maxHealth) {
        this(name, init, maxHealth, maxHealth);
    }

    public Creature(String name, int init, int maxHealth, int currentHealth) {
        mName = name;
        mInitiativeMod = init;
        mInitiative = init+1;
        mMaxHealth = maxHealth;
        mCurrentHealth = currentHealth;
    }

    public Creature(Creature c) {
        mName = c.mName;
        mInitiative = c.mInitiative;
        mInitiativeMod = c.mInitiativeMod;
        mMaxHealth = c.mMaxHealth;
        mCurrentHealth = c.mCurrentHealth;
    }
}
