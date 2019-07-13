package com.chenxu.logcat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

public class MainFrame extends JFrame {
    private JPanel panelTop,panelButton,panelOutput;
    private JButton btnStart,btnSave,btnClean;
    private JLabel labelPackage,labelTag,labelKey;
    private JTextField textPackage,textTag,textKey;
    private JCheckBox cbFilter;
    private JComboBox cbLevel;
    private JTable tableOutput;

    private Control control;
    private DefaultTableModel logcatModel;

    private String[] logLevel = {"VERBOSE", "DEBUG", "INFO", "WARN", "ERROR"};
    private String[] columnName = {"级别", "时间", "进程", "标签", "文本"};
    private String[] startTexts = {"开始","停止"};

    private boolean isStartStatus = false;
    private boolean isFilterStatus = false;

    public MainFrame(){
        initData();
        initUI();
        setAction();
    }

    private void initUI(){
        setTitle("Android Logcat v1.0");
        getContentPane().setLayout(new BorderLayout(0, 0));
        panelTop = new JPanel();
        panelButton = new JPanel();
        panelOutput = new JPanel();

        btnStart = new JButton(startTexts[0]);
        btnSave = new JButton("保存");
        btnClean = new JButton("清理");

        labelPackage = new JLabel("包名：");
        labelTag = new JLabel("标签：");
        labelKey = new JLabel("关键字：");

        textPackage = new JTextField();
        textTag = new JTextField();
        textKey = new JTextField();

        cbFilter = new JCheckBox("过滤");

        cbLevel = new JComboBox(logLevel);
        tableOutput = new JTable(logcatModel);
        //上部Panel
        setHorizontalLayout(panelTop);
        panelTop.setBorder(new EmptyBorder(10,10,10,10));
        setVerticalLayout(panelButton);
        panelButton.setBorder(new EmptyBorder(0,10,0,0));
        panelButton.add(btnStart);
        panelButton.add(btnSave);

        panelTop.add(labelPackage);
        panelTop.add(textPackage);
        panelTop.add(Box.createHorizontalStrut(20));
        panelTop.add(labelTag);
        panelTop.add(textTag);
        panelTop.add(Box.createHorizontalStrut(20));
        panelTop.add(labelKey);
        panelTop.add(textKey);
        panelTop.add(Box.createHorizontalStrut(5));
        panelTop.add(cbFilter);
        panelTop.add(Box.createHorizontalStrut(10));
        panelTop.add(cbLevel);
        panelTop.add(Box.createHorizontalStrut(30));
        panelTop.add(btnStart);
        panelTop.add(Box.createHorizontalStrut(5));
        panelTop.add(btnClean);
        panelTop.add(Box.createHorizontalStrut(5));
        panelTop.add(btnSave);
        getContentPane().add(panelTop,BorderLayout.NORTH);

        //中间的输出区
        panelOutput.setLayout(new BorderLayout(0, 0));
        panelOutput.setBorder(new EmptyBorder(10,10,10,10));
        tableOutput.getColumnModel().getColumn(0).setPreferredWidth(35);
        tableOutput.getColumnModel().getColumn(1).setPreferredWidth(160);
        tableOutput.getColumnModel().getColumn(2).setPreferredWidth(50);
        tableOutput.getColumnModel().getColumn(3).setPreferredWidth(140);
        tableOutput.getColumnModel().getColumn(4).setPreferredWidth(800);
        tableOutput.setDefaultRenderer(Object.class,new TableCellTextAreaRenderer());
        tableOutput.setShowVerticalLines(true);//列格显示设定
        tableOutput.setShowHorizontalLines(false);//行格显示确定
        tableOutput.setRowSelectionAllowed(true);
        panelOutput.add(new JScrollPane(tableOutput),BorderLayout.CENTER);
        getContentPane().add(panelOutput,BorderLayout.CENTER);
        setSize(900,600);
    }

    private void initData(){
        control = new Control(this);
        logcatModel = new DefaultTableModel(null,columnName);
    }

    private void setAction(){
        /**
         * 开始和停止
         */
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isStartStatus = !isStartStatus;
                btnStart.setText(isStartStatus ? startTexts[1]:startTexts[0]);
                if (isStartStatus){
                    //运行
                    control.startLogcat();
                    btnSave.setEnabled(false);
                }else {
                    //停止
                    control.stopLogcat();
                    btnSave.setEnabled(true);
                }
            }
        });

        /**
         * 保存
         */
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.getName().endsWith(".txt") && f.isFile()){
                            return true;
                        }else {
                            return false;
                        }
                    }

                    @Override
                    public String getDescription() {
                        return "文本文件（*.txt）";
                    }
                });
                int status = fileChooser.showSaveDialog(null);
                if (status == JFileChooser.APPROVE_OPTION){
                    System.out.println(fileChooser.getSelectedFile().getName());
                }
            }
        });

        /**
         * 清除
         */
        btnClean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                control.setStop(true);
                logcatModel.setRowCount(0);
                control.cleanLogcat();
                control.setStop(false);
            }
        });

        /**
         * 开启过滤
         */
        cbFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isFilterStatus = cbFilter.isSelected();
                if (isFilterStatus){
                    textPackage.setEnabled(false);
                    textTag.setEnabled(false);
                    textKey.setEnabled(false);
                    textPackage.setBackground(Color.lightGray);
                    textTag.setBackground(Color.lightGray);
                    textKey.setBackground(Color.lightGray);
                    control.setFilter(textPackage.getText().trim(),textTag.getText().trim(),textKey.getText().trim());
                }else {
                    textPackage.setEnabled(true);
                    textTag.setEnabled(true);
                    textKey.setEnabled(true);
                    textPackage.setBackground(Color.white);
                    textTag.setBackground(Color.white);
                    textKey.setBackground(Color.white);
                    control.setFilter("","","");
                }
            }
        });

        /**
         * 级别
         */
        cbLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                control.setLevel(cbLevel.getSelectedIndex());
            }
        });
    }

    protected void updateTable(LogCat logCat){
        String[] row = {logCat.getLevel(),logCat.getTime(),logCat.getPid(),logCat.getTag(),logCat.getText()};
        logcatModel.addRow(row);
        Rectangle rect = tableOutput.getCellRect(tableOutput.getRowCount()-1, 0, true);
        tableOutput.scrollRectToVisible(rect);
    }

    private void setVerticalLayout(JPanel panel){
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
    }

    private void setHorizontalLayout(JPanel panel){
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
    }

    class TableCellTextAreaRenderer extends JTextArea implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(control.getColorByRow(row));
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    protected boolean isStartStatus() {
        return isStartStatus;
    }
}
