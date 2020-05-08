package hu.erdos.bittakaro;

import javax.swing.*;
import java.awt.*;

public class about extends JFrame {
    JFrame aFr = new JFrame("bitTakaro - névjegy");
    JPanel amp = new JPanel(new BorderLayout());

    JLabel lblTitle = new JLabel("bitTakaro "+bT.VersionNumber, SwingConstants.CENTER);
    JLabel lblC = new JLabel("(c) 2016 Erdős Attila", SwingConstants.RIGHT);
    JTextArea txtAbout = new JTextArea("\nA bitTakaro egy szöveg titkosítsra használható program.\nTöbbek között alkamas jelszavak biztonságos  tárolására.");

    public about(){
        aFr.setSize(400,300);
        aFr.setResizable(false);

        txtAbout.setEditable(false);
        txtAbout.setLineWrap(true);

        amp.add(lblTitle,BorderLayout.NORTH);
        amp.add(txtAbout,BorderLayout.CENTER);
        amp.add(lblC, BorderLayout.SOUTH);

        aFr.add(amp);
        aFr.setVisible(true);
    }
}
