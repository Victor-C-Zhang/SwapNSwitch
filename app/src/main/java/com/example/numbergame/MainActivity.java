package com.example.numbergame;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static Interpolator smoothInterpolator = new MyInterpolator();
    Button s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s= findViewById(R.id.startButton);
        startRotation();
    }

    public void onStartClick(View v){
        Intent i = new Intent(getBaseContext(), SelectionActivity.class);
        startActivity(i);
    }
    public void onSettingsClick(View v){
//        Intent i = new Intent(getBaseContext(), RedBlueActivity.class);
//        startActivity(i);
//        Intent i = new Intent(getBaseContext(),SettingsActivity.class);
//        startActivity(i);

    }
    public void onTestClick(View v){
        Intent i = new Intent(getBaseContext(),TestActivity.class);
        startActivity(i);
    }

    private static class MyInterpolator extends AccelerateDecelerateInterpolator{
        @Override
        public float getInterpolation(float input) {
            if (input<0.25 || input>=0.875) return 0f;
            if (input>=0.375 && input<0.75) return 1f;
            if (input<0.5) return super.getInterpolation((float)(input-0.25)*8);
            return 1f - super.getInterpolation((float)(input-0.75)*8);
        }
    }
    public void translate(final View b, final View r){
        TranslateAnimation t = new TranslateAnimation(0,r.getLeft()-b.getLeft(),0,r.getTop()-b.getTop());//change this to actual shit
        t.setDuration(4000);
        t.setInterpolator(smoothInterpolator);
        t.setRepeatCount(Animation.INFINITE);
        t.setFillAfter(false);
        b.startAnimation(t);
    }
    public void rotation(final ArrayList<View> rects) {
        View p, q;
        for (int i = 0; i < rects.size(); i++) {
            p = rects.get(i);
            q = rects.get((i + 1) % rects.size());
            translate(p, q);
        }
    }
    public void startRotation(){
        final ArrayList<View> rects = new ArrayList<>();
        rects.add(findViewById(R.id.buttonOne));
        rects.add(findViewById(R.id.buttonTwo));
        rects.add(findViewById(R.id.buttonFive));
        rects.add(findViewById(R.id.buttonFour));

        class MaskTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(Void result){
                rotation(rects);
            }
        }
        new MaskTask().execute();
    }
}
