package chiprogram.chipapp;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import chiprogram.chipapp.classes.CHIPLoaderSQL;
import chiprogram.chipapp.classes.Chapter;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link chiprogram.chipapp.ChapterSessionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link chiprogram.chipapp.ChapterSessionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ChapterSessionsFragment extends Fragment {

    private Chapter m_chapter;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param chapterId Name of chosen Chapter.
     * @return A new instance of fragment ChapterVideoFragment.
     */
    public static ChapterSessionsFragment newInstance(String chapterId) {
        ChapterSessionsFragment fragment = new ChapterSessionsFragment();
        Bundle args = new Bundle();
        args.putString(ChapterTabsActivity.CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }
    public ChapterSessionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String chapterId = getArguments().getString(ChapterTabsActivity.CHAPTER_ID);

            m_chapter = CHIPLoaderSQL.getChapterDetails(chapterId);
        } else {
            m_chapter = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chapter_sessions, container, false);

        TextView sessionsList = (TextView) view.findViewById(R.id.chap_sessions_title);
        if (m_chapter != null) {
            // TODO: change this
            String text = "Sessions for: \r\n";
            for (int i = 0; i < m_chapter.numSessions(); ++i) {
                text += (i+1) + ": " + m_chapter.getSessions(i).toString() + "\r\n\t.ppt link: " + m_chapter.getSessions(i).getPresentationLink() + "\r\n\r\n";
            }
            sessionsList.setText(text);
        } else {
            // TODO: change this
            sessionsList.setText("Oops!  Error loading Chapter!");
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
