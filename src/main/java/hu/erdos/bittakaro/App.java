package hu.erdos.bittakaro;

import javax.swing.*;

public class App {
    public static void main(String[] arg){
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }

        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }

        catch (ClassNotFoundException e) {
            // handle exception
        }

        catch (InstantiationException e) {
            // handle exception
        }

        catch (IllegalAccessException e) {
            // handle exception
        }

        new bT();
    }
}
