package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class AdvertDetails extends AppCompatActivity {
    ConnectionClass connectionClass;
    String userName;
    String userSurname;
    String userEmailAdress;
    TextView username;
    TextView useremail;
    TextView userphone;
    ImageView advertimage;
    TextView adverttitle;
    TextView advertdetails;
    Button buttonView;
    ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = (TextView) findViewById(R.id.nameText);
        useremail = (TextView) findViewById(R.id.emailText);
        adverttitle = (TextView) findViewById(R.id.advertTitle);
        advertdetails = (TextView) findViewById(R.id.advertDetails);
        advertimage = (ImageView) findViewById(R.id.advertImage);
        buttonView= (Button) findViewById(R.id.btnView);
        buttonBack=(ImageButton) findViewById(R.id.btnBack);
        connectionClass = new ConnectionClass();
        final Connection con = connectionClass.connectionDb();
        String query;
        final String email = getUsername();
        Bundle bundle = getIntent().getExtras();
        final String advertIdString = bundle.getString("advert_Id");
        Integer advertId = Integer.parseInt(advertIdString);

        query = "select * from AdvertInfo a inner join UserInfo u on a.user_id=u.user_id where advert_id = " + advertId + "";
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        AdvertClass advert = new AdvertClass();
        try {
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                advert.advertName = rs.getString("advert_name");
                advert.advertText = rs.getString("advert_text");
                advert.endDate = rs.getDate("advert_end_date");
                advert.advertImage = rs.getBytes("advert_image");
                advert.userıd = rs.getInt("user_id");
                userName = rs.getString("user_name");
                userSurname = rs.getString("user_surname");
                userEmailAdress = rs.getString("user_email_adress");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (advert.getAdvertImage() != null && !(advert.getAdvertImage().toString().isEmpty())) {
            byte[] bytarray = Base64.decode(advert.getAdvertImage(), Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);
            advertimage.setImageBitmap(bmimage);
        }
        username.setText(userName + " " + userSurname);
        useremail.setText(userEmailAdress);
        adverttitle.setText(advert.getAdvertName());
        advertdetails.setText(advert.getAdvertText());

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdvertDetails.this, ProfileView.class);
                Bundle bundle = new Bundle();
                bundle.putString("advert_Id", advertIdString);
                intent = intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdvertDetails.this, BoardMap.class));
        finish();
    }
}
