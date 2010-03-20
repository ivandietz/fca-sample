package fca_sample;
import java.awt.*;
import java.net.*;
import javax.swing.*;
 
public class Anima {
    public static void main(String[] args) throws MalformedURLException {

        Icon icon = new ImageIcon("loading3.gif");
        JLabel label = new JLabel(icon);
 
        JFrame f = new JFrame("Animation");
        f.getContentPane().add(label);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}