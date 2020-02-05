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

public class notifications extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar;
    RecyclerView recyclerView;

    ArrayList<String> idList;
    ArrayList<String> subjectList;
    ArrayList<String> dateList;
    ArrayList<String> contentList;
    ArrayList<String> seenList;

    LinearLayoutManager layoutManager;
    SharedPreferences sharedPref;
    static Activity activity;
    String params="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        sharedPref=getSharedPreferences("local_db",MODE_PRIVATE);

        activity=this;

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        profile_toolbar.setVisibility(View.GONE);
        title_toolbar.setText("NOTIFICATIONS");

        params ="id="+sharedPref.getString("userId","0");
        //new listNotifications().execute(params);

    }

    @Override
    protected void onResume() {
        new listNotifications().execute(params);
        super.onResume();
        //Toast.makeText(notifications.this, "OnResume", Toast.LENGTH_SHORT).show();
    }

    public static class MyViewHolderRes extends RecyclerView.ViewHolder {

        TextView id,subject,date,content;
        ImageView image;
        LinearLayout mainLinear;

        public MyViewHolderRes(View itemView) {
            super(itemView);

            id = (TextView) itemView.findViewById(R.id.notificationID);
            subject = (TextView) itemView.findViewById(R.id.notificationSubject);
            date = (TextView) itemView.findViewById(R.id.notificationDate);
            content = (TextView) itemView.findViewById(R.id.notificationContent);
            image = (ImageView) itemView.findViewById(R.id.notificationImg);
            mainLinear = (LinearLayout) itemView.findViewById(R.id.notificationMainLayout);
        }
    }

    public static class MyListAdapter extends RecyclerView.Adapter<MyViewHolderRes> {

        ArrayList<String> idList;
        ArrayList<String> subjectList;
        ArrayList<String> dateList;
        ArrayList<String> contentList;
        ArrayList<String> seenList;

        public MyListAdapter(ArrayList<String> id, ArrayList<String> subject, ArrayList<String> date,ArrayList<String> content,ArrayList<String> seen) {
            this.idList = id;
            this.subjectList = subject;
            this.dateList = date;
            this.contentList=content;
            this.seenList=seen;
        }

        @Override
        public int getItemCount() {
            if (contentList==null){
                return 0;
            }else {
                return contentList.size();
            }
        }

        @Override
        public MyViewHolderRes onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_notification_layout, parent, false);
            MyViewHolderRes holder = new MyViewHolderRes(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderRes holder, final int position) {

            if (seenList.get(position).equals("0")){
                holder.image.setImageResource(R.drawable.unread);
            }else{
                holder.image.setImageResource(R.drawable.read);
            }

            holder.id.setText(idList.get(position));
            holder.subject.setText(subjectList.get(position));
            holder.date.setText(dateList.get(position));

            if (contentList.get(position).length()>45){
                holder.content.setText(contentList.get(position).substring(0,45)+" ...");
            }else{
                holder.content.setText(contentList.get(position));
            }

            holder.mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),description.class);
                    i.putExtra("notificationId",holder.id.getText().toString().trim());
                    view.getContext().startActivity(i);
                }
            });

        }
    }

    public class listNotifications extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(notifications.this);
            pd.setMessage("Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getallnotifications.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    idList= new ArrayList<String>();
                    subjectList =  new ArrayList<String>();
                    dateList =  new ArrayList<String>();
                    contentList =  new ArrayList<String>();
                    seenList =  new ArrayList<String>();



                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);

                        idList.add(Jobj.getString("id"));
                        subjectList.add(Jobj.getString("subject"));
                        dateList.add(Jobj.getString("camdate"));
                        contentList.add(Jobj.getString("msg"));
                        seenList.add(Jobj.getString("seen"));


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
                    recyclerView.setAdapter(new MyListAdapter(idList,subjectList,dateList,contentList,seenList));
                }else {
                    Toast.makeText(notifications.this, "No Notifications Yet", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
