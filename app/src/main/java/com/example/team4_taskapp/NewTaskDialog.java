package com.example.team4_taskapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.sql.Connection;
import java.sql.Statement;

public class NewTaskDialog extends AppCompatDialogFragment{
    private EditText editTextTask;
    private EditText editTextDate;
    private EditText editTextTime;

    String taskText;
    String dateText;
    String timeText;

    ConnectionClass connection;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_task_layout, null);

        connection = new ConnectionClass();
        editTextTask = view.findViewById(R.id.edit_new_task);
        editTextDate = view.findViewById(R.id.date_input);
        editTextTime = view.findViewById(R.id.time_input);

        // Create Dialog Interface to allow user to make a new task.
        builder.setView(view).setTitle("New Task")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog box one the user is done creating new task.
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskText = editTextTask.getText().toString().trim();
                        dateText = editTextDate.getText().toString().trim();
                        timeText = editTextTime.getText().toString().trim();
                        Log.d("Something", taskText + " " + dateText + " " + timeText);
                        // Execute a mysql query to add to database.

                        if (taskText.equals("") || dateText.equals("") || timeText.equals("")) {
                            String msg = "Please fill out all fields.";
                            Log.d("Warning", msg);
                        }
                        else {
                            Log.d("Context", "Inside else");
                            try {
                                Connection con = connection.CONN();
                                Log.d("Checking", "If connection is made");
                                if (con == null) {
                                    Log.d("Warning", "Connection not established");
                                } else {
                                    // Send query to add to database.
                                    String addQuery;
                                    addQuery = "INSERT INTO tasks_data VALUES ('" + taskText + "', '" + dateText + " " + timeText + "', b'0')";
                                    Log.d("String", addQuery);

                                    Statement st = con.createStatement();
                                    st.executeUpdate(addQuery);
                                }

                            } catch (Exception e) {
                                Log.d("Error", "Inside try failed");
                                e.printStackTrace();
                            }
                        }

                    }
                });

        return builder.create();
    }
}