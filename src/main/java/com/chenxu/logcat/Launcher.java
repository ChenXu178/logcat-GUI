package com.chenxu.logcat;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class Launcher {

    private static MainFrame mainFrame;

    public static void main(String[] args){
        initGlobalFont();
        mainFrame = new MainFrame();
        mainFrame.setMinimumSize(new Dimension(900,600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private static void initGlobalFont(){
        UIManager.put("TextField.inactiveForeground", new Color(0, 0, 0));
        FontUIResource fontUIResource = new FontUIResource(new Font("微软雅黑",Font.PLAIN, 12));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value= UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }
}
