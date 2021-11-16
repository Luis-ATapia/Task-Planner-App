package com.example.team4_taskapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;


public class AllTasksListViewFragment extends Fragment {

    private String username;

    private ArrayList<String> taskItems;
    private ArrayAdapter<String> itemsAdapter;
    private ListView listView;


    private FloatingActionButton addNewTask;
    private int checking_something = 0;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Constructor
    public AllTasksListViewFragment(ArrayList<String> curTasks, String username) {
        taskItems = curTasks;
        this.username = username;
        for (String i : taskItems) {
            Log.d("Checking in constructor: ", i);
        }
    }


    public static AllTasksListViewFragment newInstance(String param1, String param2) {
        ArrayList<String> nothing = new ArrayList<>();
        String uName = "";
        AllTasksListViewFragment fragment = new AllTasksListViewFragment(nothing, uName);
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
        Log.d("Creating", "Creating Testing fragment.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View testing_frag_view = inflater.inflate(R.layout.cal_list_view_fragment, container, false);

        //addNewTask = (FloatingActionButton) stats_frag_view.findViewById(R.id.addTaskBttn);
        //addNewTask.hide();
        //Button holdBttn = testing_frag_view.findViewById(R.id.testBttn);
        //holdBttn.setText(String.valueOf(checking_something));
        checking_something = checking_something + 1;

        // Find the listview
        listView = (ListView) testing_frag_view.findViewById(R.id.cal_list_view_id);
/*
        // Find add button
        bttn = testing_frag_view.findViewById(R.id.testBttn);
        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemsFromArray(view);
            }
        });

 */

        // Arraylist to hold task items;
        for (String item : taskItems) {
            Log.d("Checking if array was received", "Checking now" + item);
        }

        itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, taskItems);
        listView.setAdapter(itemsAdapter);
        //addItemsFromArray();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = testing_frag_view.getContext();

                // Remove task from database.
                ConnectionClass connection = new ConnectionClass();
                String msg = "";
                try {
                    Connection con = connection.CONN();
                    Log.d("Checking", "If connection is made");
                    if (con == null) {
                        Log.d("Warning", "Connection not established");
                        msg = "Please check your internet connection...";
                    } else {
                        // Send query to add to database.
                        String task_to_remove = taskItems.get(i);
                        String addQuery = "DELETE from `" + username + "` WHERE task = '" + task_to_remove + "'";
                        Log.d("String", addQuery);

                        Statement st = con.createStatement();
                        st.executeUpdate(addQuery);
                        msg = "Task Complete";

                        // Remove from list view and update it.
                        taskItems.remove(i);
                        itemsAdapter.notifyDataSetChanged();
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.d("Error", "Inside try failed");
                    msg = "Sorry, something went wrong.";
                    e.printStackTrace();
                }

                return true;
            }
        });
        //setUpListViewListener();



        // Inflate the layout for this fragment
        Log.d("Creating", "Creating view of Testing fragment number " + checking_something);
        return testing_frag_view;
    }
/*
    private void setUpListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = testing_frag_view.getContext();
                Toast.makeText(context, "Task Complete", Toast.LENGTH_LONG).show();

                taskItems.remove(i);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });


    }
 */

    private void addItemsFromArray() {
        for (String i : taskItems) {
            itemsAdapter.add(i);
            Log.d("Checking array items", " Now: " + i);
        }
        //itemsAdapter.add(taskText);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("Message", "We are attaching Testing View");
    }

}