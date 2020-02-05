package com.ttb.bcp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class scheduledetails extends AppCompatActivity {

    RecyclerView recyclerView;
    JSONArray data = new JSONArray();
    JSONArray sanitizedData = new JSONArray();
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduledetails);

        recyclerView = findViewById(R.id.scheduleRecycler);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        ((TextView) findViewById(R.id.toolbar_heading)).setText("SCHEDULES");

        getSchedules();

    }

    void getSchedules() {

        try {
            data.put(new JSONObject()
                    .put("city", "Jeddah")
                    .put("type", "airport")
                    .put("date", "22-09-2019, 02:00 PM")
                    .put("desc", "Flights land at Jeddah International Airport.")
            );
            data.put(new JSONObject()
                    .put("city", "Jeddah")
                    .put("type", "bus")
                    .put("date", "22-09-2019, 03:00 PM")
                    .put("desc", "All hajjaj to reach the bus for departure to Makkah. (Bus # 2112)")
            );
            data.put(new JSONObject()
                    .put("city", "Makkah")
                    .put("type", "Makkah")
                    .put("date", "22-09-2019, 04:30 PM")
                    .put("desc", "Arrival at Makkah.")
            );
            data.put(new JSONObject()
                    .put("city", "Makkah")
                    .put("type", "hotel")
                    .put("date", "22-09-2019, 05:30 PM")
                    .put("desc", "Check in at the hotel (Please check your accomodation).")
            );
            data.put(new JSONObject()
                    .put("city", "Makkah")
                    .put("type", "Haram")
                    .put("date", "22-09-2019, 06:30 PM")
                    .put("desc", "Departure to Haram.")
            );
            data.put(new JSONObject()
                    .put("city", "Madina")
                    .put("type", "bus")
                    .put("date", "28-09-2019, 02:00 PM")
                    .put("desc", "All hajjaj to reach the bus for departure to Madina. (Bus # 2112)")
            );
            data.put(new JSONObject()
                    .put("city", "Asfan")
                    .put("type", "restservices")
                    .put("date", "28-09-2019, 04:30 PM")
                    .put("desc", "Rest at service area")
            );
            data.put(new JSONObject()
                    .put("city", "Madina")
                    .put("type", "Madina")
                    .put("date", "28-09-2019, 07:00 PM")
                    .put("desc", "Arrival at Madina")
            );
            data.put(new JSONObject()
                    .put("city", "Madina")
                    .put("type", "hotel")
                    .put("date", "28-09-2019, 08:00 PM")
                    .put("desc", "Check in at the hotel (Please check your accomodation).")
            );
            data.put(new JSONObject()
                    .put("city", "Madina")
                    .put("type", "masjid")
                    .put("date", "28-09-2019, 08:30 PM")
                    .put("desc", "Departure to Haram.")
            );

            Set<String> cities = new HashSet<>();
            for (int i = 0; i < data.length(); i++) {
                cities.add(data.getJSONObject(i).getString("city"));
            }
//            Log.e("cities", cities.toString());

            Set<String> dates = new HashSet<>();
            for (int i = 0; i < data.length(); i++) {
                dates.add(data.getJSONObject(i).getString("date").split(",")[0]);
            }
//            Log.e("cities", dates.toString());


            //data
            String currentCity = "";
            String currentDate = "";

            for (int i = 0; i < data.length(); i++) {

                if (currentCity.equalsIgnoreCase(data.getJSONObject(i).getString("city"))) {
                    //city header already present
                    if(currentDate.equalsIgnoreCase(data.getJSONObject(i).getString("date").split(",")[0])){
                        //date header already present
                        sanitizedData.put(new JSONObject()
                                .put("type", "content")
                                .put("value", new JSONObject().put("type",data.getJSONObject(i).getString("type")).put("desc",data.getJSONObject(i).getString("desc")).put("date",data.getJSONObject(i).getString("date"))));
                        //for loop content
                    }else{
                        sanitizedData.put(new JSONObject()
                                .put("type", "date")
                                .put("value", data.getJSONObject(i).getString("date").split(",")[0]));
                        currentDate = data.getJSONObject(i).getString("date").split(",")[0];
                        sanitizedData.put(new JSONObject()
                                .put("type", "content")
                                .put("value", new JSONObject().put("type",data.getJSONObject(i).getString("type")).put("desc",data.getJSONObject(i).getString("desc")).put("date",data.getJSONObject(i).getString("date"))));
                    }

                } else {
                    sanitizedData.put(new JSONObject()
                            .put("type", "city")
                            .put("value", data.getJSONObject(i).getString("city")));
                    currentCity = data.getJSONObject(i).getString("city");

                    if(currentDate.equalsIgnoreCase(data.getJSONObject(i).getString("date").split(",")[0])){
                        //date header already present
                        //for loop content
                        sanitizedData.put(new JSONObject()
                                .put("type", "date")
                                .put("value", data.getJSONObject(i).getString("date").split(",")[0]));
                        currentDate = data.getJSONObject(i).getString("date").split(",")[0];
                        sanitizedData.put(new JSONObject()
                                .put("type", "content")
                                .put("value", new JSONObject().put("type",data.getJSONObject(i).getString("type")).put("desc",data.getJSONObject(i).getString("desc")).put("date",data.getJSONObject(i).getString("date"))));
                    }else{
                        sanitizedData.put(new JSONObject()
                                .put("type", "date")
                                .put("value", data.getJSONObject(i).getString("date").split(",")[0]));
                        currentDate = data.getJSONObject(i).getString("date").split(",")[0];
                        sanitizedData.put(new JSONObject()
                                .put("type", "content")
                                .put("value", new JSONObject().put("type",data.getJSONObject(i).getString("type")).put("desc",data.getJSONObject(i).getString("desc")).put("date",data.getJSONObject(i).getString("date"))));
                    }
                }


            }


            //logging sanitzed
            for (int i=0;i<sanitizedData.length();i++){
                Log.e("sanetized",sanitizedData.getJSONObject(i).toString());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 50);
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyListAdapter(sanitizedData));
    }


    public static class MyViewHolderRes extends RecyclerView.ViewHolder {

        TextView date, desc, cityheader, dateheader;
        ImageView image;
        LinearLayout parent;



        public MyViewHolderRes(View view, int itemView) {
            super(view);


            if(itemView==2){
                date = (TextView) view.findViewById(R.id.scheduleDate);
                desc = (TextView) view.findViewById(R.id.scheduleDesc);
                image = (ImageView) view.findViewById(R.id.scheduleImg);
                parent = (LinearLayout) view.findViewById(R.id.parentLayout);
            }else if(itemView == 1){
                dateheader = (TextView) view.findViewById(R.id.custom_date);
            }else if(itemView == 0){
                cityheader = (TextView) view.findViewById(R.id.custom_city);
            }
        }
    }

    public static class MyListAdapter extends RecyclerView.Adapter<MyViewHolderRes> {

        JSONArray data;
        String currentDate = "";
        String currentCity = "";
        View view = null;

        public MyListAdapter(JSONArray data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            if (data == null) {
                return 0;
            } else {
                return data.length();
            }
        }

        @Override
        public int getItemViewType(int position) {
            try {
                if (data.getJSONObject(position).getString("type").equalsIgnoreCase("city")){
                    Log.e("itemViweType", "returned 0" );
                    return 0;
                }else if (data.getJSONObject(position).getString("type").equalsIgnoreCase("date")){
                    Log.e("itemViweType", "returned 1" );
                    return 1;
                }else if (data.getJSONObject(position).getString("type").equalsIgnoreCase("content")){
                    Log.e("itemViweType", "returned 2" );
                    return 2;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return position;
        }

        @Override
        public MyViewHolderRes onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (viewType == 0){
                view = inflater.inflate(R.layout.custom_schedule_item_city, parent, false);
                Log.e("itemViweType", "returned city" );
            }else if (viewType == 1){
                view = inflater.inflate(R.layout.custom_schedule_item_date, parent, false);
                Log.e("itemViweType", "returned date" );
            }else if(viewType == 2){
                view = inflater.inflate(R.layout.custom_schedule_item, parent, false);
                Log.e("itemViweType", "returned content" );
            }

            MyViewHolderRes holder = new MyViewHolderRes(view,viewType);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderRes holder, final int position) {
//            holder.setIsRecyclable(false);

            try {
                if (data.getJSONObject(position).getString("type").equalsIgnoreCase("city")){
                    holder.cityheader.setText(data.getJSONObject(position).getString("value"));
                }else if(data.getJSONObject(position).getString("type").equalsIgnoreCase("date")){
                    holder.dateheader.setText(data.getJSONObject(position).getString("value"));
                }else if(data.getJSONObject(position).getString("type").equalsIgnoreCase("content")){
//                    holder.image.setImageResource(R.drawable.pending);
                    if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("airport")){
                        holder.image.setImageResource(R.drawable.airport);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("bus")){
                        holder.image.setImageResource(R.drawable.bus);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("Makkah")){
                        holder.image.setImageResource(R.drawable.city);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("hotel")){
                        holder.image.setImageResource(R.drawable.hotel);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("Haram")){
                        holder.image.setImageResource(R.drawable.kaaba);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("restservices")){
                        holder.image.setImageResource(R.drawable.restservices);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("Madina")){
                        holder.image.setImageResource(R.drawable.city);
                    }else if(data.getJSONObject(position).getJSONObject("value").getString("type").equalsIgnoreCase("masjid")){
                        holder.image.setImageResource(R.drawable.masjid);
                    }



                    holder.date.setText(data.getJSONObject(position).getJSONObject("value").getString("date"));
                    holder.desc.setText(data.getJSONObject(position).getJSONObject("value").getString("desc"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            try {
//
//                if (position == 0) {
//
//                    currentCity = data.getJSONObject(position).getString("city");
//                    TextView t = new TextView(holder.parent.getContext());
//                    t.setText(currentCity);
//                    t.setTextSize(20);
//                    t.setGravity(Gravity.LEFT);
//                    t.setBackgroundColor(Color.parseColor("#555555"));
//                    t.setPadding(25, 25, 5, 25);
//                    t.setTextColor(Color.parseColor("#ffffff"));
//                    t.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                    holder.parent.addView(t, 0);
//
//                    currentDate = data.getJSONObject(position).getString("date").split(",")[0];
//                    TextView t1 = new TextView(holder.parent.getContext());
//                    t1.setText(currentDate);
//                    t1.setTextSize(18);
//                    t1.setGravity(Gravity.CENTER);
//                    t1.setBackgroundColor(Color.parseColor("#999999"));
//                    t1.setPadding(5, 5, 5, 5);
//                    t1.setTextColor(Color.parseColor("#ffffff"));
//                    t1.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                    holder.parent.addView(t1, 1);
//
////                    Log.e("pos0", position + "");
//
//                }
//
//                if (position > 0) {
//
//                    if (currentCity.equalsIgnoreCase(data.getJSONObject(position).getString("city"))) {
//                        if (currentDate.equalsIgnoreCase(data.getJSONObject(position).getString("date").split(",")[0])) {
//                        } else {
//                            currentDate = data.getJSONObject(position).getString("date").split(",")[0];
//                            TextView t = new TextView(holder.parent.getContext());
//                            t.setText(currentDate);
//                            t.setTextSize(18);
//                            t.setGravity(Gravity.CENTER);
//                            t.setBackgroundColor(Color.parseColor("#999999"));
//                            t.setPadding(5, 5, 5, 5);
//                            t.setTextColor(Color.parseColor("#ffffff"));
//                            t.setLayoutParams(new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                            holder.parent.addView(t, 0);
//                        }
//                    } else {
//                        if (currentDate.equalsIgnoreCase(data.getJSONObject(position).getString("date").split(",")[0])) {
//                        } else {
//
//                            currentCity = data.getJSONObject(position).getString("city");
//                            TextView t = new TextView(holder.parent.getContext());
//                            t.setText(currentCity);
//                            t.setTextSize(20);
//                            t.setGravity(Gravity.LEFT);
//                            t.setBackgroundColor(Color.parseColor("#555555"));
//                            t.setPadding(25, 25, 5, 25);
//                            t.setTextColor(Color.parseColor("#ffffff"));
//                            t.setLayoutParams(new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                            holder.parent.addView(t, 0);
//
//
//                            currentDate = data.getJSONObject(position).getString("date").split(",")[0];
//                            TextView t1 = new TextView(holder.parent.getContext());
//                            t1.setText(currentDate);
//                            t1.setTextSize(18);
//                            t1.setGravity(Gravity.CENTER);
//                            t1.setBackgroundColor(Color.parseColor("#999999"));
//                            t1.setPadding(5, 5, 5, 5);
//                            t1.setTextColor(Color.parseColor("#ffffff"));
//                            t1.setLayoutParams(new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                            holder.parent.addView(t1, 1);
//                        }
////                        Log.e("pos>1", position + "");
//                    }
//
//
//                }
//
//
//                if (data.getJSONObject(position).getString("type").equalsIgnoreCase("completed")) {
//                    holder.image.setImageResource(R.drawable.checked);
//                } else {
//                    holder.image.setImageResource(R.drawable.pending);
//                }
//                holder.date.setText(data.getJSONObject(position).getString("date"));
//                holder.desc.setText(data.getJSONObject(position).getString("desc"));
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        }
    }
}
