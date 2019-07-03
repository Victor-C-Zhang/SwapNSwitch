package com.example.numbergame;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.numbergame.customViews.NumberView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private static final int[][] BUTTON_IDS = {
            {R.id.buttonZero, R.id.buttonOne, R.id.buttonTwo},
            {R.id.buttonThree, R.id.buttonFour, R.id.buttonFive},
            {R.id.buttonSix, R.id.buttonSeven, R.id.buttonEight},
    };
    public NumberView[][] viewIDs;
    private boolean inited;
    private OnFragmentInteractionListener mListener;

    public GoalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GoalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoalFragment newInstance() {
        GoalFragment fragment = new GoalFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_goal, container, false);
        viewIDs = new NumberView[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                viewIDs[i][j] = root.findViewById(BUTTON_IDS[i][j]);
        return root;
    }

    public void init(){
        inited = true;
    }
    public boolean isInited() {return inited;}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        void onInitGoal(ArrayList<Integer> goalList);
    }
}
