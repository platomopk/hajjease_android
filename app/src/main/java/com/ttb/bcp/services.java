package com.ttb.bcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class services extends Activity {

    TextView title_toolbar;
    ImageView profile_toolbar,toolbar_add;
    RecyclerView recyclerView;

    ArrayList<String> idList;
    ArrayList<String> categoryList;
    ArrayList<String> titleList;
    ArrayList<String> statusList;



    LinearLayoutManager layoutManager;
    SharedPreferences sharedPref;
    static Activity activity;
    String params="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        sharedPref=getSharedPreferences("local_db",MODE_PRIVATE);

        activity=this;

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        toolbar_add = (ImageView) findViewById(R.id.toolbar_add);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        profile_toolbar.setVisibility(View.GONE);
        toolbar_add.setVisibility(View.VISIBLE);

        title_toolbar.setText("SERVICES");

        params ="id="+sharedPref.getString("userId","0");


        toolbar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(services.this,createservice.class));
            }
        });

    }

    @Override
    public void onResume() {
        new listComplaints().execute(params);
        super.onResume();
    }

    public static class MyViewHolderRes extends RecyclerView.ViewHolder {

        TextView id,title,ticket,status;
        ImageView image;
        LinearLayout mainLinear;

        public MyViewHolderRes(View itemView) {
            super(itemView);

            id = (TextView) itemView.findViewById(R.id.complaintRID);
            title = (TextView) itemView.findViewById(R.id.complaintRTitle);
            ticket = (TextView) itemView.findViewById(R.id.complaintRTicket);
            status = (TextView) itemView.findViewById(R.id.complaintRStatus);

            image = (ImageView) itemView.findViewById(R.id.complaintRImage);

            mainLinear = (LinearLayout) itemView.findViewById(R.id.complaintRLinear);
        }
    }

    public static class MyListAdapter extends RecyclerView.Adapter<MyViewHolderRes> {

        ArrayList<String> idList;
        ArrayList<String> categoryList;
        ArrayList<String> titleList;
        ArrayList<String> statusList;

        public MyListAdapter(ArrayList<String> id, ArrayList<String> cat, ArrayList<String> title,ArrayList<String> status) {
            this.idList = id;
            this.categoryList = cat;
            this.titleList = title;
            this.statusList=status;
        }

        @Override
        public int getItemCount() {
            if (statusList==null){
                return 0;
            }else {
                return statusList.size();
            }
        }

        @Override
        public MyViewHolderRes onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_complaint_layout, parent, false);
            MyViewHolderRes holder = new MyViewHolderRes(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderRes holder, final int position) {

            if (categoryList.get(position).equals("Plumber")){
                holder.image.setImageResource(R.drawable.plumber);
            }else if (categoryList.get(position).equals("Electrician")){
                holder.image.setImageResource(R.drawable.electrician);
            }else if (categoryList.get(position).equals("Gardener")){
                holder.image.setImageResource(R.drawable.gardener);
            }else if (categoryList.get(position).equals("Driver")){
                holder.image.setImageResource(R.drawable.driver);
            }else if (categoryList.get(position).equals("Handyman")){
                holder.image.setImageResource(R.drawable.handyman);
            }

            holder.id.setText(idList.get(position));
            holder.ticket.setText("Required: "+categoryList.get(position));
            holder.status.setText(statusList.get(position));

            if (titleList.get(position).length()>14){
                holder.title.setText(titleList.get(position).substring(0,13)+" ..");
            }else {
                holder.title.setText(titleList.get(position));
            }

            holder.mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (statusList.get(position).toString().equals("--")){
                        AlertDialog.Builder adb = new AlertDialog.Builder(holder.id.getContext());
                        adb.setTitle("In-Progress");
                        adb.setMessage("Your request for said resource is still under consideration. We will get back to you as soon as we can.\n\n You can delete this request before assignment of any resource.");
                        adb.setCancelable(false);
                        adb.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        adb.setNegativeButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                deleteService(holder.id.getText().toString().trim(),activity);

                            }
                        });
                        AlertDialog ad= adb.create();
                        ad.show();
                    }else{
                        Intent i = new Intent(view.getContext(),servicesdesc.class);
                        i.putExtra("servicesId",holder.id.getText().toString().trim());
                        i.putExtra("status",holder.status.getText().toString().trim());
                        view.getContext().startActivity(i);
                    }
                }
            });

        }
    }

    static void deleteService(final String servicesId, final Activity activity) {
        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage("Please Wait ...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest request = new StringRequest(Request.Method.POST, new functions().baseIP+"deleteservice.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        if (response.equals("success")){
                            //new functions().exitDialog(activity,"Success","Successfully Deleted","ok");
                            Toast.makeText(activity, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }else{
                            Toast.makeText(activity, "Some Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //new functions().finishDialog(activity,"Stack Trace",error.getMessage().toString(),"ok");
                Toast.makeText(activity, "Internet is weak, try again later", Toast.LENGTH_SHORT).show();
                //Toast.makeText(activity, error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("servicesId",servicesId);
                return map;
            }
        };
        requestQueue.add(request);
        pd.show();
    }


    public class listComplaints extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(services.this);
            pd.setMessage("Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getallservices.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    idList= new ArrayList<String>();
                    categoryList =  new ArrayList<String>();
                    titleList =  new ArrayList<String>();
                    statusList =  new ArrayList<String>();

                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);

                        idList.add(Jobj.getString("servicesId"));
                        categoryList.add(Jobj.getString("category"));
                        titleList.add(Jobj.getString("title"));
                        statusList.add(Jobj.getString("status"));


                    }

                }else {
                    return  false;
                }


            } catch (Exception e) {
                Log.e("HOME ListJokes", "doInBackground: "+e.getMessage());
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
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(new MyListAdapter(idList,categoryList,titleList,statusList));
                }else {
                    Toast.makeText(services.this, "No Services Yet", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
