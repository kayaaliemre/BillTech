package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog pbSettings;
    ConnectionClass connectionClass;
    String email;
    Switch notification;
    Switch vibration;
    ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        buttonBack = (ImageButton) findViewById(R.id.btnBack);
        setSupportActionBar(toolbar);
        email = getUsername();
        notification = (Switch) findViewById(R.id.switchnotification);
        vibration = (Switch) findViewById(R.id.switchvibration);

        connectionClass = new ConnectionClass();
        Connection con = connectionClass.connectionDb();
        String query;
        query = "select * from UserInfo where user_email_adress = '" + email + "'";
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        UserClass user = new UserClass();
        try {
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user.notification = rs.getInt("user_notification");
                user.vibration = rs.getInt("user_vibration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user.getNotification() == 1) {
            notification.setChecked(true);
            vibration.setChecked(true);
        } else {
            notification.setChecked(false);
            vibration.setChecked(false);
        }

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, BoardMap.class));
                finish();
            }
        });

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                    builder.setMessage("Are you sure??");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new checkedNotification().execute();
                            notification.setChecked(true);
                            vibration.setChecked(true);
                            Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new uncheckedNotification().execute();
                            notification.setChecked(false);
                            vibration.setChecked(false);
                            dialogInterface.cancel();
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                    builder.setMessage("Are you sure??");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new uncheckedNotification().execute();
                            notification.setChecked(false);
                            vibration.setChecked(false);
                            Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new checkedNotification().execute();
                            notification.setChecked(true);
                            vibration.setChecked(true);
                            dialogInterface.cancel();
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });
        /*
        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (notification.isChecked()) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                    builder.setMessage("Are you sure??");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new uncheckedVibration().execute();
                            vibration.setChecked(false);
                            Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new checkedVibration().execute();
                            vibration.setChecked(true);
                            dialogInterface.cancel();
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {

                }
            }
        });
        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent p = new Intent(SettingsActivity.this, ProfilActivity.class);
            startActivity(p);
        } else if (id == R.id.nav_billboard) {
            Intent b = new Intent(SettingsActivity.this, MyAdverts.class);
            startActivity(b);

        } else if (id == R.id.nav_settings) {
            Intent s = new Intent(SettingsActivity.this, SettingsActivity.class);
            startActivity(s);

        } else if (id == R.id.nav_main) {
            Intent s = new Intent(SettingsActivity.this, BoardMap.class);
            startActivity(s);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class checkedNotification extends AsyncTask<String, String, String> {
        Boolean isSuccess = false;
        String message;

        @Override
        protected void onPreExecute() {
            pbSettings = new ProgressDialog(SettingsActivity.this);
            pbSettings.setMessage("Updating...");
            pbSettings.setIndeterminate(true);
            pbSettings.setCancelable(false);
            pbSettings.show();
        }

        @Override
        protected void onPostExecute(String obj) {
            pbSettings.dismiss();
            Toast.makeText(SettingsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
            startService(new Intent(SettingsActivity.this, LocationService.class));
        }

        @Override
        protected String doInBackground(String... strings) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String query;
            query = "update UserInfo set user_notification = 1 where user_email_adress = '" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = con.prepareStatement(query);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class uncheckedNotification extends AsyncTask<String, String, String> {
        Boolean isSuccess = false;
        String message;

        protected void onPreExecute() {
            pbSettings = new ProgressDialog(SettingsActivity.this);
            pbSettings.setMessage("Updating...");
            pbSettings.setIndeterminate(true);
            pbSettings.setCancelable(false);
            pbSettings.show();
        }

        @Override
        protected void onPostExecute(String obj) {
            pbSettings.dismiss();
            Toast.makeText(SettingsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
            stopService(new Intent(SettingsActivity.this, LocationService.class));
        }

        @Override
        protected String doInBackground(String... strings) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String query;
            query = "update UserInfo set user_notification = 0 where user_email_adress = '" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = con.prepareStatement(query);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public class checkedVibration extends AsyncTask<String, String, String> {
        Boolean isSuccess = false;
        String message;

        protected void onPreExecute() {
            pbSettings = new ProgressDialog(SettingsActivity.this);
            pbSettings.setMessage("Updating...");
            pbSettings.setIndeterminate(true);
            pbSettings.setCancelable(false);
            pbSettings.show();
        }

        @Override
        protected void onPostExecute(String obj) {
            pbSettings.dismiss();
            Toast.makeText(SettingsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String query;
            query = "update UserInfo set user_vibration = 1 where user_email_adress = '" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = con.prepareStatement(query);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class uncheckedVibration extends AsyncTask<String, String, String> {
        Boolean isSuccess = false;
        String message;

        protected void onPreExecute() {
            pbSettings = new ProgressDialog(SettingsActivity.this);
            pbSettings.setMessage("Updating...");
            pbSettings.setIndeterminate(true);
            pbSettings.setCancelable(false);
            pbSettings.show();
        }

        @Override
        protected void onPostExecute(String obj) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String query;
            query = "update UserInfo set user_vibration = 0 where user_email_adress = '" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            try {
                preparedStatement = con.prepareStatement(query);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pbSettings.dismiss();
            Toast.makeText(SettingsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
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
}
