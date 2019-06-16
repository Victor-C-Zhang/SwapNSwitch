package com.example.numbergame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
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


import com.example.numbergame.customViews.NumberView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class NumberActivity extends AppCompatActivity {

    private NumberView circles[][] = new NumberView[3][3];
    private long mLastClickTime = 0;
    private long mLastSwipeTime = 0;
    private static final int[][] BUTTON_IDS = {
            {R.id.buttonZero, R.id.buttonOne, R.id.buttonTwo},
            {R.id.buttonThree, R.id.buttonFour, R.id.buttonFive},
            {R.id.buttonSix, R.id.buttonSeven, R.id.buttonEight},
    };
    private static final int[][] GHOST_IDS = {
            {R.id.ghostZero, R.id.ghostOne, R.id.ghostTwo},
            {R.id.ghostThree, R.id.ghostFour, R.id.ghostFive},
            {R.id.ghostSix, R.id.ghostSeven, R.id.ghostEight},
    };
    private ArrayList<NumberView> last;
    private Handler handler = new Handler();
    private boolean canUndo = false;
    private boolean canReset = false;
    static Pair<Integer,Integer> base;
    int width;
    int height;
    int space;
    int levelNumber;
    private ArrayList<Integer> masterlist = new ArrayList<>();
    Canvas arrows;
    Paint arrowpainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        //init set-up from memory
        SharedPreferences preferences = getSharedPreferences("NumberGamePreferences", MODE_PRIVATE);
        levelNumber = preferences.getInt("levelNumber",1);
        for (int i = 0; i<3;i++){
            for (int j=0;j<3;j++){
                circles[i][j] = (NumberView) findViewById(BUTTON_IDS[i][j]);
                circles[i][j].setGhost(GHOST_IDS[i][j]);
                int num = preferences.getInt(Integer.toString(i*3+j),0);
                circles[i][j].setText(Integer.toString(num));
                masterlist.add(num);
                String s = circles[i][j].toString();
                Log.d("oopsie",s);
            }
        }

        //get screen sizes
        Context c = getApplicationContext();
        Log.d("Context", c.toString());
        int w = getScreenWidthInDPs(c);
        int h = getScreenHeightInDPs(c);
        Log.d("size", w+ " " +h);
        arrows = new Canvas();
        arrowpainter = new Paint();
        arrowpainter.setColor(Color.RED);
        arrowpainter.setStyle(Paint.Style.FILL_AND_STROKE);
        arrowpainter.setAntiAlias(true);
        arrowpainter.setStrokeWidth(2);
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
    public void translate(final NumberView b, final NumberView r){
        TranslateAnimation t = new TranslateAnimation(0,r.getLeft()-b.getLeft(),0,r.getTop()-b.getTop());//change this to actual shit
        t.setDuration(300);
        t.setFillAfter(true);
        b.startAnimation(t);
    }

    /**
     * Internal method used to execute the animation
     * Bundles individual translations and updates each view's attributes after the translation
     */
    protected void rotation(final ArrayList<NumberView> rects){
        NumberView p,q;
        for (int i=0;i<rects.size();i++){
            p = rects.get(i);
            q = rects.get((i+1)%rects.size());
            ((NumberView)findViewById(q.ghost())).setText(p.getText().toString());
            translate(p,q);
        }
        final String s = rects.get(rects.size()-1).getText().toString();
        class MaskTask extends AsyncTask<Void, Void, Void>{
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
                for (int i=0;i<rects.size();i++){
                    rects.get(i).setVisibility(View.INVISIBLE);
                    ((NumberView)findViewById(rects.get(i).ghost())).setVisibility(View.VISIBLE);
                    rects.get(i).clearAnimation();
                }
                for (int i=rects.size()-1;i>0;i--){
                    rects.get(i).setText(rects.get(i-1).getText().toString());
                }
                rects.get(0).setText(s);
                for (int i=0;i<rects.size();i++){
                    rects.get(i).setVisibility(View.VISIBLE);
                    ((NumberView)findViewById(rects.get(i).ghost())).setVisibility(View.INVISIBLE);
                }
            }
        }
        new MaskTask().execute();
    }

    ArrayList<NumberView> ans;
    boolean touch[][] = new boolean[3][3];
    Pair<Integer,Integer> start=null, end=null;
    boolean canExecute = true;
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
                break;
            case MotionEvent.ACTION_MOVE: {
                if (!canExecute) break;
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
                        circles[y][x].setBackground(getResources().getDrawable(R.drawable.pressed_circle));
                        touch[y][x] = true;
                        end = new Pair<>(y, x);
                        Log.d("woopsie", x + " " + y);

                    }
                    else if ((end.first==y && Math.abs(end.second-x)==1) ||
                        (end.second==x && Math.abs(end.first-y)==1) ){
                        ans.add(circles[y][x]);

                        //TODO: Fix arrow-painting (when a valid move is made)
                        int endX = base.first+x*100+35, endY = base.second+y*100+35;
                        arrows.drawLine(base.first+end.second*100+35, base.second+end.first*100+35,
                                endX,endY, arrowpainter);
                        int multiplier = 10;
                        int xmult = (x-end.second)*multiplier, ymult = (y-end.first)*multiplier;
                        Point tip = new Point(endX+xmult, endY+ymult);
                        Point side1 = new Point(endX+ymult,endY+xmult);
                        Point side2 = new Point(endX-ymult,endY-xmult);

                        Path path = new Path();
                        path.setFillType(Path.FillType.EVEN_ODD);
                        path.moveTo(tip.x,tip.y);
                        path.lineTo(side1.x,side1.y);
                        path.lineTo(side2.x,side2.y);
                        path.close();

                        arrows.drawPath(path, arrowpainter);

                        end = new Pair<>(y, x);
                        circles[y][x].setBackground(getResources().getDrawable(R.drawable.pressed_circle));
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
                if (ans.size()>3 && (
                        (start.first.equals(end.first) && Math.abs(start.second-end.second)==1) ||
                        (start.second.equals(end.second) && Math.abs(start.first-end.first)==1)
                    ) ) {
                    for (int i=0;i<ans.size();i++) Log.d("uwu", ans.get(i).toString());
                    rotation(ans);
                    ((TextView)findViewById(R.id.moves)).setText(Integer.valueOf((Integer.parseInt(((TextView)findViewById(R.id.moves)).getText().toString())+1)).toString());
                    ViewCompat.setBackgroundTintList(findViewById(R.id.undo), ContextCompat.getColorStateList(this, R.color.colorAccent));
                    ViewCompat.setBackgroundTintList(findViewById(R.id.reset), ContextCompat.getColorStateList(NumberActivity.this, R.color.colorAccent));
                    mLastClickTime = SystemClock.elapsedRealtime();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            canUndo = true;
                            canReset = true;
                            Collections.reverse(ans);
                            last = ans;
                            boolean done = true;
                            for (int i=0;i<3;i++) {
                                for (int j = 0; j < 3; j++) {
                                    Log.d("Circle" + i + " " + j, circles[i][j].getText().toString());
                                    if (Integer.parseInt(circles[i][j].getText().toString())!=i*3+j+1) done = false;
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
                for (NumberView t: ans){
                    t.setBackground(getResources().getDrawable(R.drawable.numbercircle));
                }
                break;

        }
        return true;
    }

    public void displayWin(){
        LayoutInflater lf = (LayoutInflater) NumberActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = lf.inflate(R.layout.win_screen,null);
        ((TextView)layout.findViewById(R.id.winText)).setText(((TextView)findViewById(R.id.moves)).getText());
        double density= NumberActivity.this.getResources().getDisplayMetrics().density;
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
        int x = 1, y = 0;
        base = new Pair<>(135,755);
        end = new Pair<>(0,0);
        int endX = base.first+x*100+35, endY = base.second+y*100+35;
        arrows.drawLine(base.first+end.second*100+35, base.second+end.first*100+35,
                endX,endY, arrowpainter);
        int multiplier = 10;
        int xmult = (x-end.second)*multiplier, ymult = (y-end.first)*multiplier;
        Point tip = new Point(endX+xmult, endY+ymult);
        Point side1 = new Point(endX+ymult,endY+xmult);
        Point side2 = new Point(endX-ymult,endY-xmult);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(tip.x,tip.y);
        path.lineTo(side1.x,side1.y);
        path.lineTo(side2.x,side2.y);
        path.close();

        arrows.drawPath(path, arrowpainter);
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
        for (int i=0;i<3;i++)
            for (int j=0;j<3;j++) circles[i][j].setText(String.valueOf(masterlist.get(i*3+j)));
        ((TextView) findViewById(R.id.moves)).setText("0");
        ViewCompat.setBackgroundTintList(findViewById(R.id.reset), ContextCompat.getColorStateList(this, R.color.colorGray));
        canReset = false;
        canUndo = false;
        ViewCompat.setBackgroundTintList(findViewById(R.id.undo), ContextCompat.getColorStateList(this, R.color.colorGray));
    }

    public void openDialog() {
        String s = String.format("You completed the puzzle in %s steps. The most efficient solution takes %s steps... Can you find it?",
                ((TextView)findViewById(R.id.moves)).getText().toString(), "NUMSTEPS");
        Log.d("Context", this.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!")
                .setMessage(s)
                .setPositiveButton("THANK YOU NEXT ! !", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newBoard();
                    }
                })
                .setNegativeButton("RETURN", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NumberActivity.this.finish();
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
        //Randomizer
        masterlist = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i<3;i++){
            for (int j=0;j<3;j++){
                circles[i][j] = (NumberView) findViewById(BUTTON_IDS[i][j]);
                circles[i][j].setGhost(GHOST_IDS[i][j]);
                list.add(i*3+j+1);
                String s = circles[i][j].toString();
                Log.d("oopsie",s);
            }
        }
        Random rn = new Random();
        for (int i = 0; i<3;i++){
            for (int j=0;j<3;j++){
                int n = rn.nextInt(9-i*3-j);
                circles[i][j].setText(String.valueOf(list.get(n)));
                masterlist.add(list.get(n));
                list.remove(n);
            }
        }
    }
//    for the lolz
//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }
}
