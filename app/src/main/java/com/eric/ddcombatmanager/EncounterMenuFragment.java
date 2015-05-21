package com.eric.ddcombatmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 */
public class EncounterMenuFragment extends ListFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EncounterMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper dbh = new DatabaseHelper(getActivity());

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, dbh.getEncounterList()));
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_encounters_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_encounter) {
            createNewEncounter();
        }
        return false;
    }

    public void createNewEncounter() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.text_input_dialog, null);
        TextView label = (TextView) view.findViewById(R.id.labelText);
        label.setText("Enter Encounter name");
        final EditText input = (EditText) view.findViewById(R.id.inputText);
        input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, CombatFragment.newInstance(input.getText().toString()))
                        .addToBackStack(null).commit();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        CombatFragment combatFragment = CombatFragment.newInstance((String)l.getItemAtPosition(position));
        getActivity().getFragmentManager().beginTransaction().replace(android.R.id.content, combatFragment).addToBackStack(null).commit();
    }

}
