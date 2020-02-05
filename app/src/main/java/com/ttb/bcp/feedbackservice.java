package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

public class feedbackservice extends AppCompatActivity {
    Intent intent;
    RatingBar ratingBar;
    EditText remarks;
    Button submit;
    float rating;
    TextView toolbar_heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackservice);
        intent= getIntent();

        toolbar_heading = (TextView) findViewById(R.id.toolbar_heading);
        toolbar_heading.setText("FEEDBACK");

        ratingBar=(RatingBar) findViewById(R.id.fRating);
        remarks = (EditText) findViewById(R.id.fRemarks);
        submit = (Button) findViewById(R.id.fSubmit);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating=v;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating==0 || remarks.getText().equals("")){
                    Toast.makeText(feedbackservice.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
                }else{
                    submitFeedback();
                }
            }
        });

    }

    private void submitFeedback() {
        final ProgressDialog pd = new ProgressDialog(feedbackservice.this);
        pd.setMessage("Please Wait ..");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"feedbackservice.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().exitDialog(feedbackservice.this,"Congrats","Thank you for your response","ok");
                        }else{
                            new functions().normalDialog(feedbackservice.this,"Error","There was a network issue. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(feedbackservice.this, "Internet is weak, try again later", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(feedbackservice.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("servicesId",intent.getStringExtra("servicesId"));
                map.put("rating",String.valueOf(rating));
                map.put("remarks",remarks.getText().toString().trim());

                //Log.e("FEEdbac", "getParams: "+map.toString() );
                return map;
            }
        };
        requestQueue.add(request);
        Log.e("fb", "registerRequest: "+request.toString() );
        pd.show();
    }
}
