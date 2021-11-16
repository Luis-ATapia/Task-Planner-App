package com.example.team4_taskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.Statement;

public class RegisterActivity extends AppCompatActivity {

    ConnectionClass connection; // Class to save connection
    // Elements of the layout
    EditText user, pass, passConfirm, email;
    Button register;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // hide support bar
        //getSupportActionBar().hide();

        // set app to fullscreen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialize layout components
        user = (EditText) findViewById(R.id.nameTV);
        pass = (EditText) findViewById(R.id.passTV);
        email = (EditText) findViewById(R.id.emailTV);
        passConfirm = (EditText)findViewById(R.id.passConfirmTV);
        progressDialog = new ProgressDialog(this);

        register = (Button) findViewById(R.id.button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterBtn doregister = new RegisterBtn();
                doregister.execute("");
            }
        });
    }

    public class RegisterBtn extends AsyncTask<String, String, String>
    {
        // Because we need strings to use the text from the editText fields
        String nameStr = user.getText().toString();
        String emailStr = email.getText().toString();
        String passStr = pass.getText().toString();
        // These 2 will be used to define the connection state (success/failure)
        String msg = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute(){
            // Show a loading message so the user knows something is happening
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Check if all fields were used
            if(nameStr.trim().equals("") || emailStr.trim().equals("") || passStr.trim().equals("")){
                msg = "Please enter all fields";
            }
            else{
                connection = new ConnectionClass();
                try {
                    Connection con = connection.CONN();

                    // if connection is null, then something went wrong in ConnectionClass
                    if (con == null){
                        msg = "Please check your internet connection";
                    }
                    // Check if passwords match
                    else if(!passStr.equals(passConfirm.getText().toString())){
                        msg = "Passwords do not match";
                    }
                    // You can add more if statements to check for correct email or anything else you'll require.
                    else{
                        // Define query to send to server
                        String query = "INSERT INTO all_users VALUES (\"" + emailStr + "\", '" + nameStr + "', '" + passStr + "')";
                        System.out.println(query); // For testing purposes
                        // Create statement and send query
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        // Create a table for the user.
                        // createUserTable(String username);
                        String createUserTable = "CREATE TABLE `"+emailStr+"` ("
                                + "task VARCHAR(150) NOT NULL,"
                                + "date_time DATETIME NULL,"
                                + "am_pm BIT(1) NULL,"
                                + "PRIMARY KEY(task))";
                        stmt.executeUpdate(createUserTable);


                        // The user was able to successfully register.
                        msg = "Register Successful";
                        isSuccess = true;
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    msg = "Exceptions "+e;
                    e.printStackTrace();
                }
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String s){
            if(isSuccess){
                // Show user error or success message (depends on the doInBackground function)
                Toast.makeText(getBaseContext(), ""+msg, Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
            else{
                // Show user error message
                Toast.makeText(getBaseContext(), ""+msg, Toast.LENGTH_LONG).show();
            }

            progressDialog.hide();
        }
    }
}