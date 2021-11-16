package com.example.team4_taskapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class allTasksFragment extends Fragment {

    // Use ConnectionClass to make connection with database.
    ConnectionClass connection;

    // Would hold username to know which table to get tasks from.
    private String username = "example@email.com";
    private ArrayList<String> allTasks;

    private EditText editTextTask;
    private EditText editTextDate;
    private EditText editTextTime;
    private ToggleButton editToggle;

    private String taskText;
    private String dateText;
    private String timeText;

    private AllTasksListViewFragment nestedFrag;
    private FragmentTransaction ft;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // Constructor
    public allTasksFragment() {

    }

    public static allTasksFragment newInstance(String param1, String param2) {
        allTasksFragment fragment = new allTasksFragment();
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
    }

    // Listen for events and display information on the screen.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Hold the all_tasks fragment as a view object.
        View all_tasks_view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        Log.d("Checking if received", "the username" + username);
        connection = new ConnectionClass();



        // Find the floating action button and listen for click.
        FloatingActionButton fab1 = (FloatingActionButton) all_tasks_view.findViewById(R.id.addTaskBttn);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Context", "Floating action button clicked");

                // Display an alert dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("New Task");
                // To use a fragment layout on the alert dialog use getView() to provide ViewGroup
                View new_task_view = LayoutInflater.from(getContext()).inflate(R.layout.new_task_layout, (ViewGroup) getView(), false);

                // Find the input TextViews.
                editTextTask = (EditText) new_task_view.findViewById(R.id.edit_new_task);
                editTextDate = (EditText) new_task_view.findViewById(R.id.date_input);
                editTextTime = (EditText) new_task_view.findViewById(R.id.time_input);
                editToggle = (ToggleButton) new_task_view.findViewById(R.id.toggleBttn);

                // Make the connection to the database.
                connection = new ConnectionClass();


                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(new_task_view);

                // Listen for button clicks.
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Add Button", "was clicked in alert dialog");
                        // dialog.dismiss(); ****** doesn't seem to be needed.

                        // Grab the data the user left in the TextFields.
                        taskText = editTextTask.getText().toString().trim();
                        dateText = editTextDate.getText().toString().trim();
                        timeText = editTextTime.getText().toString().trim();

                        String toastMsg = "";

                        if (taskText.equals("") || dateText.equals("") || timeText.equals("")) {
                            toastMsg = "Please fill out all fields.";

                        }
                        else {
                            dateText = dateSeparate(dateText);
                        }

                        // Call method to take execute sql query to add task to database.
                        int amPM = toggleState(editToggle);
                        toastMsg = exeSQLQuery(taskText, dateText, timeText, amPM);
                        Toast.makeText(getContext(), ""+ toastMsg, Toast.LENGTH_LONG).show();

                        if (toastMsg.equals("Task Added")) {
                            populateTaskArr(connection);
                            // Now that the task was added to the database, refresh listview.
                            nestedFrag = new AllTasksListViewFragment(allTasks, username);
                            ft = getChildFragmentManager().beginTransaction();
                            ft.replace(R.id.frameLayout1_id, nestedFrag);
                            ft.commit();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel(); ****** doesn't seem to be needed.
                    }
                });

                builder.show();
            }
        });

        // Populate the array of task items with the user's tasks for the current day.
        allTasks = new ArrayList<String>();
        populateTaskArr(connection);
        Log.d("Size of array1: ", String.valueOf(allTasks.size()));

        // Begin the transaction
        Log.d("Begin", "the transaction");
        nestedFrag = new AllTasksListViewFragment(allTasks, username);
        ft = getChildFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.frameLayout2_id, nestedFrag);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();





        // Return the fragment view.
        return all_tasks_view;
    }

    private String populateTaskArr(ConnectionClass connection) {
        allTasks = new ArrayList<String>();
        String msg = "";
        //
        try {
            Connection con = connection.CONN();

            // If connection null then error happened in connection class.
            if (con == null){
                msg = "Please check your internet connection";
            }
            else{
                // Define query to send to server
                String query = "SELECT * FROM `" + username + "`";

                // Create statement and send query
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                // Add each task to array holding tasks.
                while(rs.next()) {
                    allTasks.add(rs.getString("task"));
                }
                msg = "Retrieved day's tasks";
            }
        } catch (Exception e) {
            msg = "Exceptions "+e;
            e.printStackTrace();
        }
        return msg;
    }


    // ********** Helper Methods ****************

    /// Method to separate a date.
    public String dateSeparate(String date) {
        String[] words = date.split("-|/");
        String dateStr;
        if (words[0].length() == 4) {
            dateStr = words[0] + "-" + words[1] +"-" + words[2];
        }
        else {
            dateStr = words[2] + "-" + words[0] +"-" + words[1];
        }
        return dateStr;
    }

    // Method formats inputs for sql query.
    public String formatQuery(String task, String date, String time, int dayState) {
        return "INSERT INTO `" + username + "` VALUES ('" + task + "', '" + date + " " + time + "', b'"+ dayState +"')";
    }

    // Check state of toggle button.
    public int toggleState(ToggleButton tBttn) {
        final int[] tBttnState = new int[1];
        tBttn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tBttnState[0] = 1;
                } else {
                    tBttnState[0] = 0;
                }
            }
        });
        return tBttnState[0];
    }

    // Method executes sql query to add to database.
    public String exeSQLQuery(String task, String date, String time, int dayState) {
        String msg = "";
        // If the user leaves out an text field prompt them to fill all fields.
        if (taskText.equals("") || dateText.equals("") || timeText.equals("")) {
            msg = "Please fill out all fields.";

        }
        // Otherwise try to execute the sql query.
        else {
            Log.d("Context", "Inside else");
            try {
                Connection con = connection.CONN();
                Log.d("Checking", "If connection is made");
                if (con == null) {
                    Log.d("Warning", "Connection not established");
                } else {
                    // Send query to add to database.
                    String addQuery = formatQuery(task, date, time, dayState);
                    Log.d("String", addQuery);

                    Statement st = con.createStatement();
                    st.executeUpdate(addQuery);
                    msg = "Task Added";
                }

            } catch (Exception e) {
                msg = "Sorry, something went wrong.";
                Log.d("Error", "Inside try failed");
                e.printStackTrace();
            }
        }
        return msg;
    }
}