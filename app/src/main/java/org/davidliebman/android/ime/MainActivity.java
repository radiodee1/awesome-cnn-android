package org.davidliebman.android.ime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Example example;
    boolean mExampleLoadComplete = false;
    Context mContext;
    MainActivity mMyActivity;

    double[][] screen = new double[28][28];
    boolean write = true;
    int type = Operation.EVAL_SINGLE_ALPHA_UPPER;

    Operation [] operations;
    String mDisplay = "";

    private Canvas mCanvas;

    private InnerView view;
    private int mHeight = 0, mWidth = 0;
    int marginTop = 5, marginBottom = 5, marginLeft = 5, marginRight = 5;

    public static final int ONE_SIDE = 28;
    Paint mPaint = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = new InnerView(this);
        //setContentView((View) view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FrameLayout screenLoc = (FrameLayout) findViewById(R.id.innerView);
        screenLoc.addView(view);
        //screenLoc.addView(new InnerView(this));

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */



        mContext = this;
        mMyActivity = this;
        try {
            example = new Example(this, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        new ExampleInstantiate().execute(0);


        Button mRightAccept = (Button) findViewById(R.id.rightAccept);
        mRightAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView mOutput = (TextView) findViewById(R.id.textView);

                if(mExampleLoadComplete) {
                    if (operations != null && operations.length == 3) {
                        try {
                            switch (type) {
                                case Operation.EVAL_SINGLE_ALPHA_LOWER:
                                    mDisplay = "lower ";
                                    //mOutput = (TextView) findViewById(R.id.textView);
                                    operations[0].startOperation(getScreen());
                                    setOutput(operations[0].getOutput());
                                    break;
                                case Operation.EVAL_SINGLE_ALPHA_UPPER:
                                    mDisplay = "upper ";
                                    //mOutput = (TextView) findViewById(R.id.textView);
                                    mOutput.setText(mDisplay);
                                    operations[1].startOperation(getScreen());
                                    setOutput(operations[1].getOutput());
                                    break;
                                case Operation.EVAL_SINGLE_NUMERIC:
                                    mDisplay = "digits ";
                                    mOutput.setText(mDisplay);
                                    operations[2].startOperation(getScreen());
                                    setOutput(operations[2].getOutput());
                                    break;
                            }
                            clearScreen();

                        } catch (Exception p) {
                            p.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void addOperations ( Operation op1, Operation op2, Operation op3) {
        operations = new Operation[] {op1,op2,op3};
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public double [][] getScreen() { return screen ; }

    public void setOutput( String in ) {
        mDisplay = mDisplay + in;
        TextView mOutput = (TextView) findViewById(R.id.textView);
        mOutput.setText(mDisplay);
    }

    public void clearScreen() {
        for (int i = 0; i < 28; i ++ ) {
            for (int j = 0; j < 28; j ++ ) {
                screen[i][j] = 0.0d;
            }
        }
    }

    class InnerView extends View {



        public InnerView(Context c) {
            super(c);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float xx = (mWidth - (marginLeft+marginRight)) / (float) ONE_SIDE;
            float yy = (mHeight - (marginTop+marginBottom)) /(float) ONE_SIDE;

            //g.setColor(Color.BLACK);
            //g.fillRect(0, this.getHeight() - 2, this.getWidth(), 2);


            for (int i = 0; i < 28; i++) {
                for (int j = 0; j < 28; j++) {
                    if (screen[j][i] > 0.5d) {

                        int xpos = (int) (i * xx) + marginLeft;
                        int ypos = (int) (j * yy) + marginTop;

                        mPaint.setColor(Color.BLACK);
                        canvas.drawRect(xpos, ypos, xpos+ (int) (xx - 2), ypos + (int) (yy - 2), mPaint);
                    }
                }
            }


        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mWidth = w;
            mHeight = h;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            int sizex = mWidth - (marginRight + marginLeft);
            int sizey = mHeight - (marginTop + marginBottom);

            int xx = (int) event.getX() - marginLeft;
            int yy = (int) event.getY() - marginTop;

            int posx = (int) (xx / (float) sizex * ONE_SIDE);
            int posy = (int) (yy / (float) sizey * ONE_SIDE);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:

                    brushScreen(posx,posy);

                    invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            return true;
            //return super.onTouchEvent(event);

        }

        public  void brushScreen(int x, int y) {
            for (int i = y - 1; i <= y + 1; i ++) {
                for (int j = x - 1; j <= x + 1; j ++) {
                    if (i >= 0 && i < 28 && j >= 0 && j < 28) {
                        if (write) {
                            screen[i][j] = 1.0d;
                            //Log.e("color","color");
                        }
                        else {
                            screen[i][j] = 0.0d;
                        }
                    }
                }
            }
        }
    }

    class ExampleInstantiate extends AsyncTask< Integer , Integer , Integer > {

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                example.setNetworks();
            }
            catch (Exception e) {
                Log.e("mainactivity","optionsloader");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mExampleLoadComplete = true;
            mDisplay = "out: ";
            TextView mOutput = (TextView) findViewById(R.id.textView);
            mOutput.setText(mDisplay);
            super.onPostExecute(integer);
        }
    }
}
