package com.eric.ddcombatmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.eric.ddcombatmanager.dummy.DummyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class CombatFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CreatureAdapter mAdapter;

    private ArrayList<Creature> mCreatureList;

    // TODO: Rename and change types of parameters
    public static CombatFragment newInstance(String param1, String param2) {
        CombatFragment fragment = new CombatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CombatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // some sample creatures
        mCreatureList = new ArrayList<Creature>();
        mCreatureList.add(new Creature("Joe", 3,111));
        mCreatureList.add(new Creature("Bob", 5,12));

        mAdapter = new CreatureAdapter(getActivity(),
                R.layout.creature_list_summary, mCreatureList);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(mResourceId, null);
                holder = new ViewHolder();
                holder.healthView = (TextView) convertView.findViewById(R.id.healthText);
                holder.nameView = (TextView) convertView.findViewById(R.id.nameText);
                holder.initView = (TextView) convertView.findViewById(R.id.initiativeText);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Creature creature = getItem(position);
            holder.healthView.setText(creature.mCurrentHealth + "/" + creature.mMaxHealth);
            holder.nameView.setText(creature.mName);
            holder.initView.setText(Integer.toString(creature.mInitiative));
            holder.initView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Eric","init clicked, in the future this will open a dialog");
                }
            });
            return convertView;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Eric", "item clicked " + view.toString());
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
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
