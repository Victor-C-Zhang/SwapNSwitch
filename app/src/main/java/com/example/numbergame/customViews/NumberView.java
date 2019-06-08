package com.example.numbergame.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.numbergame.NumberActivity;
import com.example.numbergame.R;

public class NumberView extends android.support.v7.widget.AppCompatTextView {

    /**
     * An INTEGER that points to the ID (not the actual view) of a NumberView's ghost
     * Any time the actual ghost is needed, you MUST find the ghost view first
     */
    private int ghost;
    private Paint paint = new Paint();

    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NumberView,
                0, 0);
        try {
            ghost = a.getInt(R.styleable.NumberView_ghost, 0);
        } finally {
            a.recycle();
        }
    }
    //    public RedBlueView(Context context, AttributeSet attrs, int defStyle){
//        super(context, attrs, defStyle);
//
//        // TODO Auto-generated constructor stub
//        setBackgroundResource(R.drawable.blue_circle);
//    }
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

    }

    public int ghost() {
        if (ghost==0) Log.d("ERROR", "Ghost has not been initialized");
        return ghost;
    }
    public void setGhost(int g) {ghost = g;}
}
