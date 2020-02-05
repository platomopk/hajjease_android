package com.ttb.bcp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Shehreyar on 10/16/2017.
 */
public class NotificationReciever extends BroadcastReceiver {

    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        String action = intent.getAction();

        if ("com.ttb.bcp.YES".equals(action)){
            CancelNotification(context,Integer.valueOf(intent.getStringExtra("id")));
            //Toast.makeText(context, "carpool id "+intent.getStringExtra("carpoolid"), Toast.LENGTH_SHORT).show();

            String userid = context.getApplicationContext().getSharedPreferences("local_db",Context.MODE_PRIVATE).getString("userId","0");

            new registertobroadcastTask().execute("userId="+userid+"&carpoolid="+intent.getStringExtra("carpoolid"));
        }

        if ("com.ttb.bcp.NO".equals(action)){
            CancelNotification(context,Integer.valueOf(intent.getStringExtra("id")));
            //Toast.makeText(context, "NO", Toast.LENGTH_SHORT).show();
        }
    }

    public static void CancelNotification(Context ctx, int notifyId) {
        String  s = Context.NOTIFICATION_SERVICE;
        NotificationManager mNM = (NotificationManager) ctx.getSystemService(s);
        mNM.cancel(notifyId);
    }


    public class registertobroadcastTask extends AsyncTask<String, Void, Boolean> {
        String TAG= "logintask";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpHandler handler = new HttpHandler();
                String jsonString = handler.makeServiceCall(new functions().baseIP+"registertobroadcast.php?"+strings[0]);



                return true;

                //JSONArray jArr = new JSONArray(jsonString);

//                if (jArr.length() > 0) {
//
//                    JSONObject obj = jArr.getJSONObject(0);
//
//                    return true;
//
//                } else {
//                    return false;
//                }

            }catch (Exception e){
                Log.e(TAG, "doInBackground: "+e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result){
                Toast.makeText(ctx, "Carpool joined successfully", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
}
