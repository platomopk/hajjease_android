package com.ttb.bcp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class carpool extends AppCompatActivity {

    EditText to,from;
    Button pickup;
    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool);

        title = (TextView) findViewById(R.id.toolbar_heading);
        to = (EditText) findViewById(R.id.to);
        from = (EditText) findViewById(R.id.from);
        pickup = (Button) findViewById(R.id.pickup);

        title.setText("CARPOOL");

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (to.getText().toString().trim().length()>2 && from.getText().toString().trim().length()>2){
                    String userId = getApplicationContext().getSharedPreferences("local_db",MODE_PRIVATE).getString("userId","0");
                    String toTxt = null;
                    try {
                        toTxt = URLEncoder.encode(to.getText().toString().trim(),"UTF-8");
                        toTxt = toTxt.replaceAll("\\+","%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String fromTxt = null;
                    try {
                        fromTxt = URLEncoder.encode(from.getText().toString().trim(),"UTF-8");
                        fromTxt = fromTxt.replaceAll("\\+","%20");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new broadcastTask().execute("userId="+userId+"&dest_to="+toTxt+"&dest_from="+fromTxt);
                    //Toast.makeText(carpool.this, toTxt+ "   " + fromTxt, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(carpool.this, "Please enter correct values", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    public class broadcastTask extends AsyncTask<String, Void, Boolean> {
        String TAG= "logintask";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(carpool.this);
            pd.setMessage("Please wait ...");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"sendbroadcast.php?"+strings[0]);



                return true;

                //JSONArray jArr = new JSONArray(jsonString);

//                if (jArr.length() > 0) {
//
//                    JSONObject obj = jArr.getJSONObject(0);
//
//                    return true;
//
//                } else {
//                    return false;
//                }

            }catch (Exception e){
                Log.e(TAG, "doInBackground: "+e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result){
                new functions().finishDialog(carpool.this,"Success","Broadcasted your request. Stay tuned","ok");
            }
            super.onPostExecute(result);
        }
    }
}
