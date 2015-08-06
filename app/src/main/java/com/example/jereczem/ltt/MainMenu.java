package com.example.jereczem.ltt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void callTestActivityClick(View view) {
        Intent i = new Intent(this, CallTest.class);
        startActivity(i);
    }
}
