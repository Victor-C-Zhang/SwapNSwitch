package com.example.numbergame;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;

import com.example.numbergame.customViews.ArrowView;

public class TestActivity extends AppCompatActivity {

    ArrowView arrowView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        arrowView = new ArrowView(this);
        arrowView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT));

        constraintLayout.addView(arrowView);
    }
    public void onTestClick(View v){
        arrowView.init(500,0);
        arrowView.updateCoords(500,500);
    }
    public void onSecondTestClick(View v){
        arrowView.init(500,500);
        arrowView.updateCoords(500,1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        int maskedAction = e.getActionMasked();
        switch (maskedAction){
            case MotionEvent.ACTION_DOWN:
                arrowView.init(e.getX(),e.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                arrowView.updateCoords(e.getX(),e.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                arrowView.reset();
                arrowView.postInvalidate();
                break;
        }
        return true;
    }
}
