package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mindorks.paracamera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class security extends AppCompatActivity {

    LinearLayout securityLayout;
    Button takepic;
    TextView name,address,time,title;
    ImageView logout,siren;
    Camera camera;
    CountDownTimer countdowntimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);



        securityLayout = (LinearLayout) findViewById(R.id.securityLayout);
        takepic = (Button) findViewById(R.id.security_btn);
        name = (TextView) findViewById(R.id.securityName);
        address = (TextView) findViewById(R.id.securityAddress);
        time = (TextView) findViewById(R.id.securityTime);
        title = (TextView) findViewById(R.id.toolbar_heading);
        siren = (ImageView) findViewById(R.id.securityImage);

        logout = (ImageView) findViewById(R.id.toolbar_signout);
        logout.setVisibility(View.VISIBLE);

        title.setText("Panic Monitoring");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePic();
            }
        });



        triggerStuff();

    }

    void triggerStuff(){
        countdownaction();
    }

    void countdownaction(){
        countdowntimer = new CountDownTimer(3000000,10000) {
            @Override
            public void onTick(long l) {
                //call and update stuff
                new getUpdatedPanic().execute();
                //Toast.makeText(security.this, "Countdowntimer fired", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                triggerStuff();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (countdowntimer!=null){
//            countdowntimer.cancel();
//        }
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                if (countdowntimer!=null){
            countdowntimer.cancel();
        }
        finish();
    }

    void takePic(){
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("BCP_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        try {
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void logout(){
        getSharedPreferences("local_db",MODE_PRIVATE).edit().putString("loggedin","").commit();
        startActivity(new Intent(security.this,login.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
                saveImage(bitmap);
                //siren.setImageBitmap(bitmap);
                //Toast.makeText(security.this, camera.getCameraBitmapPath(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "BCP-" + System.currentTimeMillis()+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(security.this, "Image Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public class getUpdatedPanic extends AsyncTask<String, Void, Boolean> {
        String TAG= "updatedpanic";
        ProgressDialog pd;
        JSONObject objectify;

        @Override
        protected void onPreExecute() {
//            pd = new ProgressDialog(security.this);
//            pd.setMessage("Verifying .. \nEmail & Password ..");
//            pd.setIndeterminate(false);
//            pd.setCancelable(false);
//            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"securitypanic.php");

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length() > 0) {

                    objectify = jArr.getJSONObject(0);

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
//            if (pd.isShowing()) {
//                pd.dismiss();
//            }
            if (result){
                try {
                    name.setText(objectify.getString("name"));
                    address.setText(objectify.getString("address"));
                    time.setText(objectify.getString("requesttime"));

                    name.setVisibility(View.VISIBLE);
                    address.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    takepic.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                siren.setImageResource(R.drawable.sirenred);

            }else{
                siren.setImageResource(R.drawable.sirengreen);
                name.setText("--");
                address.setText("--");
                time.setText("--");

                name.setVisibility(View.INVISIBLE);
                address.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                takepic.setVisibility(View.INVISIBLE);

                //Toast.makeText(security.this, "Not Found", Toast.LENGTH_SHORT).show();
            }

            //triggerStuff();

            super.onPostExecute(result);
        }
    }

}
