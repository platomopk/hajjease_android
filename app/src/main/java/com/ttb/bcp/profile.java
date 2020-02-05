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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar,image;
    Button register;
    ImageButton selectImg,galleryImg;
    EditText firstName,lastName,cnic,cell,landline,street,houseNumber,email,password;
    Spinner gender,city,phase,sector;
    ArrayAdapter<CharSequence> genderAdapter,cityAdapter,phaseAdapter,sectorAdapter;
    String encoded_string, image_name;
    Bitmap bitmap;
    File file;
    Uri file_uri;
    static final String TAG="profile";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Switch carpoolSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);

        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);
        editor= sharedPreferences.edit();

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        cnic = (EditText) findViewById(R.id.cnic);
        cell = (EditText) findViewById(R.id.cell);
        landline = (EditText) findViewById(R.id.landline);
        street = (EditText) findViewById(R.id.street);
        houseNumber = (EditText) findViewById(R.id.houseNumber);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        gender = (Spinner) findViewById(R.id.gender);
        city = (Spinner) findViewById(R.id.city);
        phase = (Spinner) findViewById(R.id.phase);
        sector = (Spinner) findViewById(R.id.sector);
        image = (ImageView) findViewById(R.id.image);

        register = (Button) findViewById(R.id.register);
        selectImg = (ImageButton) findViewById(R.id.selectImg);
        galleryImg = (ImageButton) findViewById(R.id.galleryImg);

        carpoolSwitch = (Switch) findViewById(R.id.carpoolSwitch);

        selectImg.requestFocus();

        profile_toolbar.setVisibility(View.GONE);
        title_toolbar.setText("MY PROFILE");

        genderAdapter = ArrayAdapter.createFromResource(this, R.array.genderArray, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);

        cityAdapter = ArrayAdapter.createFromResource(this, R.array.cityArray, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        phaseAdapter = ArrayAdapter.createFromResource(this, R.array.phaseArray, android.R.layout.simple_spinner_item);
        phaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phase.setAdapter(phaseAdapter);

        sectorAdapter = ArrayAdapter.createFromResource(this, R.array.sectorArray, android.R.layout.simple_spinner_item);
        sectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sector.setAdapter(sectorAdapter);

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

                    if (firstName.getText().toString().trim().equals("") || lastName.getText().toString().trim().equals("") || cnic.getText().toString().trim().equals("") || landline.getText().toString().trim().equals("") || cell.getText().toString().trim().equals("") || street.getText().toString().trim().equals("") || houseNumber.getText().toString().trim().equals("") || email.getText().toString().trim().equals("")) {
                        Toast.makeText(profile.this, "There are some empty fields here", Toast.LENGTH_SHORT).show();
                    } else {
                        try{
                            registerRequest2();
                            //registerRequest();
                        }catch (Exception e){
                            Log.e(TAG, "regClick: "+e.getMessage());
                        }
                    }
            }
        });

        new getProfile().execute("userId="+sharedPreferences.getString("userId","0"));
    }

    private void registerRequest2() {
        final ProgressDialog pd = new ProgressDialog(profile.this);
        pd.setMessage("Updating");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("picture",encoded_string.trim());
            jsonObject.put("image_name",image_name.trim().replaceAll(" ","%20"));
            jsonObject.put("first_name",firstName.getText().toString().trim());
            jsonObject.put("last_name",lastName.getText().toString().trim());
            jsonObject.put("gender",gender.getSelectedItem().toString());
            jsonObject.put("cnic",cnic.getText().toString().trim());
            jsonObject.put("cell",cell.getText().toString().trim());
            jsonObject.put("landline",landline.getText().toString().trim());
            jsonObject.put("city",city.getSelectedItem().toString().trim());
            jsonObject.put("phase",phase.getSelectedItem().toString().trim());
            jsonObject.put("sector",sector.getSelectedItem().toString().trim());
            jsonObject.put("street",street.getText().toString().trim().trim());
            jsonObject.put("house_number",houseNumber.getText().toString().trim());
            jsonObject.put("email",email.getText().toString().trim());
            jsonObject.put("password",password.getText().toString().trim());
            jsonObject.put("usertoken",FirebaseInstanceId.getInstance().getToken().trim());
            jsonObject.put("userId",sharedPreferences.getString("userId","0"));

            if (carpoolSwitch.isChecked()){
                jsonObject.put("carpoolnotification","1");
            }else{
                jsonObject.put("carpoolnotification","0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, new functions().baseIP+"update2.php", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pd.dismiss();
                        Log.e("Message from server", jsonObject.toString());
                        Toast.makeText(getApplication(), "updated Successfully", Toast.LENGTH_SHORT).show();
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
        final ProgressDialog pd = new ProgressDialog(profile.this);
        pd.setMessage("Updating");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"update.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            editor.putString("lastName",lastName.getText().toString().trim());
                            editor.commit();
                            new functions().exitDialog(profile.this,"Congrats","You have successfully updated your profile.","ok");
                        }else{
                            new functions().normalDialog(profile.this,"Error","There was a network issue. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Toast.makeText(profile.this, "Error: The image file might be too large.", Toast.LENGTH_SHORT).show();
                //new functions().finishDialog(profile.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("picture",encoded_string.trim());
                map.put("image_name",image_name.trim().replaceAll(" ","%20"));
                map.put("first_name",firstName.getText().toString().trim().replaceAll(" ","%20"));
                map.put("last_name",lastName.getText().toString().trim().replaceAll(" ","%20"));
                map.put("gender",gender.getSelectedItem().toString());
                map.put("cnic",cnic.getText().toString().trim());
                map.put("cell",cell.getText().toString().trim());
                map.put("landline",landline.getText().toString().trim());
                map.put("city",city.getSelectedItem().toString().trim().replaceAll(" ","%20"));
                map.put("phase",phase.getSelectedItem().toString().trim());
                map.put("sector",sector.getSelectedItem().toString().trim().replaceAll(" ","%20"));
                map.put("street",street.getText().toString().trim().trim());
                map.put("house_number",houseNumber.getText().toString().trim().replaceAll(" ","%20"));
                map.put("email",email.getText().toString().trim().replaceAll(" ","%20"));
                map.put("password",password.getText().toString().trim());
                map.put("usertoken",FirebaseInstanceId.getInstance().getToken().trim());
                map.put("userId",sharedPreferences.getString("userId","0"));

                if (carpoolSwitch.isChecked()){
                    map.put("carpoolnotification","1");
                }else{
                    map.put("carpoolnotification","0");
                }

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

        }
    }

    private void getFileUri() {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();


        image_name = "bcp-"+ts+".jpg";


        try {
            file= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);

            //file= new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + image_name);
            file_uri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class encode_image extends AsyncTask<Void,Void,Void> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd= new ProgressDialog(profile.this);
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
    public class getProfile extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(profile.this);
            pd.setMessage("Getting Profile ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getprofile.php?"+strings[0]);

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
                    gender.setSelection(genderAdapter.getPosition(profileObj.getString("gender")));
                    city.setSelection(cityAdapter.getPosition(profileObj.getString("city")));
                    phase.setSelection(phaseAdapter.getPosition(profileObj.getString("phase")));
                    sector.setSelection(sectorAdapter.getPosition(profileObj.getString("sector")));


                    firstName.setText(profileObj.getString("firstname"));
                    lastName.setText(profileObj.getString("lastname"));
                    cnic.setText(profileObj.getString("cnic"));
                    cell.setText(profileObj.getString("cell"));
                    landline.setText(profileObj.getString("landline"));
                    street.setText(profileObj.getString("street"));
                    houseNumber.setText(profileObj.getString("housenumber"));
                    email.setText(profileObj.getString("email"));
                    password.setHint("(Unchanged)");

                    if (profileObj.getString("carpoolnotification").equals("0")){
                        carpoolSwitch.setChecked(false);
                    }else{
                        carpoolSwitch.setChecked(true);
                    }

                    Picasso.get().load(new functions().baseIP+profileObj.getString("picture")).placeholder(R.drawable.placeholder).fit().centerCrop().into(image);
                }else {
                    new functions().finishDialog(profile.this,"Alert!","We could not get your profile data at this moment","ok");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
