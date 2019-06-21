package com.example.numbergame.levelSetup;

import java.util.ArrayList;
import java.util.Scanner;

public class Level {
    private int levelNumber;
    private int[][] config;
    private int stars;
    private boolean unlocked;
    /**
     * Inits a generic level
     * @param number the level number
     * @param s A string containing all the numbers/colors in the grid, by row major order
     *          e.g.: 1 2 3
     *                4 5 6  ==> "1 2 3 4 5 6 7 8 9\n"
     *                7 8 9
     * @param stars whether the level has been solved
     * @param unlocked if the level is unlocked or not
     */
    public Level(int number, String s, int stars, boolean unlocked){
        levelNumber=number;
        this.stars = stars;
        this.unlocked = unlocked;
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
    public Level(int number, String s){
        this(number,s,0,true);
    }

    /**
     * Simple init for locked levels
     */
    public Level(){
        unlocked = false;
    }
    public int getLevelNumber(){return levelNumber;}
    public int numStars(){
        // error output -1
        if (stars <0 || stars > 3) return -1;
        return stars;
    }
    public boolean isUnlocked(){return unlocked;}
    public int[][] getConfig(){return config;}
}
