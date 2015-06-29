package chiprogram.chipapp.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import chiprogram.chipapp.activities.NavItemTabsActivity;
import chiprogram.chipapp.activities.ProfileActivity;
import chiprogram.chipapp.database.CHIPLoaderSQL;
import chiprogram.chipapp.classes.CHIPUser;
import chiprogram.chipapp.classes.Content;
import chiprogram.chipapp.classes.NavItem;

/**
 * A list fragment representing a list of Chapters.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ContentListFragmentTab extends ListFragment {

    private ArrayList<Content> m_contentArray;
    private CHIPUser m_user;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all dummy containing this fragment must
     * implement. This mechanism allows dummy to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(boolean isNavItem, int index);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(boolean isNavItem, int index) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContentListFragmentTab() {
    }

    public static ContentListFragmentTab newInstance(CHIPUser user, String navItemId) {
        ContentListFragmentTab fragment = new ContentListFragmentTab();
        Bundle args = new Bundle();
        args.putParcelable(ProfileActivity.ARGUMENT_USER, user);
        args.putString(NavItemTabsActivity.CURRENT_ID, navItemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            m_user = getArguments().getParcelable(ProfileActivity.ARGUMENT_USER);
            String m_navItemId = getArguments().getString(NavItemTabsActivity.CURRENT_ID);
            NavItem ni = CHIPLoaderSQL.getInstance().getNavItem(m_navItemId, getActivity());
            m_contentArray = ni.getContentArray();
        } else {
            m_contentArray = null;
        }
    }

    public void setArrayAdapter() {
        // TODO: create a TableRow array instead for this and
        // have a way to mark a piece of content as "completed".
        setListAdapter(new ArrayAdapter<Content>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                m_contentArray));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setArrayAdapter();

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(false, position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
