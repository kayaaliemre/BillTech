package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ProfileView extends AppCompatActivity {
    ConnectionClass connectionClass;
    String email;
    TextView name;
    TextView surname;
    TextView emailadress;
    TextView phone;
    String encoded;
    Switch switchPremium;
    ImageView imageview;
    ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        String advertIdString = bundle.getString("advert_Id");
        Integer advertId = Integer.parseInt(advertIdString);
        buttonBack = (ImageButton)findViewById(R.id.btnBack);
        connectionClass = new ConnectionClass();
        Connection con = connectionClass.connectionDb();

        name = (TextView) findViewById(R.id.user_name);
        surname = (TextView) findViewById(R.id.user_surname);
        emailadress = (TextView) findViewById(R.id.user_address);
        phone = (TextView) findViewById(R.id.user_phone);
        switchPremium = (Switch) findViewById(R.id.switch1);
        imageview = (ImageView) findViewById(R.id.imageView);

        email = getUsername();
        String query;
        query = "select * from UserInfo u inner join AdvertInfo a on u.user_id=a.user_id where a.advert_id ='" + advertId + "'";
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Integer userId = 0;
        UserClass user = new UserClass();
        try {
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user.userName = rs.getString("user_name");
                user.userSurname = rs.getString("user_surname");
                user.userEmail = rs.getString("user_email_adress");
                user.userPhone = rs.getString("user_phone");
                user.userImage = rs.getBytes("user_image");
                user.userPremium = rs.getInt("user_premium");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        name.setText(user.getUserName());
        surname.setText(user.getUserSurname());
        emailadress.setText(user.getUserEmail());
        phone.setText(user.getUserPhone());
        if (user.getUserPremium().toString() == "0") {
            switchPremium.setChecked(false);
        } else if (user.getUserPremium().toString() == "1") {
            switchPremium.setChecked(true);
        }
        if (user.userImage != null && user.userImage.toString() != "" && user.getUserImage() != null && !(user.getUserImage().toString().isEmpty())) {
            byte[] bytarray = Base64.decode(user.getUserImage(), Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);
            imageview.setBackgroundResource(0);
            imageview.setImageBitmap(bmimage);
        }

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
}
