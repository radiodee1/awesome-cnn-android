package org.davidliebman.android.ime;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CNNService extends InputMethodService implements CNNEditor {

    Example example;


    Context mContext;
    CNNService mMyService;
    View mMyServiceView;

    CNNValues val = new CNNValues();

    double[][] screen = new double[28][28];


    Operation [] operations;
    String mDisplay = "";

    int characterLeft = 0, characterRight = 0, characterTop = 0, characterBottom = 0;

    ProgressBar mProgress;
    RelativeLayout inputView;

    private CNNInnerView view;

    Button mWriteErase, mToggle;
    TextView mOutput;

    @Override
    public void onCreate() {

        super.onCreate();
        try {
            example = new Example(this, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (!val.mExampleInitInService) {
            new ExampleInstantiate().execute(0);
        }
        else {
            try {
                example.setNetworks();
            }
            catch (Exception e) {

                e.printStackTrace();
            }
            val.mExampleLoadComplete = true;
            mDisplay = "output ready: ";
            //TextView mOutput = (TextView) mMyServiceView.findViewById(R.id.textView);
            //mOutput.setText(mDisplay);
        }


        //mConnection = this.getCurrentInputConnection();
    }

    @Override
    public View onCreateInputView() {


        setWindowDimensions();
        /*
        inputView = (FrameLayout) getLayoutInflater().inflate( R.layout.ime_main, null);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, val.mWindowHeight/2);
        //(FrameLayout.LayoutParams) inputView.getLayoutParams();
        LinearLayout topHalf = (LinearLayout)inputView.findViewById(R.id.topHalf);
        topHalf.setLayoutParams(lp);
        */

        inputView = (RelativeLayout) getLayoutInflater().inflate(R.layout.content_main, null);
        /*
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, val.mWindowHeight/2);
        lp.gravity = Gravity.BOTTOM;
        lp.height = val.mWindowHeight/2;
        inputView.setLayoutParams(lp);
        */
        //////////////////////////////////
        view = new CNNInnerView(this, val ,this );


        FrameLayout screenLoc = (FrameLayout) inputView.findViewById(R.id.innerView);
        screenLoc.addView(view);

        final FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) view.getLayoutParams();


        inputView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                inputView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                view.getLocationOnScreen(locations);
                int x = locations[0];
                int y = locations[1];
                lp2.height = val.mWindowHeight - y;
                lp2.width = val.mWindowWidth/2;

                if ((val.mWindowHeight -y) * 1.25 < val.mWindowWidth / 2 ) {
                    lp2.width = val.mWindowHeight -y ;
                    //make it a square
                }
                view.setLayoutParams(lp2);
            }
        });

        //view.setLayoutParams(lp2);

        mContext = this.getApplicationContext();
        mMyService = this;
        mMyServiceView = inputView;



        Button mRightAccept = (Button) inputView.findViewById(R.id.rightAccept);
        mRightAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                examineScreen();
                if (!val.getBlocked()) {
                    new OperationSingle().execute(0);

                }
                else {
                    setOutput(" ");
                }
            }

        });

        mWriteErase = (Button) inputView.findViewById(R.id.writeErase);
        mWriteErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (val.write) {
                    val.write = false;
                    mWriteErase.setText("ERASE");
                    //System.out.println("erase");
                }
                else {
                    val.write = true;
                    mWriteErase.setText("WRITE");
                    //System.out.println("write");
                }
            }
        });

        mToggle = (Button) inputView.findViewById(R.id.toggle);
        mToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (val.type) {
                    case Operation.EVAL_SINGLE_ALPHA_LOWER:
                        mToggle.setText("UPPER");
                        val.type = Operation.EVAL_SINGLE_ALPHA_UPPER;
                        break;
                    case Operation.EVAL_SINGLE_ALPHA_UPPER:
                        mToggle.setText("#NUM#");
                        val.type = Operation.EVAL_SINGLE_NUMERIC;
                        break;
                    case Operation.EVAL_SINGLE_NUMERIC:
                        mToggle.setText("lower");
                        val.type = Operation.EVAL_SINGLE_ALPHA_LOWER;
                        break;
                    default:
                        mToggle.setText("lower");
                        val.type = Operation.EVAL_SINGLE_ALPHA_LOWER;
                        break;
                }

            }
        });

        Button mLeftErase = (Button) inputView.findViewById(R.id.leftErase);
        mLeftErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearScreen();
                val.mExampleBlockOutput = false;
                InputConnection mConnection = getCurrentInputConnection();

                //setOutput(Character.toString((char) KeyEvent.KEYCODE_DEL));
                mConnection.deleteSurroundingText(1, 0);
            }
        });


        Button mRightCursor = (Button) inputView.findViewById(R.id.rightCursor);
        mRightCursor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputConnection mConnection = getCurrentInputConnection();

                mConnection.commitText("", 2);
            }
        });

        Button mLeftCursor = (Button) inputView.findViewById(R.id.leftCursor);
        mLeftCursor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputConnection mConnection = getCurrentInputConnection();


                mConnection.commitText("", -1);


            }
        });

        Button mGoButton = (Button) inputView.findViewById(R.id.goButton);
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOutput("\n");
            }
        });

        mProgress = (ProgressBar) inputView.findViewById(R.id.progressBar);
        mProgress.setMax(10);

        return inputView;
    }





    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        int type = info.inputType & InputType.TYPE_CLASS_TEXT;

        //progressBar = (ProgressBar) inputView.findViewById(R.id.progressBar);
        //progressBar.setMax(10);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, val.mWindowHeight/2);
        lp.gravity = Gravity.BOTTOM;
        lp.height = val.mWindowHeight/2;
        inputView.setLayoutParams(lp);
        //if (mExampleLoadComplete) progressBar.setVisibility(View.GONE);
        if(!val.mExampleLoadComplete && mProgress != null ) {
            mProgress.setProgress(3);
        }

        if (type == 1) {
            if(val.mExampleLoadComplete) {
                mDisplay = "";
            }

        }
        try {
            if (mToggle != null) {
                switch (val.type) {
                    case Operation.EVAL_SINGLE_ALPHA_UPPER:
                        mToggle.setText("UPPER");
                        break;
                    case Operation.EVAL_SINGLE_NUMERIC:
                        mToggle.setText("#NUM#");
                        break;
                    case Operation.EVAL_SINGLE_ALPHA_LOWER:
                        mToggle.setText("lower");
                        break;
                    default:
                        mToggle.setText("lower");
                        break;
                }
            }
            if (mWriteErase != null) {
                if (!val.write) {
                    mWriteErase.setText("ERASE");
                    //System.out.println("erase");
                }
                else {
                    mWriteErase.setText("WRITE");
                    //System.out.println("write");
                }
            }
            mOutput = (TextView) inputView.findViewById(R.id.textView);
            mOutput.setText(mDisplay);
        }
        catch (Exception e) {

        }
        super.onStartInputView(info, restarting);
    }

    @Override
    public void addOperations ( Operation op1, Operation op2, Operation op3) {
        operations = new Operation[] {op1,op2,op3};
    }



    public double [][] getScreen() { return screen ; }





    public void setOutput( String in ) {
        InputConnection mConnection = getCurrentInputConnection();
        mConnection.commitText(in,1);
        mDisplay = mDisplay + in;
        TextView mOutput = (TextView) mMyServiceView.findViewById(R.id.textView);
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
        val.mExampleNoCharacterPressed = true;
        characterLeft = characterTop = 28;
        characterRight = characterBottom = 0;
        for (int i = 0; i < 28; i ++ ) {
            for (int j = 0; j < 28; j ++ ) {
                if(screen[i][j] >= 0.5d) {
                    val.mExampleNoCharacterPressed = false;
                    if (i < characterTop) { characterTop = i;}
                    if (j < characterLeft) {characterLeft = j;}
                    if (j > characterRight) {characterRight = j;}
                    if (i > characterBottom) {characterBottom = i;}
                }
            }
        }

    }

    public void resize() {

        double[][] screenOut = new double[CNNValues.ONE_SIDE][CNNValues.ONE_SIDE];

        float mag =  (characterBottom - characterTop)/(float) (val.RULE_POSITION - 1);
        int mLeftMove =(int) ((characterLeft + ( CNNValues.ONE_SIDE - characterRight)) /2.0f) - characterLeft;

        if(val.type == Operation.EVAL_SINGLE_ALPHA_UPPER  ) {

            if (mag >= 1.0f) mag = 1.0f;

            for (int i = 0; i < 28; i++) {
                for (int j = 0; j < 28; j++) {

                    int yy = (int) (i * mag + characterTop);
                    int xx = mLeftMove + j;

                    if (yy >= 0 && yy < CNNValues.ONE_SIDE && xx < CNNValues.ONE_SIDE && xx >= 0 && screen[yy][j] >= 0.5d) {
                        screenOut[i][xx] = 1.0d;
                    }
                }
            }
            screen = screenOut;
        }
        if (val.type == Operation.EVAL_SINGLE_ALPHA_LOWER) {

            int ii = 0;
            mag = 1.0f;

            if ( characterBottom < val.RULE_POSITION ) {

                ii = (characterTop + ( val.RULE_POSITION - characterBottom));
            }
            else {
                ii = characterTop;
            }

            for (int i = 0; i < 28; i++) {
                for (int j = 0; j < 28; j++) {

                    int yy = (int) (i * mag + characterTop);
                    int xx = mLeftMove + j;

                    if (yy >= 0 && yy < CNNValues.ONE_SIDE && xx < CNNValues.ONE_SIDE &&
                            xx >= 0 && i + ii < CNNValues.ONE_SIDE && i + ii >= 0 && screen[yy][j] >= 0.5d) {
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
        val.mWindowHeight = metrics.heightPixels;
        val.mWindowWidth = metrics.widthPixels;
    }


    public void setScreen(double [][] in) {screen = in;}



    class ExampleInstantiate extends AsyncTask< Integer , Integer , Integer > {

        @Override
        protected void onPreExecute() {
            val.mExampleLoadComplete = false;
            mDisplay = "LOADING";
            //progressBar.setVisibility(View.VISIBLE);



            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            //this.publishProgress(3);
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
            val.mExampleLoadComplete = true;
            mDisplay = "";
            TextView mOutput = (TextView) mMyServiceView.findViewById(R.id.textView);
            mOutput.setText(mDisplay);
            if (mProgress != null) {
                mProgress.setProgress(15);
                mProgress.setVisibility(View.GONE);
            }
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            //if (progressBar != null) progressBar.setProgress(3);

            super.onProgressUpdate(values);
        }
    }

    class OperationSingle extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            val.mExampleBlockOutput = true;


            if (val.mExampleTreatOutput) {
                resize();
            }
            super.onPreExecute();
        }



        @Override
        protected String doInBackground(Integer... params) {
            String mOutput = "";
            if(val.mExampleLoadComplete) {
                if (operations != null && operations.length == 3) {
                    try {

                        //choose which neural network!!
                        for (int i = 0; i < operations.length; i ++) {
                            if (val.type == operations[i].getEvalType()) {
                                operations[i].startOperation(getScreen());
                                mOutput = operations[i].getOutput();
                            }
                        }



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
            val.mExampleBlockOutput = false;
            super.onPostExecute(in);
        }
    }
}
