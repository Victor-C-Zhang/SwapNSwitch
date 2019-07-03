package com.example.numbergame.customViews;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.numbergame.GoalFragment;
import com.example.numbergame.NumberActivity;
import com.example.numbergame.R;


public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    private Context context;
    private GoalFragment goalFrag;
    public CustomTextView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setGoal(GoalFragment goalFrag){
        this.goalFrag = goalFrag;
    }
    public boolean onTouchEvent(MotionEvent e){
        if (!goalFrag.isInited()) {
            ((NumberActivity) context).initGoal();

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
