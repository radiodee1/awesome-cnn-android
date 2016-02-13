# awesome-cnn-android #

instructions:

1. load the app into your device.
2. wait for the text view to change from "LOADING" to a blank screen. When this has happened the network biases have loaded. This can take five minutes.
3. draw a character on the screen.
4. press the button marked "ENTER". The CNN should try to figure out the letter you drew.

about:

This program uses convolutional neural networks to try to figure out what letter you are drawing on the screen and feed it to a android program as input. The neural networks used are of the 'LeNet' type, pioneered by Yann LeCun.

http://yann.lecun.com/exdb/lenet/

Implementation of neural networks in this project use Skymind Ink's 'deeplearning4j' package, which is licensed under the Apache 2 license. Most Skymind code can be found in the files 'Network.java' and 'Operation.java' in the folder 'app/src/main/java/org/davidliebman/android/ime/'. 

http://deeplearning4j.org/
