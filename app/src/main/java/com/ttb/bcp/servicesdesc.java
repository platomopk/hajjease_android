package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class servicesdesc extends AppCompatActivity {

    TextView title_toolbar;

    static final String TAG="profile";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageView profile,call,sms;
    RatingBar ratingBar;
    TextView name,number ,stars,remarks;

    LinearLayout layout;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicesdesc);

        intent = getIntent();

        layout = (LinearLayout) findViewById(R.id.servicesPDoneLayout);

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);

        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);
        editor= sharedPreferences.edit();

        title_toolbar.setText("SERVICES");

        profile = (ImageView) findViewById(R.id.servicesPImage);
        call = (ImageView) findViewById(R.id.servicesPCall);
        sms = (ImageView) findViewById(R.id.servicesPSms);
        ratingBar = (RatingBar) findViewById(R.id.servicesPRating);
        name = (TextView) findViewById(R.id.servicesPName);
        number = (TextView) findViewById(R.id.servicesPNumber);
        stars = (TextView) findViewById(R.id.servicePStars);
        remarks = (TextView) findViewById(R.id.servicesPRemarks);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:0377778888"));
                    startActivity(callIntent);
                }catch (SecurityException e){
                    e.printStackTrace();
                }

            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsgDialog();
            }
        });


        new getService().execute("servicesId="+intent.getStringExtra("servicesId"));
    }

    AlertDialog alertDialog=null;
    void showMsgDialog(){
        AlertDialog.Builder adb =  new AlertDialog.Builder(servicesdesc.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_report_dialog,null);
        TextView txt = (TextView) dialogView.findViewById(R.id.generic);
        final EditText message = (EditText) dialogView.findViewById(R.id.reportMsg);
        Button reportBtn = (Button) dialogView.findViewById(R.id.reportMsgBtn);

        txt.setText("Enter your message in the given area down below. This message will be sent to the resource");
        reportBtn.setText("Send Message");

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().length()>0){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number.getText().toString().trim(), null, message.getText().toString().trim(), null, null);

                    alertDialog.dismiss();

                }else{
                    message.setError("You need to enter something here");
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

    JSONObject servicesObj;
    public class getService extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(servicesdesc.this);
            pd.setMessage("Getting Profile ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getsingleresource.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);
                        servicesObj = Jobj;
                    }

                }else {
                    return  false;
                }


            } catch (Exception e) {
                Log.e(TAG, "doInBackground: "+e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {

                if (result){

                    if (intent.getStringExtra("status").equals("done")){

                        layout.setVisibility(View.VISIBLE);
                    }

                    name.setText(servicesObj.getString("name"));
                    number.setText(servicesObj.getString("number"));
                    stars.setText(servicesObj.getString("starsgiven"));
                    remarks.setText(servicesObj.getString("remarks"));


                    ratingBar.setRating(Float.valueOf(servicesObj.getString("stars")));
                    ratingBar.setIsIndicator(true);
                    Picasso.get().load(new functions().baseIP+servicesObj.getString("picture")).placeholder(R.drawable.placeholder).fit().centerCrop().into(profile);


                }else {
                    new functions().finishDialog(servicesdesc.this,"Alert!","We could not get this data at this moment","ok");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
