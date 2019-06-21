package com.example.numbergame.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Utility class to update various files in internal storage
 */
public class LevelUpdater {
    private Context context;
    private String fileName;
    private static Scanner sc;
    private static FileWriter fw;
    public static final String NUMBERFILE = "/numberFile";
    public static final String REDBLUEFILE = "/redBlueFile";
    private static final String TEMP = "/temp";

    public LevelUpdater(Context context){
        this.context = context;
        fileName =  context.getFilesDir().toString();
    }

    /**
     * Updates the number of stars received, unlocks the next level, and writes the level settings
     * to SharedPreferences
     * @param levelNumber
     * @param stars
     * @return true if update is successful, false otherwise
     */
    public boolean updateNumberLevelStars(int levelNumber, int stars){
        File file = new File(fileName+NUMBERFILE);
        File temp = new File(fileName+TEMP);
        try {
            fw = new FileWriter(temp);
            sc = new Scanner(file);
            String s;
            int n = sc.nextInt();
            sc.nextLine();
            fw.write(Integer.toString(n));
            fw.write(System.lineSeparator());
            int seekNum = 2 + (levelNumber-1)*4;
            for (int i=0;i<seekNum;i++) {
                s = sc.nextLine();
                fw.write(s);
                fw.write(System.lineSeparator());
            }
            s = sc.nextLine();
            if (Integer.parseInt(s)<stars) fw.write('0'+stars);
            else fw.write(s);
            fw.write(System.lineSeparator()); //line3 numStars
            s = sc.nextLine();
            fw.write(s);
            fw.write(System.lineSeparator()); //line4 config
            if (levelNumber!=n){
                s = sc.nextLine();
                fw.write(s);
                fw.write(System.lineSeparator()); //line1 levelNum
                s = sc.nextLine();
                fw.write('1');
                fw.write(System.lineSeparator()); //line2 unlocked
                s = sc.nextLine();
                fw.write(s);
                fw.write(System.lineSeparator()); //line3 numStars
                s = sc.nextLine();
                fw.write(s);
                fw.write(System.lineSeparator()); //line4 config
                while (sc.hasNextLine()) {
                    fw.write(sc.nextLine());
                    fw.write(System.lineSeparator());
                }
                SharedPreferences preferences = context.getSharedPreferences("NumberGamePreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("levelNumber",levelNumber+1);
                String configArr[] = s.split(" ");
                for (int i=0;i<configArr.length;i++){
                    editor.putInt(Integer.toString(i),Integer.parseInt(configArr[i]));
                }
                editor.commit();
            }
            fw.flush();
            fw.close();
        }
        catch (Exception e) {e.printStackTrace();}
        return temp.renameTo(file);
    }
}

