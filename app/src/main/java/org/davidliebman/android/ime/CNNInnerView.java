package org.davidliebman.android.ime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dave on 2/11/16.
 */
public class CNNInnerView extends View {

    double [][] viewScreen = new double[CNNValues.ONE_SIDE][CNNValues.ONE_SIDE];

    Paint mPaint = new Paint();
    CNNValues val;
    CNNService service;

    public CNNInnerView(Context c, CNNValues v, CNNService s) {
        super(c);
        val = v;
        service = s;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xx = (val.mViewWidth - (val.marginLeft+val.marginRight)) / (float) CNNValues.ONE_SIDE;
        float yy = (val.mViewHeight - (val.marginTop+val.marginBottom)) /(float) CNNValues.ONE_SIDE;

        if(val.type == Operation.EVAL_SINGLE_ALPHA_LOWER) {
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(0, yy * val.RULE_POSITION, val.mViewWidth, (yy * val.RULE_POSITION) + 2, mPaint);
        }

        mPaint.setColor(Color.LTGRAY);
        canvas.drawRect(0,0,val.mViewWidth,val.mViewHeight,mPaint);

        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                if (viewScreen[j][i] > 0.5d) {

                    int xpos = (int) (i * xx) + val.marginLeft;
                    int ypos = (int) (j * yy) + val.marginTop;

                    mPaint.setColor(Color.BLACK);
                    canvas.drawRect(xpos, ypos, xpos+ (int) (xx - 2), ypos + (int) (yy - 2), mPaint);
                }
            }
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        val.mViewWidth = w;
        val.mViewHeight = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(val.mExampleBlockOutput) return true;

        int sizex = val.mViewWidth - (val.marginRight + val.marginLeft);
        int sizey = val.mViewHeight - (val.marginTop + val.marginBottom);

        int xx = (int) event.getX() - val.marginLeft;
        int yy = (int) event.getY() - val.marginTop;

        int posx = (int) (xx / (float) sizex * CNNValues.ONE_SIDE);
        int posy = (int) (yy / (float) sizey * CNNValues.ONE_SIDE);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                brushScreen(posx,posy);


                invalidate();

                service.setScreen(viewScreen);

                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
        //return super.onTouchEvent(event);

    }

    public  void brushScreen(int x, int y) {
        if(val.mExampleNoBrush) {
            viewScreen[y][x] = 1.0d;
            return;
        }
        for (int i = y - 1; i < y + 1; i ++) {
            for (int j = x - 1; j < x + 1; j ++) {
                if (i >= 0 && i < 28 && j >= 0 && j < 28) {
                    if (val.write) {
                        viewScreen[i][j] = 1.0d;
                        //Log.e("color","color");
                    }
                    else {
                        viewScreen[i][j] = 0.0d;
                    }
                }
            }
        }
    }
}
