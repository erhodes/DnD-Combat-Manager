package com.eric.ddcombatmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CombatFragment extends Fragment implements AbsListView.OnItemClickListener {
    private static final String ARG_ENCOUNTER_NAME = "arg_encounter_name";
    private CreatureManager mCreatureManager;
    private OnFragmentInteractionListener mListener;
    private boolean mBound = false;
    private AbsListView mListView;
    private CombatantAdapter mAdapter;
    private ArrayList<Combatant> mCombatantList;
    private Encounter mEncounter;

    public static CombatFragment newInstance(String encounterName) {
        CombatFragment fragment = new CombatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENCOUNTER_NAME, encounterName);
        fragment.setArguments(args);
        return fragment;
    }

    public CombatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreatureManager = CreatureManager.getInstance(getActivity());
        if (getArguments() != null) {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            mEncounter = db.getEncounter(getArguments().getString(ARG_ENCOUNTER_NAME));
        } else {
            mEncounter = new Encounter("Default");
            for (Creature c : mCreatureManager.getCreatures()) {
                mEncounter.addCreature(c);
            }
        }

        Log.d("Eric","encounter name is " + mEncounter.mName);
        mCombatantList = mEncounter.getCombatants();

        mAdapter = new CombatantAdapter(getActivity(),
                R.layout.creature_list_summary, mCombatantList);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCombatantList = mEncounter.getCombatants();
        mAdapter = new CombatantAdapter(getActivity(),
                R.layout.creature_list_summary, mCombatantList);
        mListView.setAdapter(mAdapter);
        updateInitiativeOrder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_combat_fragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_roll_init) {
            rollInitiative();
            return true;
        } else if (id == R.id.action_save_encounter) {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            db.updateEncounter(mEncounter);
        } else if (id == R.id.action_add_new_creature) {
            addNewCreature();
        } else if (id == R.id.action_remove_encounter) {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            db.removeEncounter(mEncounter);
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    public void addNewCreature() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_creature_selection, null);
        final Spinner creatureSpinner = (Spinner) view.findViewById(R.id.spinner_creature);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mCreatureManager.getCreatureNames());
        creatureSpinner.setAdapter(adapter);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Combatant c = new Combatant(mCreatureManager.getCreature((String)creatureSpinner.getSelectedItem()));
                mEncounter.addCombatant(c);
                DatabaseHelper db = new DatabaseHelper(getActivity());
                db.insertCombatant(c,mEncounter);
                mAdapter.notifyDataSetChanged();
                mListView.invalidate();
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

    public class CombatantAdapter extends ArrayAdapter<Combatant> {
        int mResourceId;
        Context mContext;
        public CombatantAdapter(Context context, int resourceId, List<Combatant> combatantList) {
            super(context, resourceId, combatantList);
            mResourceId = resourceId;
            mContext = context;
        }

        protected class ViewHolder {
            TextView nameView, initView, healthView;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(mResourceId, null);
                holder = new ViewHolder();
                holder.healthView = (TextView) convertView.findViewById(R.id.healthText);
                holder.nameView = (TextView) convertView.findViewById(R.id.nameText);
                holder.initView = (TextView) convertView.findViewById(R.id.initiativeText);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Combatant combatant = getItem(position);
            holder.healthView.setText(combatant.mCurrentHealth + "/" + combatant.mMaxHealth);
            holder.nameView.setText(combatant.mName);
            holder.initView.setText(Integer.toString(combatant.mInitiative));
            holder.initView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View view = inflater.inflate(R.layout.text_input_dialog, null);
                    TextView label = (TextView) view.findViewById(R.id.labelText);
                    label.setText(R.string.initiative);
                    final EditText input = (EditText) view.findViewById(R.id.inputText);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(view);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            combatant.mInitiative = Integer.valueOf(input.getText().toString());
                            updateInitiativeOrder();
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
            });
            holder.healthView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View view = inflater.inflate(R.layout.text_input_dialog, null);
                    TextView label = (TextView) view.findViewById(R.id.labelText);
                    label.setText(R.string.current_hp);
                    final EditText input = (EditText) view.findViewById(R.id.inputText);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(view);
                    builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            combatant.mCurrentHealth = Integer.valueOf(input.getText().toString());
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
            });
            return convertView;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Creature c = (Creature)parent.getItemAtPosition(position);
        CreatureDetails creatureDetails = CreatureDetails.newInstance(c.mName);
        FragmentManager fragmentManager = getActivity().getFragmentManager();

        fragmentManager.beginTransaction().replace(android.R.id.content,creatureDetails).addToBackStack(null).commit();
    }

    public void rollInitiative() {
        Random rand = new Random();
        for (Combatant c : mCombatantList) {
            c.mInitiative = rand.nextInt(20) + 1 + c.mInitiativeMod;
        }
        updateInitiativeOrder();
    }
    public void updateInitiativeOrder() {
        Collections.sort(mCombatantList, new Comparator<Combatant>() {
            @Override
            public int compare(Combatant lhs, Combatant rhs) {
                if (rhs.mInitiative > lhs.mInitiative) {
                    return 1;
                } else if (rhs.mInitiative < lhs.mInitiative) {
                    return -1;
                } else if (rhs.mInitiativeMod > lhs.mInitiativeMod) {
                    return 1;
                } else if (rhs.mInitiativeMod < lhs.mInitiativeMod) {
                    return -1;
                }
                // no further tiebreakers come to mind
                return 0;
            }
        });
        mAdapter.notifyDataSetChanged();
    }
    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
