package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ProfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    ImageButton imageButton;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    TextView name;
    TextView surname;
    TextView adress;
    TextView phone;
    ProgressDialog pbRegister;
    ConnectionClass connectionClass;
    Button btnUpdate;
    Button btnDelete;
    String encoded;
    Switch switchPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectionClass = new ConnectionClass();
        final Connection con = connectionClass.connectionDb();
        String query;
        final String email = getUsername();

        imageView = (ImageView) findViewById(R.id.imageView);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        btnUpdate = (Button) findViewById(R.id.bt_update);
        btnDelete = (Button) findViewById(R.id.bt_delete);
        name = (TextView) findViewById(R.id.user_name);
        surname = (TextView) findViewById(R.id.user_surname);
        adress = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone_number);
        switchPremium = (Switch) findViewById(R.id.switch1);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    new Updating().execute();

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfilActivity.this);
                builder.setMessage("Are you sure??");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                        new Deleting().execute();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        query = "select * from UserInfo where user_email_adress = '" + email + "'";
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        UserClass user = new UserClass();
        try {
            preparedStatement = con.prepareStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                user.userName = rs.getString("user_name");
                user.userSurname = rs.getString("user_surname");
                user.userEmail = rs.getString("user_email_adress");
                user.userMac = rs.getString("user_mac_adress");
                user.userPhone = rs.getString("user_phone");
                user.userAdress = rs.getString("user_adress");
                user.userImage = rs.getBytes("user_image");
                user.userPremium = rs.getInt("user_premium");
                user.userNumberAdverts = rs.getInt("user_number_adverts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user.userImage != null && user.userImage.toString() != "" && user.getUserImage() != null && !(user.getUserImage().toString().isEmpty())) {
            byte[] bytarray = Base64.decode(user.getUserImage(), Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(bmimage);
        }
        name.setText(user.getUserName());
        surname.setText(user.getUserSurname());
        adress.setText(user.getUserAdress());
        phone.setText(user.getUserPhone());
        if (user.getUserPremium().toString() == "0") {
            switchPremium.setChecked(false);
        } else if (user.getUserPremium().toString() == "1") {
            switchPremium.setChecked(true);
        }
        switchPremium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfilActivity.this);
                    builder.setMessage("Are you sure??");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switchPremium.setChecked(true);
                            Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switchPremium.setChecked(false);
                            dialogInterface.cancel();
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {

                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private void SelectImage() {
        final CharSequence[] items = {"CAMERA", "GALLERY", "CANCEL"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilActivity.this);
        builder.setTitle("Add image");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission();
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("CAMERA")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[i].equals("GALLERY")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "select file"), SELECT_FILE);

                } else if (items[i].equals("CANCEL")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
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

    public boolean requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final byte[][] byteArray = new byte[1][1];
        if (resultCode == Activity.RESULT_OK) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Uri selectedImage = data.getData();
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(10)
                        .oval(false)
                        .build();

                Picasso.with(ProfilActivity.this)
                        .load(selectedImage)
                        .fit().centerInside()
                        .transform(transformation)
                        .rotate(0)                    //if you want to rotate by 90 degrees
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Drawable drawable = imageView.getDrawable();
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                                byteArray[0] = byteArrayOutputStream.toByteArray();
                                encoded = Base64.encodeToString(byteArray[0], Base64.DEFAULT);
                            }

                            @Override
                            public void onError() {
                                // ...
                            }
                        });
                imageView.setBackgroundResource(0);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(10)
                        .oval(false)
                        .build();
                Picasso.with(ProfilActivity.this)
                        .load(selectedImageUri)
                        .fit().centerInside()
                        .transform(transformation)
                        .rotate(0)                    //if you want to rotate by 90 degrees
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Drawable drawable = imageView.getDrawable();
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                                byteArray[0] = byteArrayOutputStream.toByteArray();
                                encoded = Base64.encodeToString(byteArray[0], Base64.DEFAULT);
                            }

                            @Override
                            public void onError() {
                                // ...
                            }
                        });
            }
        }
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
            Intent p = new Intent(ProfilActivity.this, ProfilActivity.class);
            startActivity(p);
        } else if (id == R.id.nav_billboard) {
            Intent b = new Intent(ProfilActivity.this, MyAdverts.class);
            startActivity(b);
        } else if (id == R.id.nav_settings) {
            Intent s = new Intent(ProfilActivity.this, SettingsActivity.class);
            startActivity(s);
        } else if (id == R.id.nav_main) {
            Intent s = new Intent(ProfilActivity.this, BoardMap.class);
            startActivity(s);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class Updating extends AsyncTask<Object, Object, Object> {
        Boolean isSuccess = false;
        String message;

        @Override
        protected Object doInBackground(Object... params) {
            String email;
            String updateQuery;
            email = getUsername();
            if (encoded.toString() != null && encoded.toString() != "" && !(encoded.toString().isEmpty())) {
                updateQuery = "update UserInfo SET user_adress = '" + adress.getText() + "',user_phone = '" + phone.getText() + "' ,user_image = '" + encoded.toString() + "' WHERE user_email_adress = '" + email + "'";
            } else {
                updateQuery = "update UserInfo SET user_adress = '" + adress.getText() + "',user_phone = '" + phone.getText() + "' WHERE user_email_adress = '" + email + "'";

            }
            PreparedStatement preparedStatement = null;
            try {
                connectionClass = new ConnectionClass();
                Connection con = connectionClass.connectionDb();
                preparedStatement = con.prepareStatement(updateQuery);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            pbRegister = new ProgressDialog(ProfilActivity.this);
            pbRegister.setMessage("Updating...");
            pbRegister.setIndeterminate(true);
            pbRegister.setCancelable(false);
            pbRegister.show();
        }

        @Override
        protected void onPostExecute(Object obj) {
            pbRegister.dismiss();
            Toast.makeText(ProfilActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
        }
    }

    public class Deleting extends AsyncTask<Object, Object, Object> {
        Boolean isSuccess = false;
        String message;

        @Override
        protected Object doInBackground(Object... params) {
            String email;
            email = getUsername();
            String deleteQuery;
            deleteQuery = " DELETE FROM UserInfo where user_email_adress = '" + email + "'";
            PreparedStatement preparedStatement = null;
            try {
                connectionClass = new ConnectionClass();
                Connection con = connectionClass.connectionDb();
                preparedStatement = con.prepareStatement(deleteQuery);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            pbRegister = new ProgressDialog(ProfilActivity.this);
            pbRegister.setMessage("Deleting...");
            pbRegister.setIndeterminate(true);
            pbRegister.setCancelable(false);
            pbRegister.show();
        }
    }

}