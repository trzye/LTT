package com.example.jereczem.ltt;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jereczem.ltt.networking.HttpResponse;
import com.example.jereczem.ltt.networking.HttpResponseReceiver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;


public class CallTestActivity extends AppCompatActivity {

    TextView textView;
    EditText textView3;
    Integer num;
    Button button;
    int n;
    AsyncLoad asyncLoad;

    MotionEvent me[];
    boolean isme;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        if(n!=0)
            asyncLoad.cancelLoad();
        super.onTitleChanged(title, color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        num =0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_test);
        textView = (TextView) findViewById(R.id.textView);
        textView3 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button2);
        n=0;
        num = Integer.parseInt(textView3.getText().toString());
        me = new MotionEvent[2];
    isme = false;



        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                asyncLoad = new AsyncLoad(context);
                asyncLoad.loadInBackground();

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void turnDataON(View view) throws ExecutionException, InterruptedException, ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
    }

    public void copyToClipboard(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", textView.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Results copied", Toast.LENGTH_SHORT).show();
    }

    private class CallRecord {
        private Boolean isTestPassed;
        private float time;

        public CallRecord(Boolean isTestPassed, float time) {
            this.isTestPassed = isTestPassed;
            this.time = time;
        }

        @Override
        public String toString() {

            if (isTestPassed) {
                return "PASS\t" + "TIME: " + String.valueOf(time);
            }
            return "FAIL\t";
        }
    }

    private class CallThread implements Runnable{
        private Context context;
        private TextView textView;
        private int limit;

        public CallThread(Context context, TextView textView, int limit){
            this.context = context;
            this.textView = textView;
            this.limit = limit;
        }

        private void doInBackground(Context context, TextView textView, int limit) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            n++;
            setMobileDataEnabled(context, false);
            setMobileDataEnabled(context, true);
            long startTime = System.currentTimeMillis();
            HttpResponse response;
            float elapsedTime;
            do {
                response = new HttpResponseReceiver("http://google.com/").receive();
                long endTime = System.currentTimeMillis();
                elapsedTime = (float) (endTime - startTime) / 1000;
            } while (elapsedTime < limit && !response.getCode().equals(200));
            if (response.getCode() == 200) {
                Log.d("LTTLOG", new CallRecord(true, elapsedTime).toString());
                textView.append("\n"+n+":\t" + new CallRecord(true, elapsedTime).toString());
            } else {
                Log.d("LTTLOG", new CallRecord(false, elapsedTime).toString());
                textView.append("\n"+n+":\t" + new CallRecord(false, elapsedTime).toString());
            }
            Log.d("LTTLOG", "XD");
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

        @Override
        public void run() {
            try {
                doInBackground(context, textView, limit);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private class AsyncLoad extends AsyncTaskLoader {
        public AsyncLoad(Context context) {
            super(context);
        }

        @Override
        public Object loadInBackground() {
            EditText textView2 = (EditText) findViewById(R.id.editText);
            CallThread callThread =
                    new CallThread(this.getContext(), textView, Integer.parseInt(textView2.getText().toString()));
            Thread t = new Thread(callThread);
            t.run();
            return null;
        }

        @Override
        protected boolean onCancelLoad() {
            button.performClick();
            return super.onCancelLoad();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
