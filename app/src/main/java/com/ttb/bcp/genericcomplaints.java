package com.ttb.bcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
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

public class genericcomplaints extends AppCompatActivity {

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
    ArrayList<String> nameList;
    ArrayList<String> addressList;


    LinearLayoutManager layoutManager;
    SharedPreferences sharedPref;
    static Activity activity;
    String params="";
    ImageView logout;

    CountDownTimer countdowntimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genericcomplaints);

        sharedPref=getSharedPreferences("local_db",MODE_PRIVATE);

        activity=this;

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        toolbar_add = (ImageView) findViewById(R.id.toolbar_add);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        profile_toolbar.setVisibility(View.GONE);

        title_toolbar.setText("OPEN COMPLAINTS");

        params ="id="+sharedPref.getString("userId","0");

        logout = (ImageView) findViewById(R.id.toolbar_signout);
        logout.setVisibility(View.VISIBLE);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        triggerStuff();

    }

    @Override
    protected void onResume() {
        new listComplaints().execute(params);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdowntimer!=null){
            countdowntimer.cancel();
        }
        finish();
    }



    private void logout(){
        getSharedPreferences("local_db",MODE_PRIVATE).edit().putString("loggedin","").commit();
        startActivity(new Intent(genericcomplaints.this,login.class));
        finish();
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
        ArrayList<String> messageList;
        ArrayList<String> createdDateList;
        ArrayList<String> createdTimeList;
        ArrayList<String> nameList;
        ArrayList<String> addressList;

        public MyListAdapter(ArrayList<String> id, ArrayList<String> cat, ArrayList<String> title,ArrayList<String> status,ArrayList<String> message,ArrayList<String> d,ArrayList<String> t,ArrayList<String> name,ArrayList<String> address) {
            this.idList = id;
            this.categoryList = cat;
            this.titleList = title;
            this.statusList=status;
            this.messageList = message;
            this.createdDateList = d;
            this.createdTimeList = t;
            this.nameList = name;
            this.addressList = address;
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
        public int getItemViewType(int position) {
            return position;
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

            if (categoryList.get(position).equals("Power")){
                holder.image.setImageResource(R.drawable.power);
            }else if (categoryList.get(position).equals("Water")){
                holder.image.setImageResource(R.drawable.water);
            }else if (categoryList.get(position).equals("Gas")){
                holder.image.setImageResource(R.drawable.gas);
            }else if (categoryList.get(position).equals("Security")){
                holder.image.setImageResource(R.drawable.security);
            }

            holder.id.setText(idList.get(position));
            holder.ticket.setText("Ticket # "+idList.get(position));
            holder.status.setText(statusList.get(position));
            holder.title.setText(titleList.get(position));

            holder.mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),complaintdescgeneric.class);
                    i.putExtra("ticket",holder.id.getText().toString().trim());
                    i.putExtra("category",categoryList.get(position));
                    i.putExtra("title",holder.title.getText().toString().trim());
                    i.putExtra("message",messageList.get(position));
                    i.putExtra("status",holder.status.getText().toString().trim());
                    i.putExtra("date",createdDateList.get(position));
                    i.putExtra("time",createdTimeList.get(position));
                    i.putExtra("name",nameList.get(position));
                    i.putExtra("address",addressList.get(position));

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

            pd = new ProgressDialog(genericcomplaints.this);
            pd.setMessage("Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            //pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getallopencomplaintsandroid.php");

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    idList= new ArrayList<String>();
                    categoryList =  new ArrayList<String>();
                    titleList =  new ArrayList<String>();
                    statusList =  new ArrayList<String>();
                    messageList =  new ArrayList<String>();
                    createdDateList =  new ArrayList<String>();
                    createdTimeList =  new ArrayList<String>();
                    nameList =  new ArrayList<String>();
                    addressList =  new ArrayList<String>();

                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);

                        idList.add(Jobj.getString("complaintId"));
                        categoryList.add(Jobj.getString("category"));
                        titleList.add(Jobj.getString("title"));
                        statusList.add(Jobj.getString("status"));
                        messageList.add(Jobj.getString("message"));
                        createdDateList.add(Jobj.getString("createdOn"));
                        createdTimeList.add(Jobj.getString("createdTime"));
                        nameList.add(Jobj.getString("name"));
                        addressList.add(Jobj.getString("address"));



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
//            if (pd.isShowing()) {
//                pd.dismiss();
//            }
            try {

                if (result){
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(new MyListAdapter(idList,categoryList,titleList,statusList,messageList,createdDateList,createdTimeList,nameList,addressList));
                }else {
                    Toast.makeText(genericcomplaints.this, "No Complaints Yet", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void triggerStuff(){
        countdownaction();
    }

    void countdownaction(){
        countdowntimer = new CountDownTimer(9000000,120000) {
            @Override
            public void onTick(long l) {
                //call and update stuff
                new listComplaints().execute();
                //Toast.makeText(genericcomplaints.this, "Countdowntimer fired", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                triggerStuff();
            }
        }.start();
    }
}
