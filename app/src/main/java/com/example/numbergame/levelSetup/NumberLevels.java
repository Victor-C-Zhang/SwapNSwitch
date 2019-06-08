package com.example.numbergame.levelSetup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.numbergame.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class NumberLevels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_levels);
        String[] s = {"1" ,
                "1 5 3 7 2 4 8 9 6\n" ,
                "2" ,
                "3 2 1 4 5 6 9 8 7\n" ,
                "3" ,
                "1 2 3 4 5 6 7 8 9\n" ,
                "4" ,
                "1 2 3 4 5 6 7 8 9\n",};
        LevelSet levelSet = new LevelSet(s);
        LevelAdapter levelAdapter = new LevelAdapter(this,levelSet);
        levelAdapter.addAll(levelSet.levels);
        ListView levelList = (ListView) findViewById(R.id.levelList);
        levelList.setAdapter(levelAdapter);
    }

}
