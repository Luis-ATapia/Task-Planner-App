package com.example.team4_taskapp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://DB-IPaddress:Port/DBName";
    String un = "root";
    String pass = "Lt1234?";

    public Connection CONN() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;
        Log.d("Context", "Inside Conn()");
        try {
            // Create the connection to database.
            Class.forName(driver);
            conn = DriverManager.getConnection(url, un, pass);
            Log.d("No issues", "With first try");

        } catch (ClassNotFoundException e) {
            Log.d("Error", "In Connection");
            e.printStackTrace();
        } catch (SQLException throwables) {
            Log.d("Error", "In connection2");
            throwables.printStackTrace();
        }

        Log.d("Returning", "conn");
        return conn;
    }
}
