# awesome-cnn-android #

### Instructions: ###

1. Load the app into your device.

2. Wait for the text view to change from "LOADING" to a blank area. There should also be a progress spinner. Wait until it disappears. When this has happened the network biases have loaded. This can take five minutes.

3. Draw a character on the screen. Use the gray square at the bottom of the screen to draw your character.

4. Press the button marked "ENTER". The CNN should try to figure out the letter you drew.

5. You can select from the categories 'UPPER-case', 'LOWER-case', and 'NUM-bers' by pressing the toggle button above the gray character input area. In 'UPPER' mode only upper case characters will be correctly identified at the input. Similarly in 'LOWER' mode only lower case characters will be detected.

6. You can also choose 'WRITE' or 'ERASE' from another toggle button above the gray input area. This allows you to edit the character you are drawing with more precision.

7. Special buttons have been provided for 'Backspace' (labeled 'BACK') and 'Carriage-Return' (labeled 'GO'). There are also buttons for navigation that will allow you to move forward and back in the text area. These are labeled with arrows.

8. If you cannot get the neural network to recognise your drawings, there is a set of drop-down menus that will allow you to pick any character that is displayed on the normal keyboard and send it to the text-area. There are four of these dropdowns, for (a) numbers, (b) symbols, (c) upper-case letters, and (d) lower-case letters. They are located just to the right and left of the input area at the bottom of the screen. To use them, touch the drop-down menu and select the character you want. When you do this that character will appear on the label of the 'ENTER' key. Press the enter key at this time to send the selected character to the text area.

---

### About: ###

This program uses convolutional neural networks to try to figure out what letter you are drawing on the screen and feed it to a android program as input. The neural networks used are of the 'LeNet' type, pioneered by Yann LeCun.

http://yann.lecun.com/exdb/lenet/

Implementation of neural networks in this project use Skymind Ink's 'deeplearning4j' package, which is licensed under the Apache 2 license. Most Skymind code can be found in the files 'Network.java' and 'Operation.java' in the folder 'app/src/main/java/org/davidliebman/android/ime/'. 

http://deeplearning4j.org/
