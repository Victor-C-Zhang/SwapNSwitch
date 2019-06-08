package com.example.numbergame.levelSetup;

import java.util.ArrayList;
import java.util.Scanner;

public class Level {
    private int levelNumber;
    private int[][] config;
    private boolean solved;
    private boolean unlocked;

    /**
     * Inits a generic level with solved=false
     * @param s A string containing all the numbers/colors in the grid, by row major order
     *          e.g.: 1 2 3
     *                4 5 6  ==> "1 2 3 4 5 6 7 8 9\n"
     *                7 8 9
     */
    public Level(int number, String s){
        levelNumber=number;
        solved = false;
        unlocked = true;
        Scanner sc = new Scanner(s);
        ArrayList<Integer> arr = new ArrayList<>();
        while (sc.hasNextInt()) arr.add(sc.nextInt());
        int dim = 1;
        while (dim*dim!=arr.size()) dim++;
        config = new int[dim][dim];
        for (int i=0;i<dim;i++)
            for (int j=0;j<dim;j++)
                config[i][j]=arr.get(i*dim+j);
    }
    public int getLevelNumber(){return levelNumber;}
    public boolean isSolved(){return solved;}
    public boolean isUnlocked(){return unlocked;}
    public int[][] getConfig(){return config;}

}
