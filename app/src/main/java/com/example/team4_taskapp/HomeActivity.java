package com.example.team4_taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Grab the username sent by the calling activity.
        // Will be used to grab data and send data to the correct user's table in sql database.
        Intent intent = getIntent();
        String userName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Find the bottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // Get the navController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentHost);
        NavController navController = navHostFragment.getNavController();

        // Create instance of appBarConfiguration and change the title of screen accordingly.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.allTasksFragment, R.id.calendarFragment, R.id.statsFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Display the fragment that is selected.
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
/*
        // Find the floating action button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTaskBttn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Context", "Floating action button clicked");
                openNewTaskDialog();
                Log.d("Context", "Out of task dialog");

                LayoutInflater inflater = navHostFragment.getActivity().getLayoutInflater();
                View calView = inflater.inflate(R.layout.fragment_calendar, null);
                TextView calText = (TextView) calView.findViewById(R.id.calendar_id);
                calText.setText("Changed");
                navController.navigate(R.id.allTasksFragment);
            }
        });
 */

        // Override which fragment layout (screen) is displayed when an item in the bottom
        // navigation view is selected, allows more control over what happens.
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // If allTasksFragment or calendarFragment is selected show the
                // floating action button
                // If statsFragment is selected hide the button.
                switch (item.getItemId()) {
                    case R.id.allTasksFragment:
                        navController.navigate(R.id.allTasksFragment);
                        break;
                    case R.id.calendarFragment:

                        navController.navigate(R.id.calendarFragment);
                        break;
                    case R.id.statsFragment:

                        navController.navigate(R.id.statsFragment);
                        break;
                }
                return true;
            }
        });



    }
/*
    // Method to open new task dialog.
    public void openNewTaskDialog() {
        NewTaskDialog new_taskDialog = new NewTaskDialog();
        new_taskDialog.show(getSupportFragmentManager(), "Add New Task");
    }

 */
}