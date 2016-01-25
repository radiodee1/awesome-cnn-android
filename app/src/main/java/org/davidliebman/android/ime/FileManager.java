package org.davidliebman.android.ime;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;

/**
 * Created by dave on 1/19/16.
 */
public class FileManager {
    MultiLayerNetwork model;

    String fileName = "";
    String name = "lenet_example_digits";
    String homeDir = System.getProperty("user.home") + File.separator +"workspace" + File.separator;

    FileManager (  String name  ) {
        setFileName(name);
        //model = m;
    }

    public MultiLayerNetwork getModel() {return model;}
    public void setModel(MultiLayerNetwork m) {model = m;}

    public void setFileName(String name) {
        this.name = name;

        fileName = homeDir + name +".bin";
    }

    public void setLongFileName(String name) {fileName = name;}

    public void loadModel(MultiLayerNetwork m ) throws Exception{
        model = m;
        File filePath = new File(fileName);
        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
        INDArray newParams = Nd4j.read(dis);
        dis.close();
        model.setParameters(newParams);
    }

    public void saveModel(MultiLayerNetwork m) throws Exception {
        model = m;
        //Write the network parameters:
        File filePointer = new File(fileName);
        //OutputStream fos = Files.newOutputStream(Paths.get(fileName));
        FileOutputStream fos = new FileOutputStream(filePointer);
        DataOutputStream dos = new DataOutputStream(fos);
        Nd4j.write(model.params(), dos);
        dos.flush();
        dos.close();
    }
}
