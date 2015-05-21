package com.eric.ddcombatmanager;

/**
 * Created by Eric on 16/05/2015.
 */
public class Combatant extends Creature {
    public int mCurrentHealth, mInitiative;
    public String mDisplayName;

    Combatant(Creature c) {
        super(c);
        mCurrentHealth = mMaxHealth;
        mInitiative = 10 + mInitiativeMod;
        mDisplayName = mName;
    }
}
