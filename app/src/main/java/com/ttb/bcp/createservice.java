package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class createservice extends AppCompatActivity {

    TextView title_toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView toolbar_ok;
    EditText title,message;
    Spinner category;
    ArrayAdapter<CharSequence> complaintAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createservice);

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        toolbar_ok = (ImageView) findViewById(R.id.toolbar_ok);
        toolbar_ok.setVisibility(View.VISIBLE);

        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);
        editor= sharedPreferences.edit();

        title = (EditText) findViewById(R.id.complaintTitle);
        message = (EditText) findViewById(R.id.complaintMsg);
        category = (Spinner) findViewById(R.id.complaintCategory);

        title_toolbar.setText("NEW SERVICE");

        complaintAdapter = ArrayAdapter.createFromResource(this, R.array.serviceArray, android.R.layout.simple_spinner_item);
        complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(complaintAdapter);

        toolbar_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().trim().equals("")||message.getText().toString().trim().equals("")){
                    Toast.makeText(createservice.this, "There are still some empty fields.", Toast.LENGTH_SHORT).show();
                }else{
                    registerRequest();
                }
            }
        });
    }


    private void registerRequest() {
        final ProgressDialog pd = new ProgressDialog(createservice.this);
        pd.setMessage("Uploading");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"createservice.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().exitDialog(createservice.this,"Success","You have successfully requested for a service. Our support team is going to contact you very soon.","ok");
                        }else{
                            new functions().normalDialog(createservice.this,"Error","We could not register your request at this moment. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(createservice.this, "Internet is weak, try again later", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(createservice.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",sharedPreferences.getString("userId","0"));
                map.put("category",category.getSelectedItem().toString());
                map.put("title",title.getText().toString().trim());
                map.put("message",message.getText().toString().trim());
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(80000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        pd.show();
    }


}
