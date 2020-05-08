package hu.erdos.bittakaro;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.Key;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bT extends JFrame implements ActionListener {

    public static String VersionNumber = "1.8";
    private static String keyValue;
    File confDir = new File(System.getProperty("user.home")+"/.bittakaro");
    File recent = new File(System.getProperty("user.home")+"/.bittakaro/recently");

    String getRec = new String();

    JFrame cfrm = new JFrame("bitTakaro "+VersionNumber);
    JPanel mp = new JPanel(new BorderLayout());
    JPanel bp = new JPanel(new GridLayout(1,4));
    JPanel txpane = new JPanel();

    JMenuBar menuB = new JMenuBar();
    JMenu Fmenu = new JMenu("Fájl");
    JMenu Tmenu = new JMenu("Szöveg");
    JMenu Bmenu = new JMenu("Beállítás");
    JMenu Smenu = new JMenu("Súgó");

    JCheckBox chkLW = new JCheckBox("Sor törés");

    JMenuItem mntNew = new JMenuItem("Új titkosított fájl!");
    JMenuItem mntLoad = new JMenuItem("Fájl betöltése");
    JMenuItem mntSave = new JMenuItem("Mentés");
    JMenuItem mntSearch = new JMenuItem("Keresés");
    JMenu mntRecently = new JMenu("Legutóbb használt:");
    JMenuItem[] mnuR;
    JMenuItem mntExit = new JMenuItem("Kilép");
    JMenuItem mntMod = new JMenuItem("Módosítás");
    JCheckBoxMenuItem mntsetLineWrap = new JCheckBoxMenuItem("Sor törés");
    JMenuItem mntAbout = new JMenuItem("Névjegy");
    JMenuItem mntDoc = new JMenuItem("Dokumentáció");

    JPasswordField pf = new JPasswordField();

    JLabel lblFileInfo = new JLabel();

    int keyLength = 16;
    static JTextArea txA = new JTextArea();
    static JScrollPane scTXA = new JScrollPane(txA);



    static String decData = null;
    static String encData = new String();

    public bT(){
        cfrm.setSize(1024,768);
        cfrm.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //	mntRecently.setText("Legutóbb használt: "+getRecently());
        setRecent();
        Fmenu.add(mntNew);
        Fmenu.add(mntLoad);
        Fmenu.add(mntSave);
        Fmenu.add(mntRecently);
        Tmenu.add(mntMod);
        Tmenu.add(mntSearch);
        Fmenu.add(mntExit);

        Bmenu.add(mntsetLineWrap);
        Smenu.add(mntDoc);
        Smenu.add(mntAbout);

        txA.setVisible(false);
        txA.setEditable(false);
        txA.setLineWrap(false);
        menuB.add(Fmenu);
        menuB.add(Tmenu);
        menuB.add(Bmenu);
        menuB.add(Smenu);
        mp.add(lblFileInfo,BorderLayout.NORTH);
        mp.add(scTXA,BorderLayout.CENTER);

        cfrm.setJMenuBar(menuB);
        cfrm.add(mp);
        cfrm.setVisible(true);

        if (!confDir.exists()){
            confDir.mkdir();
        }

        mntNew.addActionListener(this);
        mntMod.addActionListener(this);
        mntLoad.addActionListener(this);
        mntSave.addActionListener(this);
        mntSearch.addActionListener(this);
        mntExit.addActionListener(this);
        mntsetLineWrap.addActionListener(this);
        mntAbout.addActionListener(this);
        mntDoc.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e){
        for (int i=0; i<5; i++){
            if (e.getSource().equals(mnuR[i])){
                getRec = mnuR[i].getText();
                try {
                    OpenRecentData(getRec);
                }catch(IOException IOe){
                }
                setRecent();
            }

        }
        if (e.getSource().equals(mntExit)){
            System.exit(0);
        }

        if (e.getSource().equals(mntAbout)){
            new about();
        }

        if (e.getSource().equals(mntsetLineWrap)){
            txA.setLineWrap(mntsetLineWrap.getState());
        }

        if (e.getSource().equals(mntNew)){
            lblFileInfo.setText("<Új fájl>");

            if (newPassword()){
                setPass(keyValue);
                txA.setText("");
                txA.setVisible(true);
                txA.setEditable(true);
            } else {
                JOptionPane.showMessageDialog(rootPane,"HIBA! Nem egyezik a két jelszó!");
            }
            setRecent();
        }
        if (e.getSource().equals(mntSave)){
            if ((txA.isVisible()) && (!txA.getText().isEmpty())){
                SaveData(txA.getText());
                setRecent();
            }
        }
        if (e.getSource().equals(mntLoad)){
            txA.setVisible(true);
            txA.setText(null);
            try{
                OpenData();
            }catch(IOException IE){
            }
            setRecent();

        }
        if (e.getSource().equals(mntSearch)){
            SearchText();
        }

        if (e.getSource().equals(mntMod)){
            if (!txA.getText().isEmpty()){

                setPass(pWindow());
                if (passIsGood()){
                    txA.setEditable(true);
                    txA.setVisible(false);
                    txA.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(rootPane,"HIBA! Rossz jelszó!");
                }
            }
        }

        if (e.getSource().equals(mntDoc)){
            openDocumentation();
        }

    }

    public void SaveData(String Data){
        File SFile = null;
        if (keyValue != null){
            AES.setKey(keyValue);
            if ((lblFileInfo.getText().isEmpty() || (lblFileInfo.getText().equals("<Új fájl>")))){

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Fájl mentése");

                int userSelection = fileChooser.showSaveDialog(cfrm);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    SFile = fileChooser.getSelectedFile();
                    lblFileInfo.setText(SFile.getAbsolutePath());
                }
            } else
                SFile = new File(lblFileInfo.getText());
        }
        try{
            PrintWriter PW = new PrintWriter(new FileWriter(SFile));
            PW.write(AES.encrypt(txA.getText(),keyValue).toString());
            PW.close();
        }catch(Exception EN){
            System.out.println(EN);
        }
        txA.setEditable(false);
        JOptionPane.showMessageDialog(rootPane,"A fájl mentése megtörtént: "+SFile);
        try {
            addtoRecently(SFile.getAbsolutePath());
        } catch(IOException IOe){
        }

        mntRecently.setText("Legutóbb használt: ");
    }

    public String fileOpen(){
        File OFile = null;
        String pathF = new String();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Fájl megnyitása!");

        int userSelection = fileChooser.showOpenDialog(cfrm);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            OFile = fileChooser.getSelectedFile();
            pathF = OFile.getAbsolutePath();
        }
        return pathF;
    }

    public void OpenData() throws IOException{
        lblFileInfo.setText(fileOpen());
        encData="";
        BufferedReader bfr = new BufferedReader(new FileReader(new File(lblFileInfo.getText())));
        String L = null;
        while ((L = bfr.readLine()) != null){
            try {
                encData+=L;
            }catch(Exception e){
            }
        }
        bfr.close();
        if (!lblFileInfo.getText().isEmpty()){
            addtoRecently(lblFileInfo.getText());
            setPass(pWindow());
        }

    }
    public void OpenRecentData(String RF) throws IOException{
        lblFileInfo.setText(RF);
        txA.setVisible(true);
        encData="";
        BufferedReader bfr = new BufferedReader(new FileReader(new File(lblFileInfo.getText())));
        String L = null;
        while ((L = bfr.readLine()) != null){
            try {
                encData+=L;
            }catch(Exception e){
            }
        }
        bfr.close();
        if (!lblFileInfo.getText().isEmpty()){
            addtoRecently(lblFileInfo.getText());
            setPass(pWindow());
        }
    }

    public static void putDataToArea(String EDa){
        txA.setText("");
        try {
            txA.setText(AES.decrypt(EDa,keyValue));
        }catch (Exception e){
        }
    }

    public String pWindow(){
        String password = new String();
        JPasswordField passwordField = new JPasswordField();
        passwordField.setEchoChar('*');
        password = JOptionPane.showInputDialog(rootPane,"Titkos kulcs:");
        return password;
    }

    public boolean passIsGood(){
        File OF = new File(lblFileInfo.getText());
        String tmp = new String();
        String eD = new String();
        try{
            BufferedReader bfr = new BufferedReader(new FileReader(OF));
            String L = null;
            while ((L = bfr.readLine()) != null){
                eD+=L;
            }
            bfr.close();
            tmp = AES.decrypt(eD,keyValue);
        }catch (Exception e){
        }
        if (!tmp.isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public void setPass(String P){
        String Val = new String();
        String PassKey = new String();
        if (P.length() < keyLength){
            int k = keyLength-P.length();
            for (int i=0; i<k; i++){
                Val+="0";
            }
            PassKey = P+Val;
        } else {
            PassKey = P;
        }
        keyValue=PassKey;
        if (!encData.isEmpty()){
            bT.putDataToArea(encData);
        }
    }

    public boolean newPassword(){
        boolean match = false;
        String password1 = new String();
        String password2 = new String();
        JPasswordField pf1 = new JPasswordField();
        JPasswordField pf2 = new JPasswordField();
        pf1.setEchoChar('*');
        pf2.setEchoChar('*');
        Object[] obj1 = {"A kulcs:\n\n", pf1};
        Object[] obj2 = {"A kulcs mégegyszer:\n\n", pf2};
        Object stringArray[] = {"OK","Cancel"};
        if (JOptionPane.showOptionDialog(null, obj1, "Titkosító kulcs",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, obj1) == JOptionPane.YES_OPTION){
            password1 = pf1.getText();
            if (password1.length() > 16){
                JOptionPane.showMessageDialog(rootPane,"Hiba! Nem lehet 16 byte-nál hosszabb a kulcs!");
                password1 =  "AA";
            }
        }

        if (JOptionPane.showOptionDialog(null, obj2, "Titkosító kulcs",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, obj2) == JOptionPane.YES_OPTION){
            password2 = pf2.getText();
        }

        if ((password1.equals(password2)) && (!password1.equals(""))){
            keyValue=password1;
            match = true;
        }
        return match;
    }

    public void SearchText(){
        String tempStr = new String();
        Highlighter h = txA.getHighlighter();
        tempStr = JOptionPane.showInputDialog("Search text: ");
        Pattern pattern = Pattern.compile("\\b"+tempStr+"\\b");
        Matcher matcher = pattern.matcher(txA.getText());
        while( matcher.find() )
        {
            int start = matcher.start();
            int end = matcher.end();
            try {
                h.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
            } catch (BadLocationException e1) {

                e1.printStackTrace();
            }
        }
    }

    public boolean alreadyExistsLine(String Line){
        boolean aE = false;
        try{
            BufferedReader bfr = new BufferedReader(new FileReader(recent));
            String L = null;
            while ((L = bfr.readLine()) != null ){
                if (L.equals(Line)){
                    aE = true;
                }
            }
        }catch (IOException IOe){
        }
        return aE;
    }

    public void addtoRecently(String T) throws IOException{
        if (recIsFull()){
            delLastLine();
        }

        try {
            if (!alreadyExistsLine(T)){
                PrintWriter pw = new PrintWriter(new FileWriter(recent,true));
                pw.write(T+'\n');
                pw.close();
            }
        }catch (IOException IOe){
        }
    }

    public void delLastLine() throws IOException{
        File tempFile = new File(System.getProperty("user.home")+"/.bittakaro/recent.tmp");
        int item = 0;
        int i = 0;

        Vector<String> SV = new Vector<String>();

        SV.removeAllElements();
        BufferedReader bfr = new BufferedReader(new FileReader(recent));

        String L = null;
        while ((L = bfr.readLine()) != null){
            SV.addElement(L);
        }
        SV.remove(0);
        for (int j = 0; j<SV.size(); j++) {
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile,true));
            pw.write(SV.get(j)+'\n');
            pw.close();
        }

        bfr.close();
        recent.delete();
        tempFile.renameTo(recent);

    }


    public boolean recIsFull() {

        int itCount = 0;
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(recent));
            String L = null;
            while ((L = bfr.readLine()) != null){
                itCount++;
            }
            bfr.close();
        } catch(IOException IOe){

        }
        if (itCount < 5){
            return false;
        } else {
            return true;
        }

    }

    public void setRecent(){
        int i = 0;
        mnuR = new JMenuItem[10];

        mntRecently.removeAll();

        try {
            BufferedReader bfr = new BufferedReader(new FileReader(recent));
            String L = null;
            while ((L = bfr.readLine()) != null){
                mnuR[i] = new JMenuItem(L);
                mntRecently.add(mnuR[i]);
                mnuR[i].addActionListener(this);
                i++;
            }
            bfr.close();
        } catch(IOException IOe){

        }

    }



    public void openDocumentation() {
        if (Desktop.isDesktopSupported()) {
            try {
                File documentation = new File("/opt/erdos/bittakaro/documentation/bittakaro.pdf");
                Desktop desktop = Desktop.getDesktop();
                desktop.open(documentation);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane,"Nincs alapértelmezett PDF olvasó !");
            }
        }
    }
}
