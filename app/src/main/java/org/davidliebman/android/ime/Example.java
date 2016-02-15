package org.davidliebman.android.ime;

/**
 * Created by dave on 1/18/16.
 */

        import android.content.Context;

/**
 *
 */
public class Example {

    //private static final Logger log = LoggerFactory.getLogger(Example.class);

    //CharacterEditor editor ;
    static boolean useGui = true;
    int batchSize = 64;
    int iterations = 1; //10
    int nEpochs = 1;

    CNNEditorInterface editor;

    Context mContext ;

    public Example ( Context c , CNNEditorInterface editor ) throws Exception {

        mContext = c;

        batchSize = 1;
        nEpochs = 1;
        iterations = 1;

        this.editor = editor;
        //editor = new CharacterEditor();

        String key1 = "com.github.fommil.netlib.BLAS";
        String prop1 = "com.github.fommil.netlib.F2jBLAS";

        String key2 = "com.github.fommil.netlib.LAPACK";
        String prop2 = "com.github.fommil.netlib.F2jLAPACK";

        String key3 = "com.github.fommil.netlib.ARPACK";
        String prop3 = "com.github.fommil.netlib.F2jARPACK";


        System.setProperty(key1, prop1);
        System.setProperty(key2, prop2);
        System.setProperty(key3, prop3);

        System.setProperty("org.nd4j.linalg.cpu.force_native","false");

        //Nd4j.ENFORCE_NUMERICAL_STABILITY = true;


    }
    public void setNetworks() throws Exception{


        int operation1 = Operation.EVAL_SINGLE_ALPHA_LOWER;
        OneHotOutput oneHot1 = new OneHotOutput(operation1);
        Network cnn1 = new Network(oneHot1.length());
        //DataSetSplit data1 = new DataSetSplit(operation1);
        Operation opTest1 = new Operation(cnn1, null, batchSize, nEpochs, iterations);
        opTest1.setFileManager(new FileManager(mContext, R.raw.lenet_example_alpha_lower));
        opTest1.setEvalType(operation1);

        int operation2 = Operation.EVAL_SINGLE_ALPHA_UPPER;
        OneHotOutput oneHot2 = new OneHotOutput(operation2);
        Network cnn2 = new Network(oneHot2.length());
        //DataSetSplit data2 = new DataSetSplit(operation2);
        Operation opTest2 = new Operation(cnn2, null, batchSize, nEpochs, iterations);
        opTest2.setFileManager( new FileManager(mContext, R.raw.lenet_example_alpha_upper));
        opTest2.setEvalType(operation2);

        int operation3 = Operation.EVAL_SINGLE_NUMERIC;
        OneHotOutput oneHot3 = new OneHotOutput(operation3);
        Network cnn3 = new Network(oneHot3.length());
        //DataSetSplit data3 = new DataSetSplit(operation3);
        Operation opTest3 = new Operation(cnn3, null, batchSize, nEpochs, iterations);
        opTest3.setFileManager( new FileManager(mContext, R.raw.lenet_example_digits));
        opTest3.setEvalType(operation3);

        editor.addOperations(opTest1,opTest2,opTest3);


    }


}

