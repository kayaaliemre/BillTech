package com.example.kaya.billtech;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionClass {
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    @SuppressLint("NewApi")
    public Connection connectionDb() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnURL = null;
        try {
            Class.forName(classs);
            String connString = "jdbc:jtds:sqlserver://10.27.73.176:1433/BillTech;encrypt=false;user=billtech;password=billtech;instance=SQLEXPRESS;";
            connection = DriverManager.getConnection(connString, "billtech", "billtech");
        } catch (SQLException se) {
            Log.e("ERROR Sql", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERROR Class", e.getMessage());
        } catch (Exception e) {
            Log.e("ERROR Exception", e.getMessage());
        }
        return connection;
    }
}
