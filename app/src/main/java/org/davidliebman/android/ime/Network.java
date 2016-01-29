package org.davidliebman.android.ime;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.setup.ConvolutionLayerSetup;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dave on 1/19/16.
 */
public class Network {
    private MultiLayerNetwork model;

    private int outputNum = 10;
    private int seed = 123;
    private int iterations = 1;
    private int nChannels = 1;

    //Logger log = LoggerFactory.getLogger(Network.class);


    public Network () {
        buildNetwork();
    }

    public Network(int num ) {
        setOutputNum(num);

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

        buildNetwork();
    }

    public void buildNetwork() {

        Nd4j.ENFORCE_NUMERICAL_STABILITY = true;

        //log.info("Build model.... " + outputNum);
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .regularization(true).l2(0.0005)
                .learningRate(0.01)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .list(4)
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        .nIn(nChannels)
                        .stride(1, 1)
                        .nOut(20).dropOut(0.5)
                        .activation("relu")
                        .build())
                .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(2, new DenseLayer.Builder().activation("relu")
                        .nOut(500).build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(outputNum)
                        .activation("softmax")
                        .build())
                .backprop(true).pretrain(false);
        new ConvolutionLayerSetup(builder,28,28,1);

        MultiLayerConfiguration conf = builder.build();
        model = new MultiLayerNetwork(conf);
        model.init();
    }

    public void setOutputNum(int num ) {outputNum = num;}
    public int getOutputNum() { return outputNum;}
    public MultiLayerNetwork getModel() {return model;}
    public void setModel(MultiLayerNetwork net) {model = net;}
}
