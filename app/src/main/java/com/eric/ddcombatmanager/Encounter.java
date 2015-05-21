package com.eric.ddcombatmanager;

import java.util.ArrayList;

/**
 * Created by Eric on 16/05/2015.
 */
public class Encounter {
    public String mName;
    private ArrayList<Combatant> mCombatantList;

    public Encounter(String s) {
        mName = s;
        mCombatantList = new ArrayList<Combatant>();
    }

    public ArrayList<Combatant> getCombatants() {
        return mCombatantList;
    }

    public void addCombatant(Combatant c) {
        mCombatantList.add(c);
    }

    public void addCreature(Creature c) {
        mCombatantList.add(new Combatant(c));
    }
}
