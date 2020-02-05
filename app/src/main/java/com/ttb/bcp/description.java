package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class description extends AppCompatActivity {

    TextView title_toolbar;
    TextView subject,date,content,reportBtn,ackedBtn,reportContent,contentAckedOn,reportedOn;
    LinearLayout ackedLayout,reportLayout,descMainLayout;
    Intent i;
    String notificationId="";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);

        i=getIntent();
        notificationId=i.getStringExtra("notificationId");
        Log.e("NotificationID -< ", "" + notificationId );

        subject = (TextView) findViewById(R.id.subjDesc);
        date = (TextView) findViewById(R.id.dateDesc);
        content = (TextView) findViewById(R.id.contentDesc);
        contentAckedOn = (TextView) findViewById(R.id.contentAckedOn);
        reportContent = (TextView) findViewById(R.id.reportContentDesc);
        reportedOn = (TextView) findViewById(R.id.reportedOn);

        reportBtn = (TextView) findViewById(R.id.reportBtn);
        ackedBtn = (TextView) findViewById(R.id.ackdBtn);

        ackedLayout = (LinearLayout) findViewById(R.id.ackdLayout);
        reportLayout = (LinearLayout) findViewById(R.id.reportLayout);
        descMainLayout = (LinearLayout) findViewById(R.id.descMainLayout);
        descMainLayout.setVisibility(View.GONE);

        title_toolbar.setText("NOTIFICATION DETAILS");


        new getContentTask().execute("notificationId="+notificationId+"&userId="+sharedPreferences.getString("userId","0"));

        ackedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAck();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendNack();
                showReportDialog();
            }
        });

    }

    String _subject="",_date="",_content="",_acked="",_reportmsg="",_reported="",_ackedOn="",_reportedOn="";
    public class getContentTask extends AsyncTask<String, Void, Boolean> {
        String TAG= "getcontenttask";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(description.this);
            pd.setMessage("Please Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getsinglenoti.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length() > 0) {

                    JSONObject obj = jArr.getJSONObject(0);

                    _subject = obj.getString("subject");
                    _date = obj.getString("camdate");
                    _content = obj.getString("msg");
                    _reportmsg = obj.getString("reportmsg");
                    _acked = obj.getString("ackd");
                    _reported = obj.getString("reported");
                    _ackedOn = obj.getString("ackdOn");
                    _reportedOn = obj.getString("reportedOn");


                    return true;

                } else {
                    return false;
                }

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

                descMainLayout.setVisibility(View.VISIBLE);

                subject.setText(_subject);
                date.setText(_date);
                content.setText(_content);

                if (_ackedOn.equals("0000-00-00 00:00:00")){
                    contentAckedOn.setVisibility(View.GONE);
                }else{
                    contentAckedOn.setVisibility(View.VISIBLE);
                }

                reportedOn.setText("Reported On: "+_reportedOn);
                contentAckedOn.setText("Acknowledged On: "+_ackedOn);
                reportContent.setText("'"+_reportmsg+"'");

                if (_reported.equals("0")){
                    reportLayout.setVisibility(View.GONE);
                }else{
                    reportLayout.setVisibility(View.VISIBLE);
                    ackedLayout.setVisibility(View.GONE);

                }

                if (_acked.equals("0")){
                    ackedLayout.setVisibility(View.VISIBLE);
                }else{
                    ackedLayout.setVisibility(View.GONE);
                }

                descMainLayout.setVisibility(View.VISIBLE);
            }else{
                descMainLayout.setVisibility(View.GONE);
                new functions().finishDialog(description.this,"Error","There might be a network error. Please try again later.","ok");
            }
            super.onPostExecute(result);
        }
    }

    private void sendAck() {
        final ProgressDialog pd = new ProgressDialog(description.this);
        pd.setMessage("Please Wait");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"ack.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().finishDialog(description.this,"Success","Thankyou for your response.","ok");
                        }else{
                            new functions().finishDialog(description.this,"Error","We could not submmit your request at this moment. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(description.this, "Internet is weak, try uploading the image again", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(description.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",sharedPreferences.getString("userId","0"));
                map.put("notificationId",notificationId.trim());
                return map;
            }
        };
        requestQueue.add(request);
        pd.show();
    }

    private void sendNack(final String messageLocal) {
        final ProgressDialog pd = new ProgressDialog(description.this);
        pd.setMessage("Please Wait");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"nack.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().finishDialog(description.this,"Success","Thankyou for your response.","ok");
                        }else{
                            new functions().finishDialog(description.this,"Error","We could not submmit your request at this moment. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(description.this, "Internet is weak, try again later", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(description.this,"Stack Trace",error.getMessage(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",sharedPreferences.getString("userId","0"));
                map.put("notificationId",notificationId.trim());
                map.put("message",messageLocal.trim());
                return map;
            }
        };
        requestQueue.add(request);
        pd.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    AlertDialog alertDialog=null;
    void showReportDialog(){
        AlertDialog.Builder adb =  new AlertDialog.Builder(description.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_report_dialog,null);
        final EditText message = (EditText) dialogView.findViewById(R.id.reportMsg);
        Button reportBtn = (Button) dialogView.findViewById(R.id.reportMsgBtn);

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().length()>0){
                    alertDialog.dismiss();
                    sendNack(message.getText().toString());

                }else{
                    message.setError("You need to enter something");
                }
            }
        });
        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        adb.setView(dialogView);
        adb.setCancelable(true);
        alertDialog = adb.create();
        alertDialog.show();
    }

}
