package com.ttb.bcp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class updatecar extends AppCompatActivity {

    TextView title_toolbar;

    ImageView profile_toolbar,toolbar_rem,image;

    Button register;
    ImageButton selectImg,galleryImg;

    EditText make,model,reg,chasis,color;

    String encoded_string, image_name, carId="";
    Bitmap bitmap;
    File file;
    Uri file_uri;
    static final String TAG="car update";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecar);

        Intent i = getIntent();

        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);
        carId=i.getStringExtra("carId");
        //carId="1";

        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        toolbar_rem = (ImageView) findViewById(R.id.toolbar_remove);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);

        make = (EditText) findViewById(R.id.make);
        model = (EditText) findViewById(R.id.model);
        reg = (EditText) findViewById(R.id.reg);
        chasis = (EditText) findViewById(R.id.chasis);
        color = (EditText) findViewById(R.id.color);

        image = (ImageView) findViewById(R.id.image);

        register = (Button) findViewById(R.id.register);
        selectImg = (ImageButton) findViewById(R.id.selectImg);
        galleryImg = (ImageButton) findViewById(R.id.galleryImg);

        selectImg.requestFocus();

        profile_toolbar.setVisibility(View.GONE);
        toolbar_rem.setVisibility(View.VISIBLE);

        title_toolbar.setText("CAR");


        encoded_string="";image_name="";



        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getFileUri();
                i.putExtra(MediaStore.EXTRA_OUTPUT,file_uri);
                startActivityForResult(i,10);
            }
        });

        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 11);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (make.getText().toString().trim().equals("") || model.getText().toString().trim().equals("") || reg.getText().toString().trim().equals("") || chasis.getText().toString().trim().equals("") || color.getText().toString().trim().equals("")) {
                    Toast.makeText(updatecar.this, "There are some empty fields here", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        registerRequest2();
                    }catch (Exception e){
                        Log.e(TAG, "regClick: "+e.getMessage());
                    }
                }
            }
        });


        toolbar_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCar();
            }
        });


        new getCar().execute("carId="+carId);
    }

    private void deleteCar() {
        final ProgressDialog pd = new ProgressDialog(updatecar.this);
        pd.setMessage("Please Wait ...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"deletecar.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().exitDialog(updatecar.this,"Success","Successfully Deleted","ok");
                        }else{
                            new functions().normalDialog(updatecar.this,"Error",response,"ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(updatecar.this, "Image might be too large", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(updatecar.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("carId",carId);
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(80000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        pd.show();
    }

    private void registerRequest2() {
        //Toast.makeText(this, "in 2", Toast.LENGTH_SHORT).show();
        final ProgressDialog pd = new ProgressDialog(updatecar.this);
        pd.setMessage("Updating..");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("carId",carId);

            jsonObject.put("picture",encoded_string.trim());
            jsonObject.put("image_name",image_name.trim().replaceAll(" ","%20"));

            jsonObject.put("make",make.getText().toString().trim());
            jsonObject.put("model",model.getText().toString().trim());
            jsonObject.put("reg",reg.getText().toString().trim());
            jsonObject.put("chasis",chasis.getText().toString().trim());
            jsonObject.put("color",color.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, new functions().baseIP+"updatecar.php", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pd.dismiss();
                        Log.e("Message from server", jsonObject.toString());
                        Toast.makeText(getApplication(), "Update Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Message from server Err", volleyError.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(jsonObjectRequest);
//        till here
        pd.show();
    }

    private void registerRequest() {
        final ProgressDialog pd = new ProgressDialog(updatecar.this);
        pd.setMessage("Uploading");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"updatecar.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().exitDialog(updatecar.this,"Success","Successfully Updated","ok");
                        }else{
                            new functions().normalDialog(updatecar.this,"Error",response,"ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(updatecar.this, "Internet is weak, try uploading the image again", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(updatecar.this,"Stack Trace",error.getMessage(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("carId",carId);

                map.put("picture",encoded_string.trim());
                map.put("image_name",image_name.trim().replaceAll(" ","%20"));

                map.put("make",make.getText().toString().trim());
                map.put("model",model.getText().toString().trim());
                map.put("reg",reg.getText().toString().trim());
                map.put("chasis",chasis.getText().toString().trim());
                map.put("color",color.getText().toString().trim());

                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(80000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        pd.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==10 && resultCode==RESULT_OK){
            new encode_image().execute();
        }
        if (requestCode==11 && resultCode==RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            file= new File(picturePath);
            image_name=file.getName();
            file_uri = Uri.fromFile(file);

            new encode_image().execute();

            // Toast.makeText(register.this, file_uri.toString(), Toast.LENGTH_LONG).show();
            //Toast.makeText(register.this, image_name, Toast.LENGTH_LONG).show();
        }
    }

    private void getFileUri() {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        image_name = "bcp-"+ts+".jpg";

        try {
            file= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
            file_uri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Toast.makeText(register.this, file_uri.toString(), Toast.LENGTH_LONG).show();
    }

    private class encode_image extends AsyncTask<Void,Void,Void> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd= new ProgressDialog(updatecar.this);
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.setMessage("Saving Image ..\nPlease Wait ..");
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            BitmapFactory.Options options0 = new BitmapFactory.Options();
            options0.inSampleSize = 4;
            options0.inJustDecodeBounds = true;
            options0.inPreferredConfig = Bitmap.Config.ARGB_8888;



            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,35,stream);

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array,Base64.DEFAULT);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pd.isShowing()){
                pd.dismiss();
            }
            Picasso.get().load(file).fit().centerCrop().into(image);
        }
    }


    JSONObject profileObj;
    public class getCar extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(updatecar.this);
            pd.setMessage("Getting Profile ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getsinglecar.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);
                        profileObj = Jobj;
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

                    Picasso.get().load(new functions().baseIP+profileObj.getString("picture")).placeholder(R.drawable.placeholder).fit().centerCrop().into(image);

                    make.setText(profileObj.getString("make"));
                    model.setText(profileObj.getString("model"));
                    chasis.setText(profileObj.getString("chasis_number"));
                    reg.setText(profileObj.getString("registeration_number"));
                    color.setText(profileObj.getString("color"));


                }else {
                    new functions().finishDialog(updatecar.this,"Alert!","We could not get your vehicle's profile data at this moment","ok");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
