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

public class cars extends AppCompatActivity {

    TextView title_toolbar;
    ImageView profile_toolbar,toolbar_add;
    RecyclerView recyclerView;

    ArrayList<String> idList;
    ArrayList<String> makeList;
    ArrayList<String> modelList;
    ArrayList<String> createdList;
    ArrayList<String> colorList;
    ArrayList<String> regList;
    ArrayList<String> profileImgList;
    ArrayList<String> activatedList;


    LinearLayoutManager layoutManager;
    SharedPreferences sharedPref;
    static Activity activity;
    String params="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        sharedPref=getSharedPreferences("local_db",MODE_PRIVATE);

        activity=this;

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        profile_toolbar = (ImageView) findViewById(R.id.toolbar_profile);
        toolbar_add = (ImageView) findViewById(R.id.toolbar_add);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        profile_toolbar.setVisibility(View.GONE);
        toolbar_add.setVisibility(View.VISIBLE);

        title_toolbar.setText("CARS");

        params ="id="+sharedPref.getString("userId","0");

        toolbar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(cars.this,createcar.class));
            }
        });

    }

    @Override
    protected void onResume() {
        new listCars().execute(params);
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
        ArrayList<String> makeList;
        ArrayList<String> modelList;
        ArrayList<String> createdList;
        ArrayList<String> colorList;
        ArrayList<String> regList;
        ArrayList<String> profileImgList;
        ArrayList<String> activatedList;

        public MyListAdapter(ArrayList<String> id, ArrayList<String> make, ArrayList<String> model,ArrayList<String> created,ArrayList<String> color,ArrayList<String> reg,ArrayList<String> profile,ArrayList<String> activated) {
            this.idList = id;
            this.makeList = make;
            this.modelList = model;
            this.createdList=created;
            this.colorList=color;
            this.regList=reg;
            this.profileImgList = profile;
            this.activatedList = activated;
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
            holder.name.setText(makeList.get(position)+" "+modelList.get(position));
            holder.created.setText(createdList.get(position));
            holder.cnic.setText(colorList.get(position));

            if (regList.get(position).length()>22){
                holder.address.setText(regList.get(position).substring(0,22)+" ...");
            }else{
                holder.address.setText(regList.get(position));
            }

            Picasso.get().load(new functions().baseIP+profileImgList.get(position)).placeholder(R.drawable.placeholder).fit().centerCrop().into(holder.image);

            holder.mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),updatecar.class);
                    i.putExtra("carId",holder.id.getText().toString().trim());
                    view.getContext().startActivity(i);
                }
            });

        }
    }

    public class listCars extends AsyncTask<String,Void,Boolean> {
        ProgressDialog pd;
        JSONObject Jobj;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(cars.this);
            pd.setMessage("Wait ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"getallcars.php?"+strings[0]);

                JSONArray jArr = new JSONArray(jsonString);

                if (jArr.length()>0){

                    idList= new ArrayList<String>();
                    makeList =  new ArrayList<String>();
                    modelList =  new ArrayList<String>();
                    createdList =  new ArrayList<String>();
                    colorList =  new ArrayList<String>();
                    regList =  new ArrayList<String>();
                    profileImgList =  new ArrayList<String>();
                    activatedList =  new ArrayList<String>();





                    for (int i = 0; i < jArr.length(); i++) {
                        Jobj = jArr.getJSONObject(i);

                        idList.add(Jobj.getString("carId"));
                        makeList.add(Jobj.getString("make"));
                        modelList.add(Jobj.getString("model"));
                        createdList.add(Jobj.getString("created_on"));
                        colorList.add(Jobj.getString("color"));
                        regList.add(Jobj.getString("registeration_number"));
                        profileImgList.add(Jobj.getString("picture"));
                        activatedList.add(Jobj.getString("is_activated"));


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
                    recyclerView.setAdapter(new MyListAdapter(idList,makeList,modelList,createdList,colorList,regList,profileImgList,activatedList));
                }else {
                    Toast.makeText(cars.this, "No Cars Registered Yet", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
