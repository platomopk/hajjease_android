package com.ttb.bcp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Shehreyar on 3/17/2017.
 */
public class functions {

    //public static final String baseIP="http://aslkdnfas.com/";

    public static final String baseIP="http://pdttests.net/hajjease/";

//    public static final String baseIP="http://172.17.2.180/hajjease/";
//    public static final String baseIP="http://192.168.88.203/hajjease/";



    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    public void appendLog(String text)
    {
        File logFile = new File("sdcard/log.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void exitDialog(final Activity activity, String title, String message, String btnText){
        AlertDialog.Builder adb= new AlertDialog.Builder(activity);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
                System.exit(1);
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    void finishDialog(final Activity activity, String title, String message, String btnText){
        AlertDialog.Builder adb= new AlertDialog.Builder(activity);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    void normalDialog(Activity activity,String title, String message, String btnText){
        AlertDialog.Builder adb= new AlertDialog.Builder(activity);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    public boolean isConnected(Context context) throws InterruptedException, IOException {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;





//        Runtime runtime = Runtime.getRuntime();
//        Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//        int exitValue = ipProcess.waitFor();
//        return (exitValue == 0);
    }

}
