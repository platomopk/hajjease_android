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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class creategeneralobs extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar,image;
    ImageButton selectImg,galleryImg;
    String encoded_string, image_name;
    Bitmap bitmap;
    File file;
    Uri file_uri;
    static final String TAG="general";

    Spinner category;
    ArrayAdapter<CharSequence> complaintAdapter;

    EditText name,message;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategeneralobs);

        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);

        profile_toolbar = (ImageView) findViewById(R.id.toolbar_ok);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        image = (ImageView) findViewById(R.id.image);

        name = (EditText) findViewById(R.id.complaintTitle);
        message = (EditText) findViewById(R.id.complaintMsg);

        selectImg = (ImageButton) findViewById(R.id.selectImg);
        galleryImg = (ImageButton) findViewById(R.id.galleryImg);

        selectImg.requestFocus();

        profile_toolbar.setVisibility(View.VISIBLE);
        title_toolbar.setText("GENERAL (OBS)");
        category = (Spinner) findViewById(R.id.complaintCategory);

        complaintAdapter = ArrayAdapter.createFromResource(this, R.array.generalArray, android.R.layout.simple_spinner_item);
        complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(complaintAdapter);


        encoded_string="";image_name="";

        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getFileUri();
                i.putExtra(MediaStore.EXTRA_OUTPUT,file_uri);
                //i.putExtra(MediaStore.ACTION_IMAGE_CAPTURE,file_uri);
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

        profile_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (encoded_string.length()>5 && message.getText().toString().length()>5){
                    registerRequest2();
                }else{
                    Toast.makeText(creategeneralobs.this, "Please enter all the values.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerRequest2() {
        final ProgressDialog pd = new ProgressDialog(creategeneralobs.this);
        pd.setMessage("Registering..");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userId",sharedPreferences.getString("userId","0"));
            jsonObject.put("picture",encoded_string.trim());
            jsonObject.put("image_name",image_name.trim().replaceAll(" ","%20"));
            jsonObject.put("cat",category.getSelectedItem().toString());
            jsonObject.put("placename",name.getText().toString().trim());
            jsonObject.put("message",message.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, new functions().baseIP+"creategeneral.php", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pd.dismiss();
                        Log.e("Message from server", jsonObject.toString());
                        Toast.makeText(getApplication(), "Register Successfully", Toast.LENGTH_SHORT).show();
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
        final ProgressDialog pd = new ProgressDialog(creategeneralobs.this);
        pd.setMessage("Uploading");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"creategeneral.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.e(TAG, "onResponse: "+response );
                        if (response.equals("success")){
                            new functions().exitDialog(creategeneralobs.this,"Success","You have successfully registered your observation on our platform. We will contact you shortly","ok");
                        }else{
                            new functions().normalDialog(creategeneralobs.this,"Error","We could not register your request at this moment. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(creategeneralobs.this, "Error: Image might be too large.", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(createcar.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",sharedPreferences.getString("userId","0"));
                map.put("picture",encoded_string.trim());
                map.put("image_name",image_name.trim().replaceAll(" ","%20"));
                map.put("cat",category.getSelectedItem().toString());
                map.put("message",message.getText().toString().trim().replaceAll(" ","%20"));

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
            pd= new ProgressDialog(creategeneralobs.this);
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
}
