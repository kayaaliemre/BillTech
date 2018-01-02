package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Board extends AppCompatActivity {
    ViewPager viewPager;
    String query;
    ConnectionClass connectionClass;
    PreparedStatement statement;
    ResultSet resultSet;
    ArrayList<AdvertClass> advertList;
    ArrayList<AdvertClass> advertInBoard;
    ImageButton createAdvertButton;
    ImageButton buttonBack;
    int numberOfAdvert = 4;
    int numberOfBoard;
    TextView billboardname;
    int billboardId;
    int advertId;
    int restOfAdverts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        billboardname = (TextView) findViewById(R.id.billBoardName);
        createAdvertButton = (ImageButton) findViewById(R.id.createAdvertButton);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        buttonBack =(ImageButton) findViewById(R.id.btnBack);
        advertList = new ArrayList<AdvertClass>();
        advertInBoard = new ArrayList<AdvertClass>();
        connectionClass = new ConnectionClass();
        final String billboarName = getIntent().getStringExtra("billBoard_name");
        billboardname.setText(billboarName);

        try {
            Connection con = connectionClass.connectionDb();
            query = "SELECT * FROM AdvertInfo a inner join BillboardInfo b on b.billboard_id = a.billboard_id WHERE b.billboard_name='" + billboarName + "'";
            statement = con.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AdvertClass advert = new AdvertClass();
                advert.advertId = resultSet.getInt("advert_id");
                advert.advertName = resultSet.getString("advert_name");
                advert.advertTheme = resultSet.getString("advert_theme");
                advertList.add(advert);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Connection con = connectionClass.connectionDb();
            query = "SELECT billboard_id FROM BillboardInfo WHERE billboard_name= '" + billboarName + "'";
            statement = con.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                billboardId = resultSet.getInt("billboard_id");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String email = getUsername();
        try {
            Connection con = connectionClass.connectionDb();
            query = "Select user_number_adverts from UserInfo where user_email_adress = '" + email + "'";
            statement = con.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                restOfAdverts = resultSet.getInt("user_number_adverts");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        createAdvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restOfAdverts == 0){
                    Toast.makeText(Board.this, "Limit of adverts is full.You can't add advert.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(Board.this, CreateAdvert.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("billBoard_Id", String.valueOf(billboardId));
                    bundle.putString("billBoard_Name", String.valueOf(billboarName));
                    intent = intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Board.this, BoardMap.class));
                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<android.support.v4.app.Fragment> fragmentList = new ArrayList<android.support.v4.app.Fragment>();

        if (advertList.size() % numberOfAdvert > 0) {
            numberOfBoard = (advertList.size() / numberOfAdvert) + 1;
        } else {
            numberOfBoard = advertList.size() / numberOfAdvert;
        }

        for (int i = 0; i < numberOfBoard; i++) {
            advertInBoard.clear();
            if (i == numberOfBoard - 1 && advertList.size() % 4 > 0) {
                for (int c = 0; c < advertList.size() % 4; c++) {
                    advertInBoard.add(advertList.get(i * 4 + c));
                }
                for (int k = 0; k < (4 - (advertList.size() % 4)); k++) {
                    AdvertClass advert = new AdvertClass();
                    advert.advertId=0;
                    advertInBoard.add(advert);
                }
                BoardFragment board = new BoardFragment(advertInBoard);
                fragmentList.add(board);
            } else {
                advertInBoard.add(advertList.get(i * 4));
                advertInBoard.add(advertList.get(i * 4 + 1));
                advertInBoard.add(advertList.get(i * 4 + 2));
                advertInBoard.add(advertList.get(i * 4 + 3));
                BoardFragment board = new BoardFragment(advertInBoard);
                fragmentList.add(board);
            }
        }
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
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
