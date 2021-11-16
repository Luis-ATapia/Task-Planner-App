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
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.team4_taskapp.MESSAGE";

    private ConnectionClass connection; // Class to save connection
    // Elements of the layout
    private EditText pass, email;
    private Button registerGo, login;
    private ProgressDialog progressDialog;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide support bar
        //getSupportActionBar().hide();

        // set app to fullscreen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialize layout components
        pass = (EditText) findViewById(R.id.loginPass);
        email = (EditText) findViewById(R.id.loginEmail);
        progressDialog = new ProgressDialog(this);
        connection = new ConnectionClass();
        registerGo = (Button) findViewById(R.id.registerBtn);
        login = (Button) findViewById(R.id.loginBtn);

        // Listen for login button click.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginBtn dologin = new LoginBtn();
                dologin.execute("");
            }
        });

    }

    public void goRegister(View view){
        Intent intent = new Intent(MainActivity.this, com.example.team4_taskapp.RegisterActivity.class);
        startActivity(intent);
    }

    public class LoginBtn extends AsyncTask<String, String, String>
    {
        // Because we need strings to use the text from the editText fields
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
            if(emailStr.trim().equals("") || passStr.trim().equals("")){
                msg = "Please enter all fields";
            }
            else{
                try {
                    Connection con = connection.CONN();

                    // If connection null then error happened in connection class.
                    if (con == null){
                        msg = "Please check your internet connection";
                    }
                    else{
                        // Define query to send to server
                        String query = "SELECT * FROM all_users where `usernames`='"+emailStr+"';";
                        System.out.println(query); // For testing purposes
                        // Create statement and send query
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        // Success
                        if(rs.next()) {
                            if(rs.getString("passwords").equals(passStr)) {
                                msg = "Login Successful";
                                isSuccess = true;
                            }
                        }
                        if(!isSuccess) {
                            msg = "Incorrect username or password";
                        }
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
                // Show user success message
                Toast.makeText(getBaseContext(), ""+msg, Toast.LENGTH_LONG).show();

                // Use intent to start the home screen activity.
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra(EXTRA_MESSAGE, emailStr);
                startActivity(intent);


            }
            else{
                // Show user error message
                Toast.makeText(getBaseContext(), ""+msg, Toast.LENGTH_LONG).show();
            }

            progressDialog.hide();
        }
    }

}