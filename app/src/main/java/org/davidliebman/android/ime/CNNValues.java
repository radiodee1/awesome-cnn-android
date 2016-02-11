package org.davidliebman.android.ime;

/**
 * Created by dave on 2/11/16.
 */
public class CNNValues {

    public static final int ONE_SIDE = 28;
    int RULE_POSITION = 18;
    boolean write = false;
    int type = Operation.EVAL_SINGLE_ALPHA_UPPER;

    int mViewHeight = 0, mViewWidth = 0;
    int marginTop = 5, marginBottom = 5, marginLeft = 5, marginRight = 5;
    int mWindowHeight, mWindowWidth;


    boolean mExampleLoadComplete = false;
    boolean mExampleBlockOutput = false;
    boolean mExampleTreatOutput = false;
    boolean mExampleNoCharacterPressed = false;
    boolean mExampleNoBrush = false;
    boolean mExampleInitInService = false;
}
