package com.ttb.bcp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class easypaisa extends AppCompatActivity {
    TextView title_toolbar,welcomeMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easypaisa);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        title_toolbar.setText("EasyPaisa");
    }
}
