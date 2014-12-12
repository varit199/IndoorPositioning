package com.indooratlas.example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Rawipol on 12/7/14 AD.
 */
public class MapPanel extends FrameLayout implements View.OnTouchListener {
    private int height;

    public MapPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initial();
    }

    public MapPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    public MapPanel(Context context) {
        super(context);
        initial();
    }

    private void initial(){
        //LayoutParams panel = (LayoutParams)getLayoutParams();
        //height = panel.height;

        //Add Map
        setOnTouchListener(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    private PointF last = new PointF();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());

        Log.d("oakTag","Touch"+getChildCount());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last.set(curr);
                Log.d("oakTag","down");

                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("oakTag","move");

                float deltaX = curr.x - last.x;
                float deltaY = curr.y - last.y;

                last.set(curr.x, curr.y);

                ImageView marker0 = (ImageView)getChildAt(0);
                ImageView marker1 = (ImageView)getChildAt(1);
                FrameLayout.LayoutParams params0 = (FrameLayout.LayoutParams)marker0.getLayoutParams();
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams)marker1.getLayoutParams();
                params0.leftMargin += deltaX;
                params0.topMargin += deltaY;
                params1.leftMargin += deltaX;//params0.leftMargin;
                params1.topMargin += deltaY;//params0.topMargin;


                marker0.setLayoutParams(params0);
                Log.d("oakTag",deltaX+" "+deltaY);


                marker1.setLayoutParams(params1);
                Log.d("oakTag",deltaX+" "+deltaY);




        }

        return true;
    }

}
