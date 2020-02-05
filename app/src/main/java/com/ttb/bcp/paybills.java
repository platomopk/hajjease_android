package com.ttb.bcp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class paybills extends AppCompatActivity {
    TextView title_toolbar,welcomeMsg;
    ImageView profile_toolbar,signout_toolbar;
    LinearLayout electricity,telecom,maintainence, others;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paybills);


        sharedPreferences =getSharedPreferences("local_db",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);

        electricity = (LinearLayout) findViewById(R.id.electricity);
        telecom = (LinearLayout) findViewById(R.id.telecom);
        maintainence = (LinearLayout) findViewById(R.id.maintainence);
        others = (LinearLayout) findViewById(R.id.others);


        title_toolbar.setText("PAY BILLS");

        electricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paybills.this,paymentmodule.class));
            }
        });

        telecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paybills.this,paymentmodule.class));
            }
        });

        maintainence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paybills.this,paymentmodule.class));
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paybills.this,paymentmodule.class));
            }
        });



    }

}
