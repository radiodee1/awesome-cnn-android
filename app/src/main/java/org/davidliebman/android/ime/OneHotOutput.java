package org.davidliebman.android.ime;

import org.nd4j.linalg.api.ndarray.INDArray;


/**
 * Created by dave on 1/21/16.
 */
public class OneHotOutput {

    String stringList = "";

    int start = 0, stop = 0 ;

    public OneHotOutput(int type) {
        String key1 = "com.github.fommil.netlib.BLAS";

        System.out.println(" run with "+System.getProperty(key1,""));


        switch (type) {
            case Operation.EVAL_SINGLE_NUMERIC:
            case Operation.EVAL_TRAIN_NUMERIC:
            case Operation.EVAL_TRAIN_NUMERIC_SHOW:
                makeList( "0","9");
                break;
            case Operation.EVAL_SINGLE_ALPHA_LOWER:
            case Operation.EVAL_TRAIN_ALPHA_LOWER:
            //case TYPE_ALPHA_LOWER:
                makeList("a","z");
                break;
            case Operation.EVAL_TRAIN_ALPHA_UPPER:
            case Operation.EVAL_SINGLE_ALPHA_UPPER:
            //case TYPE_ALPHA_UPPER:
                makeList("A","Z");
                break;
        }
    }

    public String makeList(int start, int stop) {
        this.start = start;
        this.stop = stop;
        String stringList = "";
        for (int i = start; i <= stop; i ++) {
            stringList = stringList + String.valueOf( (char)i);
        }
        return stringList;
    }

    public void makeList(String start, String stop) {
        int startNum = start.charAt(0);
        int stopNum = stop.charAt(0);
        stringList = makeList(startNum,stopNum);
    }

    public void addToList(String append) {
        stringList = stringList + append;
    }

    public String toString() {
        return stringList;
    }

    public String getMatchingOut (double [] in) {
        String out = "";
        double largest = 0;
        if (stringList.length() > 0 && in.length == stringList.length()) {
            out = stringList.substring(0,1);
            for (int i = 0; i < stringList.length(); i ++) {
                if (in[i] > largest) {
                    largest = in[i];
                    out = stringList.substring(i,i+1);
                }
            }
        }
        return out;

    }

    public String getMatchingOut (INDArray in) {
        String out = "";
        double largest = 0;
        in = in.linearView();
        if (stringList.length() > 0 && in.length() == stringList.length()) {
            out = stringList.substring(0,1);
            for (int i = 0; i < stringList.length(); i ++) {
                if (in.getDouble(i) > largest) {
                    largest = in.getDouble(i);
                    out = stringList.substring(i,i+1);
                }
            }
        }
        return out;

    }

    public boolean getIsMember (String in) {
        boolean out = false;

        if (stringList.length() > 0 ) {
            if (stringList.contains(in)) out = true;
        }

        return out;
    }

    public int length() {
        return stringList.length();
    }

    public int getMemberNumber(String in) {
        int out = -1;
        int offset = 0;
        double largest = 0;
        // input is hex for character
        if (in.length() >= 2) {
            int num = Integer.parseInt(in,16);
            in = String.valueOf((char) num);

            offset = start;
            //System.out.println(offset + " in "+in + "  "+String.valueOf(num) + " " + stringList.length());
        }
        // input is a visual representation of the character
        if (stringList.length() > 0) {

            for (int i = 0; i < stringList.length(); i++) {
                if (in.equals(stringList.substring(i, i + 1))) {

                    out = i + offset;

                    //System.out.println("out " + out);
                }
            }
        }

        return out ;
    }

    /*
    public INDArray getLabelOutput(String in) {
        INDArray out = Nd4j.create(new double[stringList.length()][1]);

        // MUST BE IMAGE OF DESIRED CHARACTER, IE ASCII LENGTH 1
        int num = getMemberNumber(in.substring(0,1));

        for (int i = 0; i < stringList.length(); i ++) {
            if (i == num) {
                out.putScalar(i, 1.0d);
            }
            else {
                out.putScalar(i, 0.0d);
            }
        }
        //System.out.println(out.toString());
        return out;
    }
    */
}
