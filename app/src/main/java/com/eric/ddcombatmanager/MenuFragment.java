package com.eric.ddcombatmanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Eric on 23/05/2015.
 */
public class MenuFragment extends Fragment {

    public MenuFragment() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        Button creaturesButton = (Button)view.findViewById(R.id.creatures_button);
        creaturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().replace(android.R.id.content,new CreatureMenuFragment()).addToBackStack(null).commit();
            }
        });

        Button encountersButton = (Button)view.findViewById(R.id.encounters_button);
        encountersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction().replace(android.R.id.content,new EncounterMenuFragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }

}
