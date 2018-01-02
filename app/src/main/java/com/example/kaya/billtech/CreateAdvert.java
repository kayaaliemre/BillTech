package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CreateAdvert extends AppCompatActivity {
    Button addImage;
    Button btnCreate;
    Button btnCancel;
    TextView advertName;
    TextView advertText;
    ProgressDialog pbRegister;
    ConnectionClass connectionClass;
    ImageView imageView;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    String encoded;
    Spinner spinner;
    String theme;
    private String[] arraySpinner;
    int billboardId;
    String billboardIdName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);
        btnCreate = (Button) findViewById(R.id.button_create);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        advertName = (TextView) findViewById(R.id.advert_name);
        advertText = (TextView) findViewById(R.id.advert_text);
        imageView = (ImageView) findViewById(R.id.imageviewAdvert);
        addImage = (Button) findViewById(R.id.imageButton);
        spinner = (Spinner) findViewById(R.id.spinnerTheme);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        Bundle bundle = getIntent().getExtras();
        String billboardIdString = bundle.getString("billBoard_Id");
        billboardIdName = bundle.getString("billBoard_Name");
        billboardId = Integer.parseInt(billboardIdString);

        this.arraySpinner = new String[]{
                "White(Default)", "Blue", "Yellow", "Green", "Pink"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Creating().execute();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAdvert.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure without savings?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CreateAdvert.this, Board.class);
                        intent.putExtra("billBoard_name", billboardIdName);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    public class Creating extends AsyncTask<Object, Object, Object> {
        Boolean isSuccess = false;
        String message;

        @Override
        protected Object doInBackground(Object... params) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String email;
            String createQuery;

            Date currentTime = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateString = formatter.format(currentTime);
            String updateQuery;
            email = getUsername();
            String query;
            query = "select user_id from UserInfo where user_email_adress ='" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            Integer userId = 0;
            try {
                preparedStatement = con.prepareStatement(query);
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Integer spinnerPosition = spinner.getSelectedItemPosition();
            switch (spinnerPosition) {
                case 0:
                    theme = "@drawable/white_paper";
                    break;
                case 1:
                    theme = "@drawable/blue_paper";
                    break;
                case 2:
                    theme = "@drawable/yellow_paper";
                    break;
                case 3:
                    theme = "@drawable/green_paper";
                    break;
                case 4:
                    theme = "@drawable/pink_paper";
                    break;
            }
            if (encoded != null) {
                createQuery = "insert into AdvertInfo(advert_name,advert_text,advert_begin_date,advert_image,billboard_id,user_id,advert_theme) " +
                        "values('" + advertName.getText() + "','" + advertText.getText() + "','"
                        + formattedDateString + "','" + encoded.toString() + "'," + billboardId + "," + userId + ",'" + theme.trim() + "')";
            } else {
                createQuery = "insert into AdvertInfo(advert_name,advert_text,advert_begin_date,billboard_id,user_id,advert_theme) " +
                        "values('" + advertName.getText() + "','" + advertText.getText() + "','"
                        + formattedDateString + "'," + billboardId + "," + userId + ",'" + theme + "')";
           }
            try {
                preparedStatement = con.prepareStatement(createQuery);
                preparedStatement.executeUpdate();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            updateQuery = "update UserInfo SET user_number_adverts = user_number_adverts - 1 WHERE user_email_adress = '" + email + "'";
            try {
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
            pbRegister = new ProgressDialog(CreateAdvert.this);
            pbRegister.setMessage("Creating...");
            pbRegister.setIndeterminate(true);
            pbRegister.setCancelable(false);
            pbRegister.show();
        }

        @Override
        protected void onPostExecute(Object obj) {
            pbRegister.dismiss();
            Toast.makeText(CreateAdvert.this, "Create successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateAdvert.this, Board.class);
            intent.putExtra("billBoard_name", billboardIdName);
            startActivity(intent);
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

    private void SelectImage() {
        final CharSequence[] items = {"CAMERA", "GALLERY", "CANCEL"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAdvert.this);
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

    private static final int PERMISSION_REQUEST_CAMERA = 0;

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

                Picasso.with(CreateAdvert.this)
                        .load(selectedImage)
                        .fit().centerInside()
                        .rotate(0)                    //if you want to rotate by 90 degrees
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Drawable drawable = imageView.getDrawable();
                                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                                byteArray[0] = byteArrayOutputStream.toByteArray();
                                encoded = Base64.encodeToString(byteArray[0], Base64.DEFAULT);
                            }

                            @Override
                            public void onError() {
                                // ...
                            }
                        });

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                Picasso.with(CreateAdvert.this)
                        .load(selectedImageUri)
                        .fit().centerInside()
                        .rotate(0)                    //if you want to rotate by 90 degrees
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Drawable drawable = imageView.getDrawable();
                                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(CreateAdvert.this, BoardMap.class));
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
