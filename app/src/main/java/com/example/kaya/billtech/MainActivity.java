package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int TIME_OUT = 3000;
    ConnectionClass connectionClass;
    Boolean isSuccess = false;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionClass = new ConnectionClass();
        workAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String email = getUsername();
                String mac = getMacAddr();
                try {
                    Connection con = connectionClass.connectionDb();
                    if (con == null) {
                        message = "Error in connection with SQL server";
                    } else {
                        String query;
                        query = "select user_email_adress, user_mac_adress from UserInfo where user_email_adress ='" + email + "' and user_mac_adress ='"+ mac + "'";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs == null || rs.toString() == "" || !rs.next()) {
                            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(i);
                            finish();
                            con.close();
                        } else {
                            Intent i = new Intent(MainActivity.this, BoardMap.class);
                            startActivity(i);
                            finish();
                            con.close();
                        }
                        message = "Added Successfully";
                        isSuccess = true;
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    ex.printStackTrace();
                    message = "Exceptions";
                }
            }
        }, TIME_OUT);
          //new UpdateNumberOfAdverts().execute();
    }

    private void workAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        ImageView logo = (ImageView) findViewById(R.id.ilogo);
        animation.reset();
        logo.clearAnimation();
        logo.startAnimation(animation);
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
        }
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;
        } else
            return null;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
    /*
    public class UpdateNumberOfAdverts extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String ... params) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String email;
            String updateQuery;
            String deleteQuery;

            email = getUsername();
            updateQuery = " UPDATE UserInfo\n" +
                    "SET user_number_adverts = user_number_adverts + \n" +
                    "(Select Count(*) from UserInfo as u\n" +
                    "INNER JOIN AdvertInfo AS a ON u.user_id = a.user_id\n" +
                    "WHERE a.advert_begin_date <  DATEADD(day, -18, GETDATE())) where user_email_adress = '" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = con.prepareStatement(updateQuery);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connectionClass = new ConnectionClass();
            con = connectionClass.connectionDb();
            deleteQuery = "Delete a from AdvertInfo a inner join UserInfo u on u.user_id = a.user_id\n" +
                    " where u.user_email_adress = '"+ email +"' and a.advert_begin_date <  DATEADD(day, -18, GETDATE())";
            try {
                preparedStatement = con.prepareStatement(deleteQuery);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    */
}
