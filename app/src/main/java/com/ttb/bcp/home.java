package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class home extends AppCompatActivity {

    TextView title_toolbar,welcomeMsg;
    ImageView profile_toolbar,signout_toolbar;
    LinearLayout scheduleLayout,notificationLayout,servantLayout,serviceLayout, carLayout, complainLayout, trackingLayout, carpoolLayout, panicLayout,obsLayout, paybillsLayout, accomodationLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;
    String location="";
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            location = intent.getStringExtra("location");
            Log.e("locationReceived",location);
//                Toast.makeText(context, "Location Received from reciver, "+location, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        LocalBroadcastManager.getInstance(this).registerReceiver(locationReciever,new IntentFilter("new-location-published"));


//        startService(new Intent(this,locationservice.class));


        sharedPreferences =getSharedPreferences("local_db",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        welcomeMsg = (TextView) findViewById(R.id.welcomeMsg);

        welcomeMsg.setText("WELCOME "+sharedPreferences.getString("lastName","NULL")+"!");

        notificationLayout = (LinearLayout) findViewById(R.id.notificationLayout);
        servantLayout = (LinearLayout) findViewById(R.id.servantLayout);
        serviceLayout = (LinearLayout) findViewById(R.id.serviceLayout);
        carLayout = (LinearLayout) findViewById(R.id.carLayout);
        complainLayout = (LinearLayout) findViewById(R.id.complainLayout);
        trackingLayout = (LinearLayout) findViewById(R.id.trackingLayout);
        carpoolLayout = (LinearLayout) findViewById(R.id.carpoolLayout);
        panicLayout = (LinearLayout) findViewById(R.id.panicLayout);
        obsLayout = (LinearLayout) findViewById(R.id.obsLayout);
        paybillsLayout = (LinearLayout) findViewById(R.id.billsLayout);
        accomodationLayout = (LinearLayout) findViewById(R.id.accomodationLayout);
        scheduleLayout = (LinearLayout) findViewById(R.id.scheduleLayout);


        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
//        profile_toolbar.setVisibility(View.VISIBLE);
        signout_toolbar = (ImageView) findViewById(R.id.toolbar_signout);
        signout_toolbar.setVisibility(View.VISIBLE);

        title_toolbar.setText("DASHBOARD");

        profile_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,profile.class));
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,notifications.class));
            }
        });

        servantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,servants.class));
            }
        });

        serviceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,services.class));
            }
        });

        obsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showObsMenu();
            }
        });

        carLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,cars.class));
            }
        });

        complainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,complaints.class));
            }
        });

        trackingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,trackingdetails.class));
            }
        });

        accomodationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,accomodationdetails.class));
            }
        });

        scheduleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,scheduledetails.class));
            }
        });

        carpoolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,carpool.class));
            }
        });

        paybillsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,paybills.class));
            }
        });


        panicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPanicAlert();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new tidbitTask().execute();
            }
        });



        signout_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeToken();
            }
        });
    }



    @Override
    protected void onResume() {
        //YoYo.with(Techniques.Shake).repeat(5).duration(1000).playOn(fab);
        startService(new Intent(this,locationservice.class));
        super.onResume();
    }

    private void removeToken() {
        final ProgressDialog pd = new ProgressDialog(home.this);
        pd.setMessage("Please Wait");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"removetoken.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            editor.putString("userId","");
                            editor.putString("lastName","");
                            editor.putString("loggedin","");
                            editor.commit();

                            startActivity(new Intent(home.this,login.class));
                            finish();
                        }else{
                            new functions().normalDialog(home.this,"Attention!","We could not log you off at this moment due to some network discrepency, please try again after sometime.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(home.this, "Internet is weak, try again later", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(home.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",sharedPreferences.getString("userId","0"));
                return map;
            }
        };
        requestQueue.add(request);
        pd.show();
    }


    AlertDialog ad=null;
    AlertDialog.Builder adb;
    int counter=0;
    void showPanicAlert(){
        ad=null;
        final String message = "If you have accidentally clicked on this button, cancel now.";
        adb= new AlertDialog.Builder(home.this);
        counter = 5;

        LayoutInflater inflator = getLayoutInflater();
        View v = inflator.inflate(R.layout.countdown_timer_layout,null);
        final TextView tv = (TextView) v.findViewById(R.id.seconds);
        adb.setView(v);

        adb.setTitle("Emergency Button");
        adb.setCancelable(false);
        //final TextView tv = (TextView)ad.findViewById(android.R.id.message);

        final CountDownTimer cdt = new CountDownTimer(5000,940) {
            @Override
            public void onTick(long l) {
                tv.setText(counter+" seconds");
                counter--;
            }

            @Override
            public void onFinish() {
                ad.dismiss();
                new panicTask().execute("userId="+getApplicationContext().getSharedPreferences("local_db",MODE_PRIVATE).getString("userId","0")+"&location="+location);
            }

        };

        cdt.start();

        adb.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ad.cancel();
                cdt.cancel();
                ad.dismiss();
            }
        });

        ad = adb.create();
        ad.show();
    }


    void showcommingDialog(){
        //final String message = "If you have accidentally clicked on this button, cancel now.";
        ad=null;
        adb= new AlertDialog.Builder(home.this);
        counter = 300;

        LayoutInflater inflator = getLayoutInflater();
        View v = inflator.inflate(R.layout.countdown_timer_layout,null);
        final TextView tv = (TextView) v.findViewById(R.id.seconds);
        final TextView msg = (TextView) v.findViewById(R.id.mesg);
        adb.setView(v);

        adb.setTitle("Response Team ETA!");
        msg.setText("Help is on it's way, find a safe spot and stay there.");
        adb.setCancelable(false);
        //final TextView tv = (TextView)ad.findViewById(android.R.id.message);

        final CountDownTimer cdt = new CountDownTimer(counter*1000,940) {
            @Override
            public void onTick(long l) {
                tv.setText(counter+" seconds");
                counter--;
            }

            @Override
            public void onFinish() {
                ad.dismiss();
                //Toast.makeText(home.this, "finminshed", Toast.LENGTH_SHORT).show();
            }

        };

        cdt.start();

        adb.setPositiveButton("hide", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ad.cancel();
                cdt.cancel();
                ad.dismiss();
            }
        });

        ad = adb.create();
        ad.show();
    }


    void showObsMenu(){
        ad=null;
        adb= new AlertDialog.Builder(home.this);

        LayoutInflater inflator = getLayoutInflater();
        View v = inflator.inflate(R.layout.custom_observations,null);
        final Button general = (Button) v.findViewById(R.id.dialog_general);
        final Button commercial = (Button) v.findViewById(R.id.dialog_commercial);

        adb.setView(v);

        adb.setTitle("Choose Observation Type");
        adb.setCancelable(false);
        //final TextView tv = (TextView)ad.findViewById(android.R.id.message);



        adb.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ad.dismiss();
            }
        });

        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                startActivity(new Intent(home.this,creategeneralobs.class));
            }
        });

        commercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                startActivity(new Intent(home.this,createcommercialobs.class));
            }
        });

        ad = adb.create();
        ad.show();
    }



    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, locationservice.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this,locationservice.class));
    }

    public class panicTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(home.this);
            pd.setMessage("Registering Panic Request ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"panic.php?"+strings[0]);

                return true;

            }catch (Exception e){
                Log.e("panic", "doInBackground: "+e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result){
                showcommingDialog();
            }else{
                new functions().normalDialog(home.this,"Alert!","Find a safe spot and contact '15' right now as our server is down.","ok");
            }
            super.onPostExecute(result);
        }
    }



    public class tidbitTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        JSONObject obj;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(home.this);
            pd.setMessage("Loading Something Interesting ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"tidbits.php");
                JSONArray arr = new JSONArray(jsonString);

                obj = arr.getJSONObject(0);


                return true;

            }catch (Exception e){
                Log.e("panic", "doInBackground: "+e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result){
                try {
                    new functions().normalDialog(home.this,"Information!",obj.getString("tidbit"),"ok");
                } catch (JSONException e) {
                    Toast.makeText(home.this, "sorry, cannot resolve request right now.", Toast.LENGTH_SHORT).show();
                }
            }else{
                new functions().normalDialog(home.this,"Sorry!","No interesting tidbits found.","ok");
            }
            super.onPostExecute(result);
        }
    }







}
