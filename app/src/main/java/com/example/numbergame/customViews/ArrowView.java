package com.example.numbergame.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

import com.example.numbergame.R;

/**
 * Utility view to draw fixed-width arrows
 */
public class ArrowView extends View {
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float scaleFactor;
    private boolean blank;

    private Paint arrowPaint;
    private Path arrowPath;
    private final int color = R.color.arrow_gray; //Change this to set colors

    public ArrowView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        scaleFactor = dm.density;
        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(getResources().getColor(color)); //Change this to set colors
        arrowPath = new Path();
        init(0,0);
    }
    public ArrowView(Context context){
        super(context);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        scaleFactor = dm.density;
        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(getResources().getColor(color));
        arrowPath = new Path();
        init(0,0);
    }

    /**
     * Start drawing the arrow at a certain position
     * @param startX Starting x-position in px
     * @param startY Starting y-position in px
     * @throws NullPointerException
     */
    public void init(float startX, float startY) throws NullPointerException {
        if (scaleFactor==0) throw new NullPointerException("Scale factor has not been initialized. Invoke method ArrowView.setScaleFactor()");
        this.startX = startX;
        this.startY = startY;
        endX = startX+1; //just so that theta doesn't bug out
        endY = startY+1;
        blank = true;
    }
    public float getStartX() {return startX;}
    public float getStartY() {return startY;}
    public float getEndX() {return endX;}
    public float getEndY() {return endY;}

    /**
     * Redraws an arrow based on new end coordinates (starting coordinates remain unchanged)
     * @param X Ending x-position in px
     * @param Y Ending y-position in px
     */
    public void updateCoords(float X, float Y){
        endX = X;
        endY = Y;
        arrowPath.reset();
        blank = false;
        this.postInvalidate();
    }

    /**
     * Clear the canvas (does not reset starting positions)
     */
    public void reset(){
        arrowPath.reset();
        blank = true;
        this.postInvalidate();
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (!blank) {
            arrowPath.moveTo(startX, startY);
            double theta = Math.atan2(endY - startY, endX - startX);
            arrowPath.rLineTo(2 * scaleFactor * (float) Math.cos(theta + Math.PI / 2.0), 2 * scaleFactor * (float) Math.sin(theta + Math.PI / 2.0));
            arrowPath.rLineTo(endX - startX - 8 * scaleFactor * (float) Math.cos(theta), endY - startY - 8 * scaleFactor * (float) Math.sin(theta)); //shaft
            arrowPath.rLineTo(6 * scaleFactor * (float) Math.cos(theta + Math.PI / 2.0), 6 * scaleFactor * (float) Math.sin(theta + Math.PI / 2.0)); //arrow out
            arrowPath.rLineTo(11.31370849898f * scaleFactor * (float) Math.cos(theta - Math.PI / 4.0), 11.31370849898f * scaleFactor * (float) Math.sin(theta - Math.PI / 4.0)); //arrow \
            arrowPath.rLineTo(11.31370849898f * scaleFactor * (float) Math.cos(theta + 5.0 * Math.PI / 4.0), 11.31370849898f * scaleFactor * (float) Math.sin(theta + 5.0 * Math.PI / 4.0)); //arrow /
            arrowPath.rLineTo(6 * scaleFactor * (float) Math.cos(theta + Math.PI / 2.0), 6 * scaleFactor * (float) Math.sin(theta + Math.PI / 2.0)); //arrow in
            arrowPath.rLineTo(startX - endX + 8 * scaleFactor * (float) Math.cos(theta), startY - endY + 8 * scaleFactor * (float) Math.sin(theta)); //shaft
            arrowPath.close();
            canvas.drawPath(arrowPath, arrowPaint);
        }
    }
}
