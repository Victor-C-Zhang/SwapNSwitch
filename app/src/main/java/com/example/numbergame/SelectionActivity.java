package com.example.numbergame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.numbergame.levelSetup.NumberLevels;
import com.example.numbergame.levelSetup.RedBlueLevels;

public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
    }
    public void onNumberClick(View v){
        Intent i = new Intent(getBaseContext(), NumberLevels.class);
        startActivity(i);
    }
    public void onRedBlueClick(View v){
        Intent i = new Intent(getBaseContext(), RedBlueLevels.class);
        startActivity(i);
    }
}
