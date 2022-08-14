package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Gui{
    private ArrayList<String> similarImages;

    private JFrame frame;
    private JPanel panel1, panel2;
    private  JCheckBox checkBox1;
    private  JCheckBox checkBox2;
    private  JCheckBox checkBox3;
    private JButton submit;
    private JButton load;
    private Border blackline = BorderFactory.createLineBorder(Color.black);
    private Border redline = BorderFactory.createLineBorder(Color.red);
    Color colorPanel = new Color(233, 235, 238, 255);

    private int width;
    private int height;
    
    public Gui(int w, int h) {
        frame = new JFrame();
        width = w;
        height = h;
    }
    
    public void SetUpGui(ImageClassifier imgClassifier) {
        frame.setSize(width, height);
        frame.setTitle("ImageClassification");
        panel1 = new JPanel();
        panel1.setBackground(colorPanel);
        panel1.setLocation(0, 0);
        panel1.setSize(width/3, height - 40);
        panel1.setBorder(blackline);

        load = new JButton("Importer");
        load.setBounds(width/12, height/3 - 80, 160,20);
        frame.add(load);

        checkBox1 = new JCheckBox("Couleur", true);
        checkBox1.setBounds(width/12, height/3, 160,20);
        checkBox1.setBorderPainted(true);
        checkBox1.setBorder(redline);
        frame.add(checkBox1);

        // button lo listner load
        String[] filepath = new String[1];
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Insert code here
                JFileChooser fc = new JFileChooser(new File("coil-100"));
                int i = fc.showOpenDialog(frame);
                if(i == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    filepath[0] = f.getPath();
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(filepath[0]));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    JLabel selct = new JLabel();
                    selct.setIcon(new ImageIcon(img));
                    selct.setBounds(50, 50, 128, 128);
                    selct.setBorder(redline);
                    panel1.add(selct);

                    frame.add(panel1);
                    frame.add(panel2);
                    frame.pack();
                    frame.setSize(width, height);
                }
                System.out.println(filepath[0]);
            }
        });

        checkBox2 = new JCheckBox("Forme",false);
        checkBox2.setBounds(width/12,height/3 + 40, 160,20);
        checkBox2.setBorderPainted(true);
        checkBox2.setBorder(redline);
        frame.add(checkBox2);

        checkBox3 = new JCheckBox("Texture",false);
        checkBox3.setBounds(width/12,height/3 + 80, 160,20);
        checkBox3.setBorderPainted(true);
        checkBox3.setBorder(redline);
        frame.add(checkBox3);

        submit = new JButton("Rechercher");
        submit.setBounds(width/12, height/3 + 120, 160, 20);
        frame.add(submit);

        panel2 = new JPanel();
        panel2.setBackground(colorPanel);
        panel2.setLocation(width/3 + 5, 0);
        panel2.setSize(width - (width/3) - 15, height - 40);
        panel2.setBorder(blackline);
        panel2.setLayout(new GridLayout(6,6, 0, 1));

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Insert code here
                if(filepath[0] == null)
                    return;

                BufferedImage selectedImg = null;

                try {
                    selectedImg = ImageIO.read(new File(filepath[0]));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                panel2.removeAll();

                similarImages = imgClassifier.findSimilar(selectedImg);

//                imgClassifier.show(selectedImg);

                for (String name : similarImages) {
                    System.out.println("found : " + name);
                    JLabel label = new JLabel();
                    label.setIcon(new ImageIcon(new ImageIcon("coil-100/" + name).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT)));
//                    panel2.add(label);
                    panel2.add(label);
                }

                frame.add(panel1);
                frame.add(panel2);
                frame.pack();
                frame.setSize(width, height);
            }
        });

        frame.add(panel1);
        frame.add(panel2);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void showGrid(ArrayList<JLabel> labels) {
        System.out.println("size = " + labels.size());
        for(JLabel label : labels) {
            panel2.add(label);
        }
        frame.add(panel2);
    }

}




