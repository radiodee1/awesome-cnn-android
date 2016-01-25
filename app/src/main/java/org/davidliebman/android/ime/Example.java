package org.davidliebman.android.ime;

/**
 * Created by dave on 1/18/16.
 */

        import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
        import org.deeplearning4j.eval.Evaluation;
        import org.deeplearning4j.nn.api.OptimizationAlgorithm;
        import org.deeplearning4j.nn.conf.GradientNormalization;
        import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
        import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
        import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
        import org.deeplearning4j.nn.conf.layers.DenseLayer;
        import org.deeplearning4j.nn.conf.layers.OutputLayer;
        import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
        import org.deeplearning4j.nn.conf.layers.setup.ConvolutionLayerSetup;
        import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
        import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToCnnPreProcessor;
        import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
        import org.deeplearning4j.nn.weights.WeightInit;
        import org.deeplearning4j.optimize.api.IterationListener;
        import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
        import org.nd4j.linalg.api.ndarray.INDArray;
        import org.nd4j.linalg.dataset.DataSet;
        import org.nd4j.linalg.dataset.SplitTestAndTrain;
        import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
        import org.nd4j.linalg.factory.Nd4j;
        import org.nd4j.linalg.lossfunctions.LossFunctions;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

        import java.io.*;
        import java.util.*;

/**
 *
 */
public class Example {

    private static final Logger log = LoggerFactory.getLogger(Example.class);

    CharacterEditor editor ;
    static boolean useGui = true;
    int batchSize = 64;
    int iterations = 1; //10
    int nEpochs = 1;

    public Example () throws Exception {

        batchSize = 1;
        nEpochs = 1;
        iterations = 1;

        editor = new CharacterEditor();

        int operation1 = Operation.EVAL_SINGLE_ALPHA_LOWER;
        OneHotOutput oneHot1 = new OneHotOutput(operation1);
        Network cnn1 = new Network(oneHot1.length());
        DataSetSplit data1 = new DataSetSplit(operation1);
        Operation opTest1 = new Operation(cnn1, data1, batchSize, nEpochs, iterations);
        opTest1.setFileManager( new FileManager("lenet_example_alpha_lower"));
        opTest1.setEvalType(operation1);

        int operation2 = Operation.EVAL_SINGLE_ALPHA_UPPER;
        OneHotOutput oneHot2 = new OneHotOutput(operation2);
        Network cnn2 = new Network(oneHot2.length());
        DataSetSplit data2 = new DataSetSplit(operation2);
        Operation opTest2 = new Operation(cnn2, data2, batchSize, nEpochs, iterations);
        opTest2.setFileManager( new FileManager("lenet_example_alpha_upper"));
        opTest2.setEvalType(operation2);

        int operation3 = Operation.EVAL_SINGLE_NUMERIC;
        OneHotOutput oneHot3 = new OneHotOutput(operation3);
        Network cnn3 = new Network(oneHot3.length());
        DataSetSplit data3 = new DataSetSplit(operation3);
        Operation opTest3 = new Operation(cnn3, data3, batchSize, nEpochs, iterations);
        opTest3.setFileManager( new FileManager("lenet_example_digits"));
        opTest3.setEvalType(operation3);

        editor.addOperations(opTest1,opTest2,opTest3);


    }

    public static void main(String[] args) throws Exception {

        if (useGui) {
            Example e = new Example();
        }
        else {

            int batchSize = 64;
            int iterations = 1; //10
            int nEpochs = 1;
            int operation = Operation.EVAL_TRAIN_NUMERIC_SHOW;

            OneHotOutput oneHot = new OneHotOutput(operation);

            Network cnn = new Network(oneHot.length());

            DataSetSplit data = new DataSetSplit(operation);

            final Operation opTest = new Operation(cnn, data, batchSize, nEpochs, iterations);
            opTest.setEvalType(operation);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {

                        opTest.saveModel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            opTest.startOperation();
        }

    }
}

