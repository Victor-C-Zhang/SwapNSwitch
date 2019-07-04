package com.example.numbergame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.example.numbergame.customViews.ArrowView;
import com.example.numbergame.customViews.CustomTextRedBlueView;
import com.example.numbergame.customViews.RedBlueView;
import com.example.numbergame.util.LevelUpdater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class RedBlueActivity extends AppCompatActivity implements RedBlueGoalFragment.OnFragmentInteractionListener {

    //TODO: think about "program flow"
    /**start screen with splash art
    * start, tutorial, settings, credits
    * when hit "play", bring to the level selector screen, gray levels that you can't play
    * don't record too much progress, but...
    * create star system: 1,2,3
    *
    * make balls smaller
    * display optimal number of moves, display number of moves to finish, button to show finishing position
     */
    //TODO: look more at the puzzle as an entertaining game
    //TODO: share a google doc with prof yu

    private RedBlueView circles[][] = new RedBlueView[3][3];
    private long mLastClickTime = 0;
    private long mLastSwipeTime = 0;
    private static final int[][] BUTTON_IDS = {
            {R.id.colorZero, R.id.colorOne, R.id.colorTwo},
            {R.id.colorThree, R.id.colorFour, R.id.colorFive},
            {R.id.colorSix, R.id.colorSeven, R.id.colorEight},
    };
    private static final int[][] GHOST_IDS = {
            {R.id.ghostZero, R.id.ghostOne, R.id.ghostTwo},
            {R.id.ghostThree, R.id.ghostFour, R.id.ghostFive},
            {R.id.ghostSix, R.id.ghostSeven, R.id.ghostEight},
    };

    private ArrayList<RedBlueView> last;
    private Handler handler = new Handler();
    private RedBlueGoalFragment goalFrag;
    private boolean canUndo = false;
    private boolean canReset = false;
    static Pair<Integer,Integer> base;
    int width;
    int height;
    int space;
    int levelNumber;
    int minMoves;
    private ArrayList<Boolean> masterlist = new ArrayList<>();
    private ArrayList<Boolean> goalList = new ArrayList<>();
    private ArrayList<ArrowView> arrows = new ArrayList<>();

    /**
     * blue = true, red = false;
     * @param savedInstanceState yee
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_blue);

        SharedPreferences preferences = getSharedPreferences("NumberGamePreferences", MODE_PRIVATE);
        levelNumber = preferences.getInt("levelNumber",1);
        int startConfig[] = new int[9];
        for (int i = 0; i<3;i++){
            for (int j=0;j<3;j++){
                circles[i][j] = (RedBlueView) findViewById(BUTTON_IDS[i][j]);
                circles[i][j].setGhost(GHOST_IDS[i][j]);
                int num = preferences.getInt(Integer.toString(i*3+j),0);
                if (num==1) {
                    circles[i][j].setBackground(getResources().getDrawable(R.drawable.blue_circle));
                    circles[i][j].setBlue(true);
                    masterlist.add(true);
                }
                else {
                    circles[i][j].setBackground(getResources().getDrawable(R.drawable.red_circle));
                    circles[i][j].setBlue(false);
                    masterlist.add(false);
                }
                startConfig[i*3+j] = num;
                String s = circles[i][j].toString();
                Log.d("oopsie",s);
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        goalFrag = new RedBlueGoalFragment();
        ft.replace(R.id.placehholderFrame, goalFrag);
        ft.commit();
        ft = getSupportFragmentManager().beginTransaction();
        ft.hide(goalFrag);
        ft.commit();
        ((CustomTextRedBlueView)findViewById(R.id.goal)).setGoal(goalFrag);
        //init arrows, 16 is enough to draw every possible path on a 4x4 grid
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        for (int i=0;i<16;i++){
            ArrowView temp = new ArrowView(this);
            temp.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
            arrows.add(temp);
            constraintLayout.addView(temp);
        }

        //temporary
        boolean arr[] = {
                true,true,true,
                true,true,false,
                false,false,false
        };
        for (int i=0;i<9;i++) goalList.add(arr[i]);

//        minMoves = ILPSolver.squareOptimalMoves(startConfig,
//                new int[]{1,2,3,4,5,6,7,8,9,});
        minMoves = 3;

    }

    //Methods to get screen width/height in dp
    public static int getScreenWidthInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return Math.round(dm.widthPixels / dm.density);
    }
    public static int getScreenHeightInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return Math.round(dm.heightPixels / dm.density);
    }

    /**
     * Internal method to translate one view from one location to another
     * NOTE: the translation is filled afterwards, i.e. the view will translate and stay there
     * @param b The initial view
     * @param r The final view
     */
    public void translate(final View b, final View r){
        TranslateAnimation t = new TranslateAnimation(0,r.getLeft()-b.getLeft(),0,r.getTop()-b.getTop());
        t.setDuration(300);
        t.setFillAfter(true);
        b.startAnimation(t);
    }

    /**
     * Internal method used to execute the animation
     * Bundles individual translations and updates each view's attributes after the translation
     */
    protected void rotation(final ArrayList<RedBlueView> rects){
        RedBlueView p,q;
        final Drawable d = rects.get(rects.size()-1).getBackground();
        final boolean b = rects.get(rects.size()-1).isBlue();
        for (int i=0;i<rects.size();i++){
            p = rects.get(i);
            q = rects.get((i+1)%rects.size());
            ((RedBlueView)findViewById(q.ghost())).setBackground(p.getBackground());
            ((RedBlueView)findViewById(q.ghost())).setBlue(p.isBlue());
            translate(p,q);
        }
        for (int i=0;i<rects.size();i++) Log.d("owo",rects.get(i).toString());

        class MaskTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(325);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(Void result){
                for (int i=0;i<rects.size();i++) Log.d("owo",rects.get(i).toString());
                for (int i=0;i<rects.size();i++){
                    rects.get(i).setVisibility(View.INVISIBLE);
                    ((RedBlueView)findViewById(rects.get(i).ghost())).setVisibility(View.VISIBLE);
                    rects.get(i).clearAnimation();
                }
                for (int i=rects.size()-1;i>0;i--){
                    rects.get(i).setBackground(rects.get(i-1).getBackground());
                    rects.get(i).setBlue(rects.get(i-1).isBlue());
                    Log.d("owo",i + " " + rects.get(i).toString() + " " + rects.get(i).isBlue() + " " + rects.get(i).getBackground());
                }
                rects.get(0).setBackground(d);
                rects.get(0).setBlue(b);
                for (int i=0;i<rects.size();i++){
                    rects.get(i).setVisibility(View.VISIBLE);
                    ((RedBlueView)findViewById(rects.get(i).ghost())).setVisibility(View.INVISIBLE);
                }
            }
        }
        new MaskTask().execute();
    }

    ArrayList<RedBlueView> ans;
    boolean touch[][] = new boolean[3][3];
    Pair<Integer,Integer> start=null, end=null;
    boolean canExecute = true;
    int arrowNum = 0;
    @Override
    public boolean onTouchEvent(MotionEvent e){
        int maskedAction = e.getActionMasked();
        base = new Pair<>((int)circles[0][0].getX(),(int)circles[0][0].getY());
        Log.d("uwu", base.first.toString() + " " + base.second.toString());
        width = circles[0][0].getWidth();
        height = circles[0][0].getHeight();
        space = (int) (width*3/7.0);
        switch(maskedAction){
            case MotionEvent.ACTION_DOWN:
                if (SystemClock.elapsedRealtime()-mLastSwipeTime<500) {
                    canExecute = false;
                    break;
                }
                canExecute = true;
                Log.d("oopsie","down");
                ans = new ArrayList<>();
                for (int i=0;i<3;i++){
                    for (int j=0;j<3;j++) touch[i][j] = false;
                }
                start = null;
                arrows.get(arrowNum).init(e.getX(),e.getY());
                break;
            case MotionEvent.ACTION_MOVE: {
                if (!canExecute) break;
                arrows.get(arrowNum).updateCoords(e.getX(),e.getY());
                double dx =  e.getX(), dy = e.getY();
                int x,y;
                Log.d("woopsie",dx + " " + dy);
                x = (int)Math.floor((dx-base.first)/(width+space));
                y = (int)Math.floor((dy-base.second)/(height+space));
                if (x<0 || x>2 || y<0 || y>2) break;
                if ((int)Math.floor((dx-base.first+space)/(width+space))!=x ||
                        (int)Math.floor((dy-base.second)/(height+space))!=y) break;
                if (!touch[y][x]) {
                    if (start==null) {
                        start = new Pair<>(y,x);
                        ans.add(circles[y][x]);
                        if (circles[y][x].isBlue())
                            circles[y][x].setBackground(getResources().getDrawable(R.drawable.pressed_blue_circle));
                        else circles[y][x].setBackground(getResources().getDrawable(R.drawable.pressed_red_circle));
                        touch[y][x] = true;
                        end = new Pair<>(y, x);
                        arrows.get(arrowNum).init((float)(base.first+width/2.0+x*(width+space)),(float)(base.second+height/2.0+y*(height+space)));
                        Log.d("woopsie", x + " " + y);

                    }
                    else if ((end.first==y && Math.abs(end.second-x)==1) ||
                            (end.second==x && Math.abs(end.first-y)==1) ){
                        ans.add(circles[y][x]);
                        float xCoord = (float)(base.first+width/2.0+x*(width+space)), yCoord =(float)(base.second+height/2.0+y*(height+space));
                        arrows.get(arrowNum).updateCoords(xCoord,yCoord);
                        arrowNum++;
                        arrows.get(arrowNum).init(xCoord,yCoord);
                        arrows.get(arrowNum).updateCoords(e.getX(),e.getY());
                        end = new Pair<>(y, x);
                        if (circles[y][x].isBlue())
                            circles[y][x].setBackground(getResources().getDrawable(R.drawable.pressed_blue_circle));
                        else circles[y][x].setBackground(getResources().getDrawable(R.drawable.pressed_red_circle));
                        touch[y][x] = true;
                        Log.d("woopsie", x + " " + y);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!canExecute) break;
                Log.d("uwu","up");
                mLastSwipeTime = SystemClock.elapsedRealtime();
                for (int i=0;i<ans.size();i++){

                    if (ans.get(i).isBlue())
                        ans.get(i).setBackground(getResources().getDrawable(R.drawable.blue_circle));
                    else ans.get(i).setBackground(getResources().getDrawable(R.drawable.red_circle));
                }
                if (ans.size()>3 && (
                        (start.first.equals(end.first) && Math.abs(start.second-end.second)==1) ||
                                (start.second.equals(end.second) && Math.abs(start.first-end.first)==1)
                ) ) {
                    //start rotation sequence
                    arrows.get(arrowNum).updateCoords(arrows.get(0).getStartX(),arrows.get(0).getStartY());

                    for (int i=0;i<ans.size();i++) Log.d("uwu", ans.get(i).toString());
                    rotation(ans);
                    ((TextView)findViewById(R.id.moves)).setText(Integer.valueOf((Integer.parseInt(((TextView)findViewById(R.id.moves)).getText().toString())+1)).toString());
                    ViewCompat.setBackgroundTintList(findViewById(R.id.undo), ContextCompat.getColorStateList(this, R.color.colorAccent));
                    ViewCompat.setBackgroundTintList(findViewById(R.id.reset), ContextCompat.getColorStateList(RedBlueActivity.this, R.color.colorAccent));
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            for (int i=0;i<=arrowNum;i++) arrows.get(i).reset();
                            arrowNum = 0;
                            canUndo = true;
                            canReset = true;
                            Collections.reverse(ans);
                            last = ans;
                            boolean done = true;
                            for (int i=0;i<3;i++) {
                                for (int j = 0; j < 3; j++) {
                                    Log.d("Circle" + i + " " + j, String.valueOf(circles[i][j].isBlue()));
                                    if (goalList.get(i*3+j)!=circles[i][j].isBlue()) done = false;
                                    if (!done) break;
                                }
                                if (!done) break;
                            }
                            if (done) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("win", "done");
                                        openDialog();
                                        //displayWin();
                                    }
                                });
                            }

                        }
                    }).start();
                }
                else {
                    for (int i=0;i<=arrowNum;i++) arrows.get(i).reset();
                    arrowNum = 0;
                }
                break;

        }
        return true;
    }

    public void displayWin(){
        LayoutInflater lf = (LayoutInflater) RedBlueActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = lf.inflate(R.layout.win_screen,null);
        ((TextView)layout.findViewById(R.id.winText)).setText(((TextView)findViewById(R.id.moves)).getText());
        double density= RedBlueActivity.this.getResources().getDisplayMetrics().density;
        final PopupWindow pw = new PopupWindow(layout, (int)density*250, (int)density*150, true);
        ((Button) layout.findViewById(R.id.dismissWin)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        pw.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });
        pw.setOutsideTouchable(true);
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
    public void onTestClick(View v){
        //displayWin();
        //openDialog();
    }
    public void onUndoClick(View v){
        if (!canUndo) return;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 360) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        rotation(last);
        canUndo = false;
        ((TextView)findViewById(R.id.moves)).setText(Integer.valueOf((Integer.parseInt(((TextView)findViewById(R.id.moves)).getText().toString())-1)).toString());
        ViewCompat.setBackgroundTintList(findViewById(R.id.undo), ContextCompat.getColorStateList(this, R.color.colorGray));
        if (((TextView)findViewById(R.id.moves)).getText().toString().equals("0")) {
            canReset = false;
            ViewCompat.setBackgroundTintList(findViewById(R.id.reset), ContextCompat.getColorStateList(this, R.color.colorGray));

        }
    }
    public void onResetClick(View v){
        if (!canReset) return;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 360) {
            return;
        }
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (masterlist.get(i*3+j)) {
                    circles[i][j].setBackground(getResources().getDrawable(R.drawable.blue_circle));
                    circles[i][j].setBlue(true);
                }
                else {
                    circles[i][j].setBackground(getResources().getDrawable(R.drawable.red_circle));
                    circles[i][j].setBlue(false);
                }
            }
        }
        ((TextView) findViewById(R.id.moves)).setText("0");
        ViewCompat.setBackgroundTintList(findViewById(R.id.reset), ContextCompat.getColorStateList(this, R.color.colorGray));
        canReset = false;
        canUndo = false;
        ViewCompat.setBackgroundTintList(findViewById(R.id.undo), ContextCompat.getColorStateList(this, R.color.colorGray));
    }

    public void openDialog() {
        String s;
        int stars;
        int movesTaken = Integer.valueOf(((TextView)findViewById(R.id.moves)).getText().toString());
        if (movesTaken>minMoves) {
            s = String.format(
                    "You completed the puzzle in %d steps. The most efficient solution takes %d steps... Can you find it?",
                    movesTaken, minMoves);
            int twoStarThreshold = Math.min(minMoves*2,minMoves+4);
            if (movesTaken>twoStarThreshold) stars = 1;
            else stars = 2;
        }
        else {
            s= String.format(
                    "Wow! You found the most efficient solution for level %d, taking only %d steps!", levelNumber, movesTaken);
            stars = 3;
        }
        LevelUpdater lu = new LevelUpdater(getBaseContext());
        lu.updateRedBlueLevelStars(levelNumber,stars);

        Log.d("Context", this.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!")
                .setMessage(s)
                .setPositiveButton("THANK YOU NEXT ! !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newBoard();
                    }
                })
                .setNegativeButton("RETURN TO LEVELS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RedBlueActivity.this.finish();
                    }
                })
                .setCancelable(false)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void newBoard(){
        canReset = false;
        ViewCompat.setBackgroundTintList(findViewById(R.id.reset), ContextCompat.getColorStateList(this, R.color.colorGray));
        canUndo = false;
        ViewCompat.setBackgroundTintList(findViewById(R.id.undo), ContextCompat.getColorStateList(this, R.color.colorGray));
        ((TextView) findViewById(R.id.moves)).setText("0");

        masterlist = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("RedBlueGamePreferences", MODE_PRIVATE);
        levelNumber = preferences.getInt("levelNumber",1);
        int startConfig[] = new int[9];
        for (int i = 0; i<3;i++){
            for (int j=0;j<3;j++){
                circles[i][j] = (RedBlueView) findViewById(BUTTON_IDS[i][j]);
                circles[i][j].setGhost(GHOST_IDS[i][j]);
                int num = preferences.getInt(Integer.toString(i*3+j),0);
                if (num==1) {
                    circles[i][j].setBackground(getResources().getDrawable(R.drawable.blue_circle));
                    circles[i][j].setBlue(true);
                    masterlist.add(true);
                }
                else {
                    circles[i][j].setBackground(getResources().getDrawable(R.drawable.red_circle));
                    circles[i][j].setBlue(false);
                    masterlist.add(false);
                }
                startConfig[i*3+j] = num;
                String s = circles[i][j].toString();
                Log.d("oopsie",s);
            }
        }
        minMoves = 2;
    }

    public void onInitGoal(ArrayList<Boolean> goalList){
        if (goalFrag != null) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (!goalList.get(i*3+j)) goalFrag.viewIDs[i][j].setBackground(getResources().getDrawable(R.drawable.red_circle));
            goalFrag.init();
        }
    }
    public void initGoal(){
        onInitGoal(goalList);
    }
//    for the lolz
//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }
}
