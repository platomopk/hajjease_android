package com.ttb.bcp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class complaintdescgeneric extends AppCompatActivity {

    TextView title_toolbar,date,time,status,ticket,cat,title,message,name,add;
    ImageView delete;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaintdescgeneric);

        i = getIntent();

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        title_toolbar.setText("DESCRIPTION");

        name = (TextView) findViewById(R.id.compName);
        add = (TextView) findViewById(R.id.compAddress);
        date = (TextView) findViewById(R.id.compDate);
        time = (TextView) findViewById(R.id.compTime);
        status = (TextView) findViewById(R.id.compStatus);
        ticket = (TextView) findViewById(R.id.compTicket);
        cat = (TextView) findViewById(R.id.compCat);
        title = (TextView) findViewById(R.id.compTitle);
        message = (TextView) findViewById(R.id.compContent);


        name.setText(i.getStringExtra("name"));
        add.setText(i.getStringExtra("address"));
        date.setText(i.getStringExtra("date"));
        time.setText(i.getStringExtra("time"));
        status.setText(i.getStringExtra("status"));
        ticket.setText(i.getStringExtra("ticket"));
        cat.setText(i.getStringExtra("category"));
        title.setText(i.getStringExtra("title"));
        message.setText(i.getStringExtra("message"));



    }

}
