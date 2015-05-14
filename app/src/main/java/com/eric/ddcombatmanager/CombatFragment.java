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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CombatFragment extends Fragment implements AbsListView.OnItemClickListener {
    private CreatureManager mCreatureManager;
    private OnFragmentInteractionListener mListener;
    private boolean mBound = false;
    private AbsListView mListView;
    private CreatureAdapter mAdapter;
    private ArrayList<Creature> mCreatureList;

    public CombatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreatureManager = CreatureManager.getInstance(getActivity());
        mCreatureList = mCreatureManager.getCreatures();

        mAdapter = new CreatureAdapter(getActivity(),
                R.layout.creature_list_summary, mCreatureList);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCreatureList = mCreatureManager.getCreatures();
        mAdapter = new CreatureAdapter(getActivity(),
                R.layout.creature_list_summary, mCreatureList);
        mListView.setAdapter(mAdapter);
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
        } else if (id == R.id.action_add_new_creature) {
            addNewCreature();
        }
        return false;
    }

    public void addNewCreature() {
        CreatureDetails creatureDetails = CreatureDetails.newInstance(CreatureDetails.NEW_CREATURE);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(android.R.id.content,creatureDetails).addToBackStack(null).commit();
    }

    public class CreatureAdapter extends ArrayAdapter<Creature> {
        int mResourceId;
        Context mContext;
        public CreatureAdapter(Context context, int resourceId, List<Creature> creatureList) {
            super(context, resourceId, creatureList);
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

            final Creature creature = getItem(position);
            holder.healthView.setText(creature.mCurrentHealth + "/" + creature.mMaxHealth);
            holder.nameView.setText(creature.mName);
            holder.initView.setText(Integer.toString(creature.mInitiative));
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
                            creature.mInitiative = Integer.valueOf(input.getText().toString());
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
                            creature.mCurrentHealth = Integer.valueOf(input.getText().toString());
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
        Log.d("Eric", "item clicked " + view.toString());
        Creature c = (Creature)parent.getItemAtPosition(position);
        CreatureDetails creatureDetails = CreatureDetails.newInstance(c.mName);
        FragmentManager fragmentManager = getActivity().getFragmentManager();

        fragmentManager.beginTransaction().replace(android.R.id.content,creatureDetails).addToBackStack(null).commit();
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public void rollInitiative() {
        Random rand = new Random();
        for (Creature c : mCreatureList) {
            c.mInitiative = rand.nextInt(20) + 1 + c.mInitiativeMod;
        }
        updateInitiativeOrder();
    }
    public void updateInitiativeOrder() {
        Collections.sort(mCreatureList, new Comparator<Creature>() {
            @Override
            public int compare(Creature lhs, Creature rhs) {
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
