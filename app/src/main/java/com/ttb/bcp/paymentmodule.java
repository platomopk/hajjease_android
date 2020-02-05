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

public class paymentmodule extends AppCompatActivity {
    TextView title_toolbar,welcomeMsg;
    ImageView profile_toolbar,signout_toolbar;
    LinearLayout easypaisa,jazzcash,card;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentmodule);


        sharedPreferences =getSharedPreferences("local_db",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);

        easypaisa = (LinearLayout) findViewById(R.id.easypaisa);
        jazzcash = (LinearLayout) findViewById(R.id.jazzcash);
        card = (LinearLayout) findViewById(R.id.card);


        title_toolbar.setText("PAYMENT METHODS");

        easypaisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paymentmodule.this,easypaisa.class));
            }
        });

        jazzcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paymentmodule.this,jazzcash.class));
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(paymentmodule.this,card.class));
            }
        });

    }

}
