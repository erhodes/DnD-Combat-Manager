package com.eric.ddcombatmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Eric on 05/05/2015.
 */
public class CreatureDetails extends Fragment {
    private static final String ARG_CREATURE_ID = "param_creature_id";
    private Creature mCreature;
    private CreatureManager mCreatureManager;
    private int mCreatureId = -1;
    private EditText mHealthEditText, mInitModEditText, mNameEditText;

    public static CreatureDetails newInstance(int creatureId) {
        CreatureDetails fragment = new CreatureDetails();
        Bundle args = new Bundle();
        args.putInt(ARG_CREATURE_ID, creatureId);
        fragment.setArguments(args);
        return fragment;
    };

    public CreatureDetails(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreatureManager = CreatureManager.getInstance(getActivity());
        if (getArguments() != null) {
            mCreatureId = getArguments().getInt(ARG_CREATURE_ID, -1);
        }
        if (mCreatureId > -1) {
            mCreature = mCreatureManager.getCreature(mCreatureId);
        } else {
            mCreature = new Creature();
        }
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.creature_details, container, false);

        mHealthEditText = (EditText)view.findViewById(R.id.maxHealthText);
        mInitModEditText = (EditText)view.findViewById(R.id.initiativeModEditText);
        mNameEditText = (EditText)view.findViewById(R.id.nameEditText);

        mHealthEditText.setText(mCreature.mMaxHealth + "");
        mHealthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCreature.mMaxHealth = Integer.valueOf(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mInitModEditText.setText("" + mCreature.mInitiativeMod);
        mInitModEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCreature.mInitiativeMod = Integer.valueOf(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNameEditText.setText(mCreature.mName);
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCreature.mName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_creature, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_creature) {
            //TODO: save to a database or something
            CreatureManager creatureManager = CreatureManager.getInstance(getActivity());
            creatureManager.saveCreature(mCreature);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
