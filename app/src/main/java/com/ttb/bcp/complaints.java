package com.ttb.bcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class complaints extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar,toolbar_add;
    RecyclerView recyclerView;

    ArrayList<String> idList;
    ArrayList<String> categoryList;
    ArrayList<String> titleList;
    ArrayList<String> statusList;
    ArrayList<String> messageList;
    ArrayList<String> createdDateList;
    ArrayList<String> createdTimeList;


    LinearLayoutManager layoutManager;
    SharedPreferences sharedPref;
    static Activity activity;
    String params="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        sharedPref=getSharedPreferences("local_db",MODE_PRIVATE);

        activity=this;

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        toolbar_add = (ImageView) findViewById(R.id.toolbar_add);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        profile_toolbar.setVisibility(View.GONE);
        toolbar_add.setVisibility(View.VISIBLE);

//        title_toolbar.setText("COMPLAINTS");
        title_toolbar.setText("MY FEEDBACKS");

        params ="id="+sharedPref.getString("userId","0");

        toolbar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(complaints.this,createcomplaint.class));
            }
        });

    }

    @Override
    protected void onResume() {
        new listComplaints().execute(params);
        super.onResume();
    }

    public static class MyViewHolderRes extends RecyclerView.ViewHolder {

        TextView id,title,ticket,status,category;
        ImageView image;
        LinearLayout mainLinear;

        public MyViewHolderRes(View itemView) {
            super(itemView);

            id = (TextView) itemView.findViewById(R.id.complaintRID);
            title = (TextView) itemView.findViewById(R.id.complaintRTitle);
            ticket = (TextView) itemView.findViewById(R.id.complaintRTicket);
            status = (TextView) itemView.findViewById(R.id.complaintRStatus);
            category = (TextView) itemView.findViewById(R.id.complaintRCategory);

            image = (ImageView) itemView.findViewById(R.id.complaintRImage);

            mainLinear = (LinearLayout) itemView.findViewById(R.id.complaintRLinear);
        }
    }

    public static class MyListAdapter extends RecyclerView.Adapter<MyViewHolderRes> {

        ArrayList<String> idList;
        ArrayList<String> categoryList;
        ArrayList<String> titleList;
        ArrayList<String> statusList;
        ArrayList<String> messageList;
        ArrayList<String> createdDateList;
        ArrayList<String> createdTimeList;

        public MyListAdapter(ArrayList<String> id, ArrayList<String> cat, ArrayList<String> title,ArrayList<String> status,ArrayList<String> message,ArrayList<String> d,ArrayList<String> t) {
            this.idList = id;
            this.categoryList = cat;
            this.titleList = title;
            this.statusList=status;
            this.messageList = message;
            this.createdDateList = d;
            this.createdTimeList = t;
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

            holder.category.setText(categoryList.get(position));

            if (categoryList.get(position).equals("Power")){
                holder.image.setImageResource(R.drawable.power);
            }else if (categoryList.get(position).equals("Water")){
                holder.image.setImageResource(R.drawable.water);
            }else if (categoryList.get(position).equals("Gas")){
                holder.image.setImageResource(R.drawable.gas);
            }else if (categoryList.get(position).equals("Security")){
                holder.image.setImageResource(R.drawable.security);
            }else if (categoryList.get(position).equals("Food")){
                holder.image.setImageResource(R.drawable.dish);
            }else if (categoryList.get(position).equals("Cleanliness")){
                holder.image.setImageResource(R.drawable.cleaning);
            }else if (categoryList.get(position).equals("Others")){
                holder.image.setImageResource(R.drawable.puzzle);
            }



            holder.id.setText(idList.get(position));
            holder.ticket.setText("Ticket # "+idList.get(position));
            holder.status.setText(statusList.get(position));
            holder.title.setText(titleList.get(position).length() > 16 ? titleList.get(position).substring(0,16)+"..." : titleList.get(position));

            holder.mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),complaintdesc.class);
                    i.putExtra("ticket",holder.id.getText().toString().trim());
                    i.putExtra("category",categoryList.get(position));
                    i.putExtra("title",holder.title.getText().toString().trim());
                    i.putExtra("message",messageList.get(position));
                    i.putExtra("status",holder.status.getText().toString().trim());
                    i.putExtra("date",createdDateList.get(position));
                    i.putExtra("time",createdTimeList.get(position));
                    view.getContext().startActivity(i);
                }
            });

        }
    }

    public class listComplaints extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(complaints.this);
            pd.setMessage("Please Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getallcomplaints.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    idList= new ArrayList<String>();
                    categoryList =  new ArrayList<String>();
                    titleList =  new ArrayList<String>();
                    statusList =  new ArrayList<String>();
                    messageList =  new ArrayList<String>();
                    createdDateList =  new ArrayList<String>();
                    createdTimeList =  new ArrayList<String>();

                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);

                        idList.add(Jobj.getString("complaintId"));
                        categoryList.add(Jobj.getString("category"));
                        titleList.add(Jobj.getString("title"));
                        statusList.add(Jobj.getString("status"));
                        messageList.add(Jobj.getString("message"));
                        createdDateList.add(Jobj.getString("createdOn"));
                        createdTimeList.add(Jobj.getString("createdTime"));



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
                    recyclerView.setAdapter(new MyListAdapter(idList,categoryList,titleList,statusList,messageList,createdDateList,createdTimeList));
                }else {
                    Toast.makeText(complaints.this, "No Feedbacks Yet", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
