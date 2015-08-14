package com.example.jereczem.ltt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jereczem.ltt.networking.HttpConnection;
import com.example.jereczem.ltt.networking.HttpResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class CallTest extends ActionBarActivity {
    AsyncStartTestTask asyncStartTestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_test);
        asyncStartTestTask = new AsyncStartTestTask();
    }

    @Override
    public void onBackPressed() {
        if(!asyncStartTestTask.getStatus().equals(AsyncTask.Status.RUNNING))
            super.onBackPressed();
    }

    public void startTests(View view) {
        if(!asyncStartTestTask.getStatus().equals(AsyncTask.Status.RUNNING)){
            EditText secondsEditText = (EditText)findViewById(R.id.secondsEditText);
            EditText timesEditText = (EditText)findViewById(R.id.timesEditText);
            Integer seconds = Integer.parseInt(secondsEditText.getText().toString());
            Integer times = Integer.parseInt(timesEditText.getText().toString());
            TextView resultsTextView = (TextView)findViewById(R.id.resutlsTextView);
            Button startButton = (Button)findViewById(R.id.startTestButton);
            asyncStartTestTask = new AsyncStartTestTask();
            startButton.setText("Test in progress... press to cancel");
            resultsTextView.setText("");
            asyncStartTestTask.execute(seconds, times, this, resultsTextView, startButton);
        }else{
            Button startButton = (Button)findViewById(R.id.startTestButton);
            asyncStartTestTask.cancel(true);
            startButton.setText("Tests ended, press to restart :)");
        }
    }

    public void saveToClipboard(View view) {
        TextView resultsTextView = (TextView)findViewById(R.id.resutlsTextView);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", resultsTextView.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Results copied", Toast.LENGTH_SHORT).show();
    }

    private class AsyncStartTestTask extends AsyncTask<Object, Float, Void>{

        Integer seconds;
        Integer times;
        Context context;
        TextView resultsTextView;
        Button startButton;

        @Override
        protected void onPostExecute(Void aVoid) {
            startButton.setText("Tests ended, press to restart :)");
            super.onPostExecute(aVoid);
        }

        private boolean isAPNEnabled() {
            boolean mobileDataEnabled = false; // Assume disabled
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                method.setAccessible(true); // Make the method callable
                // get the setting for "mobile data"
                mobileDataEnabled = (Boolean)method.invoke(cm);
            } catch (Exception e) {
                // Some problem accessible private API
                // TODO do whatever error handling you want here
            }
            return  mobileDataEnabled;
        }

        @Override
        protected Void doInBackground(Object... params) {
            seconds = (Integer)params[0];
            times = (Integer)params[1];
            context = (Context)params[2];
            resultsTextView = (TextView)params[3];
            startButton = (Button)params[4];
            for(int j=0; j<times; j++){
                if(isCancelled()) break;
                try {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            Log.d("HASLOG", "wsz");
                            boolean mobileDataEnabled; // Assume disabled
                            do{
                                mobileDataEnabled = isAPNEnabled();
                                Log.d("HASLOG", "1" + mobileDataEnabled);
                            }while(mobileDataEnabled);
                            do{
                                mobileDataEnabled = isAPNEnabled();
                                Log.d("HASLOG", "2" + mobileDataEnabled);
                            }while(!mobileDataEnabled);
                        }else{
                            setMobileDataEnabled(context, false);
                            setMobileDataEnabled(context, true);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.d("HASLOG", e.toString());
                    }
                HttpResponse httpResponse;
                long startTime = System.currentTimeMillis();
                long endTime;
                float elapsedTime;
                do{
                    try {
                        httpResponse = HttpConnection.get("http://google.com/");
                    } catch (IOException e) {
                        e.printStackTrace();
                        httpResponse = new HttpResponse(500, "error");
                    }
                    endTime = System.currentTimeMillis();
                    elapsedTime = (float)(endTime - startTime)/1000;
                }while(httpResponse.getCode() != 200 && (elapsedTime < seconds));
                publishProgress((float)j, (float)httpResponse.getCode(), elapsedTime);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            StringBuilder stringBuilder = new StringBuilder(((int)(float)values[0] + 1) + ":\t");
            if(values[1] == 200){
                stringBuilder.append("PASS in " + (float)values[2] + " seconds");
            }else{
                stringBuilder.append("FAIL");
            }
            resultsTextView.setText(stringBuilder.toString() + "\n" + resultsTextView.getText());
            super.onProgressUpdate(values);
        }

        private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        }
    }
}
