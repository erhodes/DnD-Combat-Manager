package com.eric.ddcombatmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Eric on 05/05/2015.
 */
public class CreatureDetails extends Fragment {

    private Creature mCreature;
    private EditText mHealthEditText, mInitModEditText, mNameEditText;


    public CreatureDetails(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.creature_details, container, false);

        mHealthEditText = (EditText)view.findViewById(R.id.maxHealthText);
        mInitModEditText = (EditText)view.findViewById(R.id.initiativeModEditText);
        mNameEditText = (EditText)view.findViewById(R.id.nameEditText);

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

    public void setCreature(Creature c) {
        mCreature = c;
    }
}
