package org.davidliebman.android.ime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Example example;
    boolean mExampleLoadComplete = false;
    boolean mExampleBlockOutput = false;
    boolean mExampleTreatOutput = false;
    Context mContext;
    MainActivity mMyActivity;

    double[][] screen = new double[28][28];
    boolean write = true;
    int type = Operation.EVAL_SINGLE_ALPHA_UPPER;

    Operation [] operations;
    String mDisplay = "";

    int characterLeft = 0, characterRight = 0, characterTop = 0, characterBottom = 0;

    private Canvas mCanvas;

    private InnerView view;
    private int mViewHeight = 0, mViewWidth = 0;
    int marginTop = 5, marginBottom = 5, marginLeft = 5, marginRight = 5;

    public static final int ONE_SIDE = 28;
    public int RULE_POSITION = 18; //22
    Paint mPaint = new Paint();

    int mWindowHeight, mWindowWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWindowDimensions();

        view = new InnerView(this);

        FrameLayout screenLoc = (FrameLayout) findViewById(R.id.innerView);
        screenLoc.addView(view);


        final FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp2.width = mWindowWidth/2;
        //lp2.gravity = Gravity.CENTER_HORIZONTAL;
        view.setLayoutParams(lp2);

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


                if (!mExampleBlockOutput) {
                    new OperationSingle().execute(0);
                    //mOutput.setText(mDisplay);
                }
            }

        });

        final Button mWriteErase = (Button) findViewById(R.id.writeErase);
        mWriteErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (write) {
                    write = false;
                    mWriteErase.setText("ERASE");
                    //System.out.println("erase");
                }
                else {
                    write = true;
                    mWriteErase.setText("WRITE");
                    //System.out.println("write");
                }
            }
        });

        final Button mToggle = (Button) findViewById(R.id.toggle);
        mToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case Operation.EVAL_SINGLE_ALPHA_LOWER:
                        mToggle.setText("UPPER");
                        type = Operation.EVAL_SINGLE_ALPHA_UPPER;
                        break;
                    case Operation.EVAL_SINGLE_ALPHA_UPPER:
                        mToggle.setText("#NUM#");
                        type = Operation.EVAL_SINGLE_NUMERIC;
                        break;
                    case Operation.EVAL_SINGLE_NUMERIC:
                        mToggle.setText("lower");
                        type = Operation.EVAL_SINGLE_ALPHA_LOWER;
                        break;
                    default:
                        mToggle.setText("lower");
                        type = Operation.EVAL_SINGLE_ALPHA_LOWER;
                        break;
                }

            }
        });

        Button mLeftErase = (Button) findViewById(R.id.leftErase);
        mLeftErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                mExampleBlockOutput = false;
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final View mView = getWindow().getDecorView();
        final WindowManager.LayoutParams lp = (WindowManager.LayoutParams) mView.getLayoutParams();

        lp.gravity = Gravity.BOTTOM;
        lp.width = mWindowWidth;
        lp.height = mWindowHeight / 2;

        getWindowManager().updateViewLayout(mView,lp);
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
        //System.out.println("setOutput " + mDisplay);
    }

    public void clearScreen() {
        for (int i = 0; i < 28; i ++ ) {
            for (int j = 0; j < 28; j ++ ) {
                screen[i][j] = 0.0d;
            }
        }
        view.invalidate();
    }

    public void examineScreen() {
        characterLeft = characterTop = 28;
        characterRight = characterBottom = 0;
        for (int i = 0; i < 28; i ++ ) {
            for (int j = 0; j < 28; j ++ ) {
                if(screen[i][j] >= 0.5d) {
                    if (i < characterTop) { characterTop = i;}
                    if (j < characterLeft) {characterLeft = j;}
                    if (j > characterRight) {characterRight = j;}
                    if (i > characterBottom) {characterBottom = i;}
                }
            }
        }

    }

    public void resize() {

        double[][] screenOut = new double[ONE_SIDE][ONE_SIDE];

        float mag =  (characterBottom - characterTop)/(float) (RULE_POSITION - 1);
        int mLeftMove =(int) ((characterLeft + ( ONE_SIDE - characterRight)) /2.0f) - characterLeft;

        if(type == Operation.EVAL_SINGLE_ALPHA_UPPER  ) {

            if (mag >= 1.0f) mag = 1.0f;

            for (int i = 0; i < 28; i++) {
                for (int j = 0; j < 28; j++) {

                    int yy = (int) (i * mag + characterTop);
                    int xx = mLeftMove + j;

                    if (yy >= 0 && yy < ONE_SIDE && xx < ONE_SIDE && xx >= 0 && screen[yy][j] >= 0.5d) {
                        screenOut[i][xx] = 1.0d;
                    }
                }
            }
            screen = screenOut;
        }
        if (type == Operation.EVAL_SINGLE_ALPHA_LOWER) {

            int ii = 0;
            mag = 1.0f;

            if ( characterBottom < RULE_POSITION ) {

                ii = (characterTop + ( RULE_POSITION - characterBottom));
            }
            else {
                ii = characterTop;
            }

            for (int i = 0; i < 28; i++) {
                for (int j = 0; j < 28; j++) {

                    int yy = (int) (i * mag + characterTop);
                    int xx = mLeftMove + j;

                    if (yy >= 0 && yy < ONE_SIDE && xx < ONE_SIDE &&
                            xx >= 0 && i + ii < ONE_SIDE && i + ii >= 0 && screen[yy][j] >= 0.5d) {
                        screenOut[i+ii][xx] = 1.0d;
                    }
                }
            }
            screen = screenOut;
        }


    }

    public void setWindowDimensions() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mWindowHeight = metrics.heightPixels;
        mWindowWidth = metrics.widthPixels;
    }

    class InnerView extends View {



        public InnerView(Context c) {
            super(c);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float xx = (mViewWidth - (marginLeft+marginRight)) / (float) ONE_SIDE;
            float yy = (mViewHeight - (marginTop+marginBottom)) /(float) ONE_SIDE;

            if(type == Operation.EVAL_SINGLE_ALPHA_LOWER) {
                mPaint.setColor(Color.BLUE);
                canvas.drawRect(0, yy * RULE_POSITION, mViewWidth, (yy * RULE_POSITION) + 2, mPaint);
            }

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
            mViewWidth = w;
            mViewHeight = h;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if(mExampleBlockOutput) return true;

            int sizex = mViewWidth - (marginRight + marginLeft);
            int sizey = mViewHeight - (marginTop + marginBottom);

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

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mExampleLoadComplete = true;
            mDisplay = "output ready: ";
            TextView mOutput = (TextView) findViewById(R.id.textView);
            mOutput.setText(mDisplay);
            super.onPostExecute(integer);
        }
    }

    class OperationSingle extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            mExampleBlockOutput = true;
            if (mExampleTreatOutput) {
                examineScreen();
                resize();
            }
            super.onPreExecute();
        }



        @Override
        protected String doInBackground(Integer... params) {
            String mOutput = "";
            if(mExampleLoadComplete) {
                if (operations != null && operations.length == 3) {
                    try {

                        switch (type) {
                            case Operation.EVAL_SINGLE_ALPHA_LOWER:

                                operations[0].startOperation(getScreen());
                                mOutput = (operations[0].getOutput());
                                break;
                            case Operation.EVAL_SINGLE_ALPHA_UPPER:

                                operations[1].startOperation(getScreen());
                                mOutput = (operations[1].getOutput());
                                break;
                            case Operation.EVAL_SINGLE_NUMERIC:

                                operations[2].startOperation(getScreen());
                                mOutput = (operations[2].getOutput());
                                break;
                        }
                        //clearScreen();

                    } catch (Exception p) {
                        p.printStackTrace();
                    }
                }
            }


            return mOutput;
        }

        @Override
        protected void onPostExecute(String in) {
            setOutput(in);
            clearScreen();
            mExampleBlockOutput = false;
            super.onPostExecute(in);
        }
    }
}
