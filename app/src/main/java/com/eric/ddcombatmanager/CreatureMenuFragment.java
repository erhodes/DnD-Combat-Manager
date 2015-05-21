package com.eric.ddcombatmanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Eric on 23/05/2015.
 */
public class CreatureMenuFragment extends ListFragment {
    CreatureManager mCreatureManager;

    public CreatureMenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreatureManager = CreatureManager.getInstance(getActivity());

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, mCreatureManager.getCreatureNames()));
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_creatures_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_creature) {
            getActivity().getFragmentManager().beginTransaction().replace(android.R.id.content,new CreatureDetails()).addToBackStack(null).commit();
        }
        return false;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        CreatureDetails creatureDetails = CreatureDetails.newInstance((String)l.getItemAtPosition(position));
        getActivity().getFragmentManager().beginTransaction().replace(android.R.id.content, creatureDetails).addToBackStack(null).commit();
    }
}
