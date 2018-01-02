package com.example.kaya.billtech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BoardFragment extends android.support.v4.app.Fragment {
    TextView textView1, textView2, textView3, textView4;
    LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4;
    String text1;
    String text2;
    String text3;
    String text4;
    String theme1, theme2, theme3, theme4;
    int id1, id2, id3, id4;
    int resId1, resId2, resId3, resId4;
    Drawable d1, d2, d3, d4;

    public BoardFragment() {

    }

    @SuppressLint("ValidFragment")
    public BoardFragment(ArrayList<AdvertClass> list) {
        for (int i = 0; i < list.size(); i++) {
            switch (i) {
                case 0:
                    this.text1 = list.get(0).advertName;
                    this.id1 = list.get(0).advertId;
                    this.theme1 = list.get(0).advertTheme;

                case 1:
                    this.text2 = list.get(1).advertName;
                    this.id2 = list.get(1).advertId;
                    this.theme2 = list.get(1).advertTheme;

                case 2:
                    this.text3 = list.get(2).advertName;
                    this.id3 = list.get(2).advertId;
                    this.theme3 = list.get(2).advertTheme;

                case 3:
                    this.text4 = list.get(3).advertName;
                    this.id4 = list.get(3).advertId;
                    this.theme4 = list.get(3).advertTheme;

            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView1 = (TextView) getView().findViewById(R.id.textView1);
        textView2 = (TextView) getView().findViewById(R.id.textView2);
        textView3 = (TextView) getView().findViewById(R.id.textView3);
        textView4 = (TextView) getView().findViewById(R.id.textView4);
        linearLayout1 = (LinearLayout) getView().findViewById(R.id.linear1);
        linearLayout2 = (LinearLayout) getView().findViewById(R.id.linear2);
        linearLayout3 = (LinearLayout) getView().findViewById(R.id.linear3);
        linearLayout4 = (LinearLayout) getView().findViewById(R.id.linear4);
        textView1.setText(text1);
        textView2.setText(text2);
        textView3.setText(text3);
        textView4.setText(text4);

        if (theme1 != null) {
            int resId1 = getResources().getIdentifier(theme1, "drawable", BoardFragment.this.getContext().getPackageName());
            Drawable d1 = BoardFragment.this.getResources().getDrawable(resId1);
            linearLayout1.setBackground(d1);
        }
        if (theme2 != null) {
            int resId2 = getResources().getIdentifier(theme2, "drawable", BoardFragment.this.getContext().getPackageName());
            Drawable d2 = BoardFragment.this.getResources().getDrawable(resId2);
            linearLayout2.setBackground(d2);
        }
        if (theme3 != null) {
            int resId3 = getResources().getIdentifier(theme3, "drawable", BoardFragment.this.getContext().getPackageName());
            Drawable d3 = BoardFragment.this.getResources().getDrawable(resId3);
            linearLayout3.setBackground(d3);
        }
        if (theme4 != null) {
            int resId4 = getResources().getIdentifier(theme4, "drawable", BoardFragment.this.getContext().getPackageName());
            Drawable d4 = BoardFragment.this.getResources().getDrawable(resId4);
            linearLayout4.setBackground(d4);
        }


        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id1 != 0) {
                    Intent i = new Intent(BoardFragment.super.getActivity(), AdvertDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("advert_Id", String.valueOf(id1));
                    i = i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id2 != 0) {
                    Intent i = new Intent(BoardFragment.super.getActivity(), AdvertDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("advert_Id", String.valueOf(id2));
                    i = i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id3 != 0) {
                    Intent i = new Intent(BoardFragment.super.getActivity(), AdvertDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("advert_Id", String.valueOf(id3));
                    i = i.putExtras(bundle);
                    startActivity(i);
                }

            }
        });
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id4 != 0) {
                    Intent i = new Intent(BoardFragment.super.getActivity(), AdvertDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("advert_Id", String.valueOf(id4));
                    i = i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board_fragment, container, false);

    }

    public TextView getTextView1() {
        return textView1;
    }

    public void setTextView1(TextView textView1) {
        this.textView1 = textView1;
    }
}