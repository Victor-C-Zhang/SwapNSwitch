package com.example.numbergame.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.numbergame.R;

public class RedBlueView extends View {

    /**
     * An INTEGER that points to the ID (not the actual view) of a NumberView's ghost
     * Any time the actual ghost is needed, you MUST find the ghost view first
     */
    private int ghost;
    private boolean blue;
    private Paint paint = new Paint();

    public RedBlueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RedBlueView,
                0, 0);

        try {
            blue = a.getBoolean(R.styleable.RedBlueView_blue, false);
            ghost = a.getInt(R.styleable.RedBlueView_ghostcolor, 0);
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

    public boolean isBlue() {return blue;}
    public void setBlue(boolean b) {blue = b;}
    public int ghost() {
        if (ghost==0) Log.d("ERROR", "Ghost has not been initialized");
        return ghost;
    }
    public void setGhost(int g) {ghost = g;}
}
