package com.example.numbergame.customViews;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.numbergame.R;
import com.example.numbergame.RedBlueActivity;
import com.example.numbergame.RedBlueGoalFragment;


public class CustomTextRedBlueView extends android.support.v7.widget.AppCompatTextView {
    private Context context;
    private RedBlueGoalFragment goalFrag;
    public CustomTextRedBlueView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomTextRedBlueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setGoal(RedBlueGoalFragment goalFrag){
        this.goalFrag = goalFrag;
    }
    public boolean onTouchEvent(MotionEvent e){
        if (!goalFrag.isInited()) {
            ((RedBlueActivity) context).initGoal();

        }
        switch (e.getActionMasked()){
            case MotionEvent.ACTION_DOWN: {
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.show(goalFrag);
                ft.commit();
                ((AppCompatActivity)context).findViewById(R.id.dimmer).setAlpha(0.5f);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.hide(goalFrag);
                ft.commit();
                ((AppCompatActivity)context).findViewById(R.id.dimmer).setAlpha(0.0f);
                break;
            }
        }
        return true;
    }
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
