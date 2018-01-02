package com.example.kaya.billtech;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by cagdas on 14.12.2017.
 */

public class AdvertAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<AdvertClass> list;

    ConnectionClass connectionClass;

    public AdvertAdapter(Activity activity, List<AdvertClass> adverts) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = adverts;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View rowView;

        rowView = inflater.inflate(R.layout.my_advert_row, null);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView textId = (TextView) rowView.findViewById(R.id.textView8);
        TextView textName = (TextView) rowView.findViewById(R.id.textView9);
        Button updateButton = (Button) rowView.findViewById(R.id.btn_update);
        Button deleteButton = (Button) rowView.findViewById(R.id.btn_delete);


        final AdvertClass advert = list.get(i);
        if (advert.getAdvertImage() != null && !(advert.getAdvertImage().toString().isEmpty())) {
            byte[] bytarray = Base64.decode(advert.getAdvertImage(), Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);
            imageView.setImageBitmap(bmimage);
        }
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UpdateAdvert.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("advert_Id", String.valueOf(list.get(i).getAdvertId()));
                intent = intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        connectionClass = new ConnectionClass();
                        String deleteQuery;
                        String updateQuery;
                        deleteQuery = " DELETE FROM AdvertInfo where advert_id = '" + advert.getAdvertId() + "'";
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
                        updateQuery = "update UserInfo set user_number_adverts = user_number_adverts + 1  where user_id = '" + advert.getUserıd() + "'";
                        try {
                            connectionClass = new ConnectionClass();
                            Connection con = connectionClass.connectionDb();
                            preparedStatement = con.prepareStatement(updateQuery);
                            preparedStatement.executeUpdate();
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(view.getContext(), MyAdverts.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                        dialog.dismiss();
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
        textId.setText("Advert No: " + advert.getAdvertId());
        textName.setText(advert.getAdvertName());


        return rowView;
    }
}
