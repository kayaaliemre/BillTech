package com.example.kaya.billtech;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

public class UpdateAdvert extends AppCompatActivity {
    Button addImage;
    Button btnUpdate;
    Button btnCancel;
    TextView advertName;
    TextView advertText;
    ProgressDialog pbRegister;
    ConnectionClass connectionClass;
    ImageView imageView;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    String encoded;
    int advertId;
    int billboardId;
    private String[] arraySpinner;
    Spinner spinner;
    String billboardIdName;
    String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_advert);
        connectionClass = new ConnectionClass();
        final Connection con = connectionClass.connectionDb();
        String query;
        final String email = getUsername();

        Bundle bundle = getIntent().getExtras();
        String advertIdString = bundle.getString("advert_Id");
        billboardIdName = bundle.getString("billBoard_Name");
        advertId = Integer.parseInt(advertIdString);
        btnUpdate = (Button) findViewById(R.id.button_update);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        advertName = (TextView) findViewById(R.id.advert_name);
        advertText = (TextView) findViewById(R.id.advert_text);
        imageView = (ImageView) findViewById(R.id.imageviewAdvert);
        addImage = (Button) findViewById(R.id.imageButton);
        spinner = (Spinner) findViewById(R.id.spinner);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new Updating().execute();

            }
        }

        );
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateAdvert.this);
                builder.setMessage("Are you sure for canceling without savings?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "You accepted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateAdvert.this, MyAdverts.class);
                        startActivity(intent);
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
                advert.beginDate = rs.getDate("advert_begin_date");
                advert.endDate = rs.getDate("advert_end_date");
                advert.advertImage = rs.getBytes("advert_image");
                advert.billboardId = rs.getInt("billboard_id");
                advert.userıd = rs.getInt("user_id");
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
            imageView.setImageBitmap(bmimage);
        }
        advertName.setText(advert.getAdvertName());
        advertText.setText(advert.getAdvertText());

        this.arraySpinner = new String[]{
                "White(Default)", "Blue", "Yellow", "Green", "Pink"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);
    }

    public class Updating extends AsyncTask<Object, Object, Object> {
        Boolean isSuccess = false;
        String message;

        @Override
        protected Object doInBackground(Object... params) {
            connectionClass = new ConnectionClass();
            Connection con = connectionClass.connectionDb();
            String email;
            String updateQuery;
            Date currentTime = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateString = formatter.format(currentTime);

            email = getUsername();
            String query;
            query = "select user_id from UserInfo where user_email_adress ='" + email + "'";
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            Integer userId = 0;

            try {
                preparedStatement = con.prepareStatement(query);
                rs = preparedStatement.executeQuery();
                userId = rs.getInt("user_id");
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
            }

            if (encoded == null) {
                updateQuery = "update AdvertInfo SET advert_name = '" + advertName.getText() + "', advert_text = '" + advertText.getText()
                        + "' ,advert_begin_date = '" + formattedDateString + "', advert_theme = '" + theme +
                        "' WHERE advert_id = " + advertId + "";
            } else {
                updateQuery = "update AdvertInfo SET advert_name = '" + advertName.getText() + "', advert_text = '" + advertText.getText()
                        + "' ,advert_begin_date = '" + formattedDateString + "',advert_image = '" + encoded.toString() + "' "
                        + ", advert_theme = '" + theme +
                        "' WHERE advert_id = " + advertId + "";
            }

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
            pbRegister = new ProgressDialog(UpdateAdvert.this);
            pbRegister.setMessage("Updating...");
            pbRegister.setIndeterminate(true);
            pbRegister.setCancelable(false);
            pbRegister.show();
        }

        @Override
        protected void onPostExecute(Object obj) {
            pbRegister.dismiss();
            Toast.makeText(UpdateAdvert.this, "Update successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateAdvert.this, MyAdverts.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAdvert.this);
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
                Uri selectedImage = data.getData();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(10)
                        .oval(false)
                        .build();

                Picasso.with(UpdateAdvert.this)
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
                Picasso.with(UpdateAdvert.this)
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

    public byte[] convertImageToByte(Uri uri) {
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(UpdateAdvert.this, BoardMap.class));
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
