package org.davidliebman.android.ime;

import org.deeplearning4j.datasets.vectorizer.ImageVectorizer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;

/**
 * Created by dave on 1/19/16.
 */
public class Operation {
    Network network;
    MultiLayerNetwork model;
    //DataSetSplit data;
    DataSetIterator train, test;
    FileManager files ;

    INDArray singleInput;
    String singleOutput;

    //CharacterEditor editor;

    int batchSize;
    int epochs;
    int iterations;

    int evalType = 0;

    int output_cursor = 0, output_num = 0;
    float output_score = 0;

    public static final int EVAL_SINGLE_NUMERIC = 1;
    public static final int EVAL_SINGLE_ALPHA_UPPER = 2;
    public static final int EVAL_SINGLE_ALPHA_LOWER = 3;
    public static final int EVAL_TRAIN_NUMERIC = 4;
    public static final int EVAL_TRAIN_ALPHA_UPPER = 5;
    public static final int EVAL_TRAIN_ALPHA_LOWER = 6;
    public static final int EVAL_TRAIN_NUMERIC_SHOW = 7;


    private static final Logger log = LoggerFactory.getLogger(Operation.class);
    public boolean saveOnExit = true;

    public Operation(Network model, Object data, int batchSize, int epochs, int iterations) throws Exception{
        this.network = model;
        this.model = model.getModel();
        //this.data = data;
        this.batchSize = batchSize;
        this.epochs = epochs;
        this.iterations = iterations;

    }

    public Operation(Network model) throws Exception {
        this.network = model;
        this.model = model.getModel();


    }

    public void setFileManager(FileManager fileManager) throws Exception{
        files = fileManager;
        files.loadModel(model);
    }

    public String getOutput() {
        return singleOutput;
    }

    public void setEvalType(int type) {evalType = type;}

    public void startOperation(double in [][]) throws Exception {
        singleInput = Nd4j.create(in).linearViewColumnOrder();
        startOperation();
    }

    public void startOperation() throws Exception {

        switch (evalType) {
            case EVAL_SINGLE_ALPHA_UPPER:
                startOperationSingle();
                break;
            case EVAL_SINGLE_ALPHA_LOWER:
                startOperationSingle();
                break;
            case EVAL_SINGLE_NUMERIC:
                startOperationSingle();
                break;
            case EVAL_TRAIN_ALPHA_UPPER:
                //startOperationAlphaUpperShow();
                break;
            case EVAL_TRAIN_ALPHA_LOWER:
                //startOperationAlphaLowerShow();
                break;
            case EVAL_TRAIN_NUMERIC:
                //startOperationMnistTrain();
                break;
            case EVAL_TRAIN_NUMERIC_SHOW:
                //startOperationMnistShow();
                break;
        }

    }

    public void startOperationSingle() throws Exception {


        OneHotOutput oneHot = new OneHotOutput(evalType);

        System.out.println(oneHot.toString());

        showSquare(singleInput);

        INDArray output = model.output(singleInput.transpose());

        String hotOut = oneHot.getMatchingOut(output);
        System.out.println("output " + hotOut);

        singleOutput = hotOut;

    }



    /*
    public void saveModel() throws Exception {
        if(saveOnExit) {
            files.saveModel(model);
            System.out.println("c=" + output_cursor + " n=" + output_num + " score=" + output_score);
            System.out.println("save from outside Operation.java");
        }
    }
    */

    public static void showSquare(INDArray num) {
        System.out.println("---------");
        INDArray num2 = num.linearView();
        for (int k = 0; k < 28*28; k ++ ) {
            if ( num2.getDouble(k) > 0.5d ) {
                System.out.print("*");
            }
            else {
                System.out.print(".");
            }
            if ((k+1) % 28 == 0) System.out.println();
        }
        System.out.println();
        System.out.println("---------");
    }


}
