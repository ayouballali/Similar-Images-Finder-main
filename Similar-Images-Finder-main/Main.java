package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Main {
     public static void main(String[] args) throws IOException {

        ImageClassifier imgClassifier = new ImageClassifier();
        Gui myGui = new Gui(1080, 720);
        myGui.SetUpGui(imgClassifier);

    }
}