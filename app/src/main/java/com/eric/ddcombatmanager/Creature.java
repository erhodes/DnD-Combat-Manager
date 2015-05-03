package com.eric.ddcombatmanager;

/**
 * Created by Eric on 03/05/2015.
 */
public class Creature {
    int mInitiative, mInitiativeMod, mMaxHealth, mCurrentHealth;
    String mName;

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

}
