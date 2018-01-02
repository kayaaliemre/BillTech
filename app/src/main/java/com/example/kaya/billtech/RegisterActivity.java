package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {
    TextView name;
    TextView surname;
    Button button;
    TextView welcomeEmail;
    CheckBox checkBox;
    ConnectionClass connectionClass;
    ProgressDialog pbRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkContactPermission();
        }
        name = (TextView) findViewById(R.id.user_name);
        surname = (TextView) findViewById(R.id.user_surname);
        welcomeEmail = (TextView) findViewById(R.id.welcomeEmail);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        button = (Button) findViewById(R.id.bt_register);

        checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("I approve the Billtech privacy agreements");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "You have to accept privacy agreements to register", Toast.LENGTH_SHORT).show();
                }
            }

        });

        connectionClass = new ConnectionClass();
        button = (Button) findViewById(R.id.bt_register);
        String email = getUsername();
        String mac = getMacAddr();
        welcomeEmail.setText("Welcome " + email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Registration register = new Registration();
                    register.execute();

            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeEmail.setVisibility(View.INVISIBLE);
            }
        });
        surname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeEmail.setVisibility(View.INVISIBLE);
            }
        });

    }

    public class Registration extends AsyncTask<Object, Object, Object> {

        Boolean isSuccess = false;
        String message;
        String userName = name.getText().toString();
        String userSurname = surname.getText().toString();

        @Override
        protected Object doInBackground(Object... params) {
            String email = getUsername();
            String mac = getMacAddr();
            try {
                Connection con = connectionClass.connectionDb();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    String query;
                    query = "INSERT INTO UserInfo(user_name, user_surname, user_email_adress, user_mac_adress, user_premium, user_number_adverts,user_notification,user_vibration) values ('" + userName + "','" + userSurname + "','" + email + "','" + mac + "', 0, 5, 0, 0)";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    message = "Added Successfully";
                    isSuccess = true;
                    con.close();
                }
            } catch (Exception ex) {
                isSuccess = false;
                ex.printStackTrace();
                message = "Exceptions";
            }

            return null;
        }
        @Override
        protected void onPreExecute() {
            pbRegister = new ProgressDialog(RegisterActivity.this);
            pbRegister.setMessage("Loading...");
            pbRegister.setIndeterminate(true);
            pbRegister.setCancelable(false);
            pbRegister.show();
        }

        @Override
        protected void onPostExecute(Object obj) {
            pbRegister.dismiss();
            Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();

            if (isSuccess) {
                Intent i = new Intent(RegisterActivity.this, BoardMap.class);
                startActivity(i);
                finish();
            }
        }
    }

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

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

    public boolean checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_CONTACTS_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.GET_ACCOUNTS)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
