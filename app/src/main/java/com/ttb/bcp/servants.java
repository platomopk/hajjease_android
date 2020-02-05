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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class servants extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar,toolbar_add;
    RecyclerView recyclerView;

    ArrayList<String> idList;
    ArrayList<String> nameList;
    ArrayList<String> createdList;
    ArrayList<String> cnicList;
    ArrayList<String> addressList;
    ArrayList<String> activatedList;
    ArrayList<String> profileImgList;

    LinearLayoutManager layoutManager;
    SharedPreferences sharedPref;
    static Activity activity;
    String params="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servants);

        sharedPref=getSharedPreferences("local_db",MODE_PRIVATE);

        activity=this;

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        toolbar_add = (ImageView) findViewById(R.id.toolbar_add);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        profile_toolbar.setVisibility(View.GONE);
        toolbar_add.setVisibility(View.VISIBLE);

        title_toolbar.setText("HELPERS");

        params ="id="+sharedPref.getString("userId","0");

        toolbar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(servants.this,createservant.class));
            }
        });

    }

    @Override
    protected void onResume() {
        new listServants().execute(params);
        super.onResume();
    }

    public static class MyViewHolderRes extends RecyclerView.ViewHolder {

        TextView id,name,created,cnic,address;
        ImageView image,activated;
        LinearLayout mainLinear;

        public MyViewHolderRes(View itemView) {
            super(itemView);

            id = (TextView) itemView.findViewById(R.id.servantID);
            name = (TextView) itemView.findViewById(R.id.servantName);
            created = (TextView) itemView.findViewById(R.id.servantCreated);
            cnic = (TextView) itemView.findViewById(R.id.servantCnic);
            address = (TextView) itemView.findViewById(R.id.servantAddress);

            image = (ImageView) itemView.findViewById(R.id.servantImg);
            activated = (ImageView) itemView.findViewById(R.id.servantActivated);

            mainLinear = (LinearLayout) itemView.findViewById(R.id.notificationMainLayout);
        }
    }

    public static class MyListAdapter extends RecyclerView.Adapter<MyViewHolderRes> {

        ArrayList<String> idList;
        ArrayList<String> nameList;
        ArrayList<String> createdList;
        ArrayList<String> cnicList;
        ArrayList<String> addressList;
        ArrayList<String> activatedList;
        ArrayList<String> profileImgList;

        public MyListAdapter(ArrayList<String> id, ArrayList<String> name, ArrayList<String> created,ArrayList<String> cnic,ArrayList<String> address,ArrayList<String> activated,ArrayList<String> profile) {
            this.idList = id;
            this.nameList = name;
            this.createdList = created;
            this.cnicList=cnic;
            this.addressList=address;
            this.activatedList=activated;
            this.profileImgList = profile;
        }

        @Override
        public int getItemCount() {
            if (activatedList==null){
                return 0;
            }else {
                return activatedList.size();
            }
        }

        @Override
        public MyViewHolderRes onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_servant_layout, parent, false);
            MyViewHolderRes holder = new MyViewHolderRes(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderRes holder, final int position) {

            if (activatedList.get(position).equals("0")){
                holder.activated.setImageResource(R.drawable.tick);
            }else{
                holder.activated.setImageResource(R.drawable.dtick);
            }

            holder.id.setText(idList.get(position));
            holder.name.setText(nameList.get(position));
            holder.created.setText(createdList.get(position));
            holder.cnic.setText(cnicList.get(position));

            if (addressList.get(position).length()>22){
                holder.address.setText(addressList.get(position).substring(0,22)+" ...");
            }else{
                holder.address.setText(addressList.get(position));
            }


            Picasso.get().load(new functions().baseIP+profileImgList.get(position)).placeholder(R.drawable.placeholder).fit().centerCrop().into(holder.image);

            holder.mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),updateservant.class);
                    i.putExtra("servantId",holder.id.getText().toString().trim());
                    view.getContext().startActivity(i);
                }
            });

        }
    }

    public class listServants extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(servants.this);
            pd.setMessage("Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getallservants.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    idList= new ArrayList<String>();
                    nameList =  new ArrayList<String>();
                    createdList =  new ArrayList<String>();
                    cnicList =  new ArrayList<String>();
                    addressList =  new ArrayList<String>();
                    activatedList =  new ArrayList<String>();
                    profileImgList =  new ArrayList<String>();




                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);

                        idList.add(Jobj.getString("servantId"));
                        nameList.add(Jobj.getString("name"));
                        createdList.add(Jobj.getString("createdOn"));
                        cnicList.add(Jobj.getString("cnic"));
                        addressList.add(Jobj.getString("address"));
                        activatedList.add(Jobj.getString("isActivated"));
                        profileImgList.add(Jobj.getString("picture"));


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
                    recyclerView.setAdapter(new MyListAdapter(idList,nameList,createdList,cnicList,addressList,activatedList,profileImgList));
                }else {
                    Toast.makeText(servants.this, "No Helpers Yet", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
