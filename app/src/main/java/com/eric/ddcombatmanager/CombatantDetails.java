package com.eric.ddcombatmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Eric on 26/05/2015.
 */
public class CombatantDetails extends Fragment {
    private static final String ARG_COMBATANT_NAME = "arg_combatant_name";
    private static final String ARG_ENCOUNTER_NAME = "arg_encounter_name";

    private Combatant mCombatant;
    private Encounter mEncounter;

    public static CombatantDetails newInstance(String name, String eName) {
        CombatantDetails combatantDetails = new CombatantDetails();
        Bundle args = new Bundle();
        args.putString(ARG_COMBATANT_NAME, name);
        args.putString(ARG_ENCOUNTER_NAME, eName);
        combatantDetails.setArguments(args);
        return combatantDetails;
    };

    public CombatantDetails() {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            mEncounter = db.getEncounter(getArguments().getString(ARG_ENCOUNTER_NAME));
            mCombatant = db.getCombatant(getArguments().getString(ARG_COMBATANT_NAME), mEncounter);
        }
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.combatant_details, container, false);

        TextView combatantName = (TextView)view.findViewById(R.id.combatant_name);
        combatantName.setText(mCombatant.mDisplayName);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_combatant_details, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_combatant) {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            db.removeCombatant(mCombatant, mEncounter);
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
