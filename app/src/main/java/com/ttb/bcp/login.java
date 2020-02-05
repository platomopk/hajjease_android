package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {

    TextView title_toolbar;
    Button register, login;
    ImageView profile_toolbar, recoverarrow, createarrow;
    LinearLayout createLayout, recoverLayout;
    static final String TAG="LoginAct";
    EditText email, password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedPreferences = getSharedPreferences("local_db", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("loggedin","").equals("true")){
            //user already logged in
            startActivity(new Intent(login.this,home.class));
            finish();
        }else if(sharedPreferences.getString("loggedin","").equals("security")){
            startActivity(new Intent(login.this,security.class));
            finish();
        }else if(sharedPreferences.getString("loggedin","").equals("complaints")){
            startActivity(new Intent(login.this,genericcomplaints.class));
            finish();
        }

        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        register = (Button) findViewById(R.id.login_registerBtn);
        login = (Button) findViewById(R.id.login_signin);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        recoverarrow = (ImageView) findViewById(R.id.recoverarrow);
        createarrow = (ImageView) findViewById(R.id.createarrow);
        recoverLayout = (LinearLayout) findViewById(R.id.recoverLayout);
        createLayout = (LinearLayout) findViewById(R.id.createLayout);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);



        profile_toolbar.setVisibility(View.GONE);

        title_toolbar.setText("BCP");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")){
                    Toast.makeText(login.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
                }else{
                    try {

                        if (email.getText().toString().trim().equals("security@bahria.com") && password.getText().toString().trim().equals("123456")){
                            editor.putString("loggedin","security");
                            editor.commit();

                            startActivity(new Intent(login.this,security.class));
                            finish();
                        }else if (email.getText().toString().trim().equals("complaints@bahria.com") && password.getText().toString().trim().equals("123456")){
                            editor.putString("loggedin","complaints");
                            editor.commit();

                            startActivity(new Intent(login.this,genericcomplaints.class));
                            finish();
                        }else{
                            String params = "email="+email.getText().toString().trim()+"&password="+password.getText().toString().trim()
                                    +"&token="+ FirebaseInstanceId.getInstance().getToken().toString();
                            new loginTask().execute(params);
                        }


                    }catch (Exception e){
                        Toast.makeText(login.this, "Illegal Values", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        recoverarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recoverLayout.getVisibility() == View.VISIBLE) {
                    recoverLayout.setVisibility(View.GONE);
                } else {
                    recoverLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        createarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createLayout.getVisibility() == View.VISIBLE) {
                    createLayout.setVisibility(View.GONE);
                } else {
                    createLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    String _userID="",_lastName="",_isactivated="";
    public class loginTask extends AsyncTask<String, Void, Boolean> {
        String TAG= "logintask";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(login.this);
            pd.setMessage("Verifying .. \nEmail & Password ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();

                String url = new functions().baseIP+"login.php?"+strings[0];
                url = url.replaceAll(" ","%20");
                Log.d(TAG, "doInBackground: "+url);

                String jsonString = handler.makeServiceCall(url);

                try {
                    JSONArray jArr = new JSONArray(jsonString);
                    if (jArr.length() > 0) {
                        JSONObject obj = jArr.getJSONObject(0);
                        _userID = obj.getString("userId");
                        _lastName = obj.getString("firstname") + " " + obj.getString("lastname");
                        _isactivated = obj.getString("isactivated");
                        return true;
                    } else {
                        return false;
                    }
                } catch (JSONException e) {
                    Toast.makeText(login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                if (_isactivated.equals("0")){
                    new functions().exitDialog(login.this,"Alert!","Your account is not activated yet, activate your account first","ok");
                }else{

                    editor.putString("userId",_userID);
                    editor.putString("lastName",_lastName);
                    editor.putString("loggedin","true");
                    editor.commit();

                    startActivity(new Intent(login.this,home.class));
                    finish();
                }
            }else{
                new functions().normalDialog(login.this,"Alert!","No information found against these credentials. Please check your email and password.","ok");
            }
            super.onPostExecute(result);
        }
    }
}
