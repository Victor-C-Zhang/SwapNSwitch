package com.example.numbergame.levelSetup;

import java.util.ArrayList;

public class LevelSet {
    ArrayList<Level> levels;
    public LevelSet(String[] params){
        levels = new ArrayList<>(params.length/2);
        for (int i=0;i<params.length/2;i++){
            levels.add(new Level(Integer.parseInt(params[i*2]),params[i*2+1]));
        }
    }
}
