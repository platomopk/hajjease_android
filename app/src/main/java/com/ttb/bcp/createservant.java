package com.ttb.bcp;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class createservant extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar,image;
    Button register,startTime,endTime;
    ImageButton selectImg,galleryImg;
    EditText firstName,lastName,cnic,cell,landline,houseNumber ;
    Spinner gender,homeKeys,carKeys,homeAlone,driveAlone,servanttype;
    ArrayAdapter<CharSequence> genderAdapter,boolAdapter,servanttypeadapter;
    String encoded_string, image_name;
    Bitmap bitmap;
    File file;
    Uri file_uri;
    static final String TAG="servant create";
    CheckBox mon,tue,wed,thu,fri,sat,sun;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createservant);

        sharedPreferences = getSharedPreferences("local_db",MODE_PRIVATE);

        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        cnic = (EditText) findViewById(R.id.cnic);
        cell = (EditText) findViewById(R.id.cell);
        landline = (EditText) findViewById(R.id.landline);
        houseNumber = (EditText) findViewById(R.id.houseNumber);
        startTime = (Button) findViewById(R.id.timeStart);
        endTime = (Button) findViewById(R.id.timeEnd);

        mon = (CheckBox) findViewById(R.id.monday);
        tue = (CheckBox) findViewById(R.id.tuesday);
        wed = (CheckBox) findViewById(R.id.wednesday);
        thu = (CheckBox) findViewById(R.id.thursday);
        fri = (CheckBox) findViewById(R.id.friday);
        sat = (CheckBox) findViewById(R.id.saturday);
        sun = (CheckBox) findViewById(R.id.sunday);


        gender = (Spinner) findViewById(R.id.gender);
        homeKeys = (Spinner) findViewById(R.id.houseKeys);
        carKeys = (Spinner) findViewById(R.id.carKeys);
        driveAlone = (Spinner) findViewById(R.id.driveAlone);
        homeAlone = (Spinner) findViewById(R.id.homeAlone);
        servanttype = (Spinner) findViewById(R.id.servanttype);

        image = (ImageView) findViewById(R.id.image);

        register = (Button) findViewById(R.id.register);
        selectImg = (ImageButton) findViewById(R.id.selectImg);
        galleryImg = (ImageButton) findViewById(R.id.galleryImg);

        selectImg.requestFocus();

        profile_toolbar.setVisibility(View.GONE);
        title_toolbar.setText("REGISTER HELPER");

        servanttypeadapter = ArrayAdapter.createFromResource(this, R.array.servantTypeArr, android.R.layout.simple_spinner_item);
        servanttypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servanttype.setAdapter(servanttypeadapter);

        genderAdapter = ArrayAdapter.createFromResource(this, R.array.genderArray, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);

        boolAdapter = ArrayAdapter.createFromResource(this, R.array.boolArr, android.R.layout.simple_spinner_item);
        boolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeKeys.setAdapter(boolAdapter);
        carKeys.setAdapter(boolAdapter);
        driveAlone.setAdapter(boolAdapter);
        homeAlone.setAdapter(boolAdapter);

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
                if (encoded_string.length() > 0) {
                    if (firstName.getText().toString().trim().equals("") || lastName.getText().toString().trim().equals("") || cnic.getText().toString().trim().equals("") || landline.getText().toString().trim().equals("") || cell.getText().toString().trim().equals("") || houseNumber.getText().toString().trim().equals("") || startTime.getText().toString().equals("set time") || endTime.getText().toString().equals("set time")) {
                        Toast.makeText(createservant.this, "There are some empty fields here", Toast.LENGTH_SHORT).show();
                    } else {
                        try{
                            //Toast.makeText(createservant.this, String.valueOf(mon.isChecked()), Toast.LENGTH_SHORT).show();
                            registerRequest2();
                        }catch (Exception e){
                            Log.e(TAG, "regClick: "+e.getMessage());
                        }
                    }
                } else {
                    Toast.makeText(createservant.this, "Please Select The Profile Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(createservant.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String time = hourOfDay + ":" + minute;
//                                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
//                                Date date = null;
//                                try {
//                                    date = fmt.parse(time );
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
//                                String formattedTime=fmtOut.format(date);
                                startTime.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.setTitle("Start Time");
                timePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(createservant.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String time = hourOfDay + ":" + minute;



//                                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
//                                Date date = null;
//                                try {
//                                    date = fmt.parse(time );
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
//                                String formattedTime=fmtOut.format(date);
                                endTime.setText(time);


                            }
                        },hour,minute,false);


                timePickerDialog.setTitle("End Time");
                timePickerDialog.show();
            }
        });
    }

    private void registerRequest2() {
        final ProgressDialog pd = new ProgressDialog(createservant.this);
        pd.setMessage("Registering..");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userId",sharedPreferences.getString("userId","0"));
            jsonObject.put("picture",encoded_string.trim());
            jsonObject.put("image_name",image_name.trim().replaceAll(" ","%20"));
            jsonObject.put("first_name",firstName.getText().toString().trim());
            jsonObject.put("last_name",lastName.getText().toString().trim());
            jsonObject.put("gender",gender.getSelectedItem().toString());
            jsonObject.put("cnic",cnic.getText().toString().trim());
            jsonObject.put("cell",cell.getText().toString().trim());
            jsonObject.put("landline",landline.getText().toString().trim());
            jsonObject.put("address",houseNumber.getText().toString().trim());

            jsonObject.put("monday",String.valueOf(mon.isChecked()));
            jsonObject.put("tuesday",String.valueOf(tue.isChecked()));
            jsonObject.put("wednesday",String.valueOf(wed.isChecked()));
            jsonObject.put("thursday",String.valueOf(thu.isChecked()));
            jsonObject.put("friday",String.valueOf(fri.isChecked()));
            jsonObject.put("saturday",String.valueOf(sat.isChecked()));
            jsonObject.put("sunday",String.valueOf(sun.isChecked()));

            jsonObject.put("startTime",startTime.getText().toString());
            jsonObject.put("endTime",endTime.getText().toString());

            jsonObject.put("homekeys",homeKeys.getSelectedItem().toString());
            jsonObject.put("carkeys",carKeys.getSelectedItem().toString());
            jsonObject.put("drivealone",driveAlone.getSelectedItem().toString());
            jsonObject.put("homealone",homeAlone.getSelectedItem().toString());

            jsonObject.put("type",servanttype.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, new functions().baseIP+"createservant.php", jsonObject,
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
        final ProgressDialog pd = new ProgressDialog(createservant.this);
        pd.setMessage("Uploading");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"createservant.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            new functions().exitDialog(createservant.this,"Success","You have successfully registered a servant on our platform. Send them to our office for biometric verfication.","ok");
                        }else{
                            new functions().normalDialog(createservant.this,"Error","We could not register you at this moment. Please try again later.","ok");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(createservant.this, "Error: Image might be too large.", Toast.LENGTH_SHORT).show();

                //new functions().finishDialog(createservant.this,"Stack Trace",error.getMessage().toString(),"ok");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",sharedPreferences.getString("userId","0"));
                map.put("picture",encoded_string.trim());
                map.put("image_name",image_name.trim().replaceAll(" ","%20"));
                map.put("first_name",firstName.getText().toString().trim().replaceAll(" ","%20"));
                map.put("last_name",lastName.getText().toString().trim().replaceAll(" ","%20"));
                map.put("gender",gender.getSelectedItem().toString());
                map.put("cnic",cnic.getText().toString().trim());
                map.put("cell",cell.getText().toString().trim());
                map.put("landline",landline.getText().toString().trim());
                map.put("address",houseNumber.getText().toString().trim().replaceAll(" ","%20"));

                map.put("monday",String.valueOf(mon.isChecked()));
                map.put("tuesday",String.valueOf(tue.isChecked()));
                map.put("wednesday",String.valueOf(wed.isChecked()));
                map.put("thursday",String.valueOf(thu.isChecked()));
                map.put("friday",String.valueOf(fri.isChecked()));
                map.put("saturday",String.valueOf(sat.isChecked()));
                map.put("sunday",String.valueOf(sun.isChecked()));

                map.put("startTime",startTime.getText().toString());
                map.put("endTime",endTime.getText().toString());

                map.put("homekeys",homeKeys.getSelectedItem().toString());
                map.put("carkeys",carKeys.getSelectedItem().toString());
                map.put("drivealone",driveAlone.getSelectedItem().toString());
                map.put("homealone",homeAlone.getSelectedItem().toString());

                map.put("type",servanttype.getSelectedItem().toString());

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
            pd= new ProgressDialog(createservant.this);
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
