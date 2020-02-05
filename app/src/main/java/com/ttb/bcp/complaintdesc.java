package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class complaintdesc extends AppCompatActivity {

    TextView title_toolbar,date,time,status,ticket,cat,title,message;
    ImageView delete;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaintdesc);

        i = getIntent();

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        title_toolbar.setText("FEEDBACK DETAILS");

        delete = (ImageView) findViewById(R.id.toolbar_remove);
        delete.setVisibility(View.VISIBLE);

        date = (TextView) findViewById(R.id.compDate);
        time = (TextView) findViewById(R.id.compTime);
        status = (TextView) findViewById(R.id.compStatus);
        ticket = (TextView) findViewById(R.id.compTicket);
        cat = (TextView) findViewById(R.id.compCat);
        title = (TextView) findViewById(R.id.compTitle);
        message = (TextView) findViewById(R.id.compContent);

        date.setText(i.getStringExtra("date"));
        time.setText(i.getStringExtra("time"));
        status.setText(i.getStringExtra("status"));
        ticket.setText(i.getStringExtra("ticket"));
        cat.setText(i.getStringExtra("category"));
        title.setText(i.getStringExtra("title"));
        message.setText(i.getStringExtra("message"));


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteComplaint();
            }
        });

    }

    private void deleteComplaint() {
        final ProgressDialog pd = new ProgressDialog(complaintdesc.this);
        pd.setMessage("Please Wait ...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"deletecomplaint.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().exitDialog(complaintdesc.this,"Success","Successfully Deleted","ok");
                        }else{
                            new functions().normalDialog(complaintdesc.this,"Error",response,"ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(complaintdesc.this, "Internet is weak, try again later", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(complaintdesc.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("complaintId",i.getStringExtra("ticket"));
                return map;
            }
        };
        requestQueue.add(request);
        pd.show();
    }
}
