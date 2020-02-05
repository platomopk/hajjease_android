package com.ttb.bcp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Shehreyar on 3/17/2017.
 */
public class tasks {

    public class checkInternet extends AsyncTask<String, Void, Boolean> {
        String TAG= "checkInternet in tasks";
        ProgressDialog pd;
        Activity activity;

        checkInternet(Activity activity){
            this.activity=activity;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(activity);
            pd.setMessage("Verifying Internet Connectivity ..");
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (new functions().isConnected(activity.getApplicationContext())){
                    return true;
                }else{
                    return false;
                }

            }catch (Exception e){
                Log.e(TAG, "doInBackground: "+e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result){
                //new functions().normalDialog(activity,"Success!","Internet Connectivity Found.","ok");
                activity.startActivity(new Intent(activity,login.class));
                activity.finish();
            }else{
                new functions().finishDialog(activity,"Failure!","No internet connectivity found.","ok");
            }
            super.onPostExecute(result);
        }
    }


}
