package com.eric.ddcombatmanager;

/**
 * Created by Eric on 03/05/2015.
 */
public class Creature {
    int mInitiativeMod, mMaxHealth;
    String mName;

    public Creature() {}

    public Creature(String name, int init, int maxHealth) {
        this(name, init, maxHealth, maxHealth);
    }

    public Creature(String name, int init, int maxHealth, int currentHealth) {
        mName = name;
        mInitiativeMod = init;
        mMaxHealth = maxHealth;
    }

    public Creature(Creature c) {
        mName = c.mName;
        mInitiativeMod = c.mInitiativeMod;
        mMaxHealth = c.mMaxHealth;
    }
}
