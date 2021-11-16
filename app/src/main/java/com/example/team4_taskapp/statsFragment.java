package com.example.team4_taskapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link statsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class statsFragment extends Fragment {

    private FloatingActionButton addNewTask;
    boolean check_something = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public statsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment statsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static statsFragment newInstance(String param1, String param2) {
        statsFragment fragment = new statsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d("Creating", "Creating stats fragment.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View stats_frag_view = inflater.inflate(R.layout.fragment_stats, container, false);

        addNewTask = (FloatingActionButton) stats_frag_view.findViewById(R.id.addTaskBttn);
        //addNewTask.hide();

        // Inflate the layout for this fragment
        Log.d("Creating", "Creating view of stats fragment.");
        return stats_frag_view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("Message", "We are attaching");
    }
/*
    public void getAView() {
        TextView holdTF = (TextView) getView().findViewById(R.id.textView3);
        holdTF.setText("Accessed");

    }
*/
}