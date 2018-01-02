package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyAdverts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView advertList;
    List<AdvertClass> adverts = new ArrayList<AdvertClass>();
    AdvertAdapter adapter;
    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboardsctivity);
        String email = getUsername();
        advertList = (ListView) findViewById(R.id.advert_list);
        String query;
        connectionClass = new ConnectionClass();
        final Connection con = connectionClass.connectionDb();
        query = "select * from AdvertInfo a \n" +
                "inner join UserInfo u on u.user_id = a.user_id  \n" +
                " where u.user_email_adress = '" + email + "'";
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                AdvertClass advert = new AdvertClass();
                advert.advertId = rs.getInt("advert_id");
                advert.advertName = rs.getString("advert_name");
                advert.advertText = rs.getString("advert_text");
                advert.advertImage = rs.getBytes("advert_image");
                advert.billboardId = rs.getInt("billboard_id");
                advert.userıd = rs.getInt("user_id");
                adverts.add(advert);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapter = new AdvertAdapter(this, adverts);
        advertList.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(MyAdverts.this, BoardMap.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent p = new Intent(MyAdverts.this, ProfilActivity.class);
            startActivity(p);
        } else if (id == R.id.nav_billboard) {
            Intent b = new Intent(MyAdverts.this, MyAdverts.class);
            startActivity(b);

        } else if (id == R.id.nav_settings) {
            Intent s = new Intent(MyAdverts.this, SettingsActivity.class);
            startActivity(s);
        } else if (id == R.id.nav_main) {
            Intent s = new Intent(MyAdverts.this, BoardMap.class);
            startActivity(s);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
