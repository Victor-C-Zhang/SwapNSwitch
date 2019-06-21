package com.example.numbergame.levelSetup;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import com.example.numbergame.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class NumberLevels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_levels);
        Scanner sc = new Scanner(System.in); //placeholder so the app compiles
        String fileName = this.getFilesDir().toString();
        fileName+="/numberFile";

        Log.d("FILENAME", fileName);

        File file = new File(fileName);
        if (!file.exists()) { //switch to file.exists() to reconfigure numberFile
            try{
                AssetManager am = this.getAssets();
                sc = new Scanner(am.open("initNumberFile.txt"));
                Log.d("FILENAME",Boolean.toString(file.createNewFile()));
                FileWriter initer = new FileWriter(file);
                while (sc.hasNext()) {
                    initer.write(sc.nextLine());
                    initer.write(System.lineSeparator());
                }
                initer.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
        try {
            sc = new Scanner(file);
        }
        catch (FileNotFoundException f) {f.printStackTrace();}

        int n = sc.nextInt();
        Log.d("FILENAME", Integer.toString(n));
        ArrayList<Level> arr = new ArrayList<>(n);
        for (int i=0;i<n;i++){
            int levelnum = sc.nextInt();
            int unlocked = sc.nextInt();
            if (unlocked==0) {
                sc.nextLine();
                sc.nextLine(); //numstars
                sc.nextLine(); //config
                arr.add(new Level());
            }
            else {
                int stars = sc.nextInt();
                sc.nextLine();
                String config = sc.nextLine();
                arr.add(new Level(levelnum,config,stars,true));
            }
        }
        LevelAdapter levelAdapter = new LevelAdapter(this,arr);
//        LevelSet levelSet = new LevelSet(s);
//        LevelAdapter levelAdapter = new LevelAdapter(this,levelSet);
        GridView levelList = (GridView) findViewById(R.id.levelList);
        levelList.setVerticalSpacing(18);
        levelList.setHorizontalSpacing(18);
        levelList.setAdapter(levelAdapter);
    }

}
