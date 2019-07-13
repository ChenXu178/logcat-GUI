package com.chenxu.logcat;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Control {

    private MainFrame mainFrame;
    private AdbUtils adbUtils;
    private String filterPackage,filterTag,filterKey;
    private static int filterPid = 0;
    private int filterLevel = 0;
    private boolean stop = false;
    private static final int MAX_COUNT = 100000;
    List<LogCat> logCats;

    private static final String[] LEVEL = {"V ","D ","I ","W ","E "};
    private static final String ADB = "adb logcat -v time -s ";
    private static final String SEPARATE = "| ";
    private static final String GREP = "grep ";
    private static final String DOT = "'";
    private static final String PS = "adb shell ps | grep ";

    public Control(MainFrame frame){
        this.mainFrame = frame;
        adbUtils = new AdbUtils(this);
        logCats = new ArrayList<>();
    }

    protected void startLogcat(){
        Utils.print("startLogcat");
        filterPid = 0;
        if (filterPackage != null && !"".equals(filterPackage)){
            String psCommand = PS + filterPackage;
            adbUtils.runPsCommand(psCommand);
            while (adbUtils.isRunPsThread()){
//                Utils.printError("....");
            }//等待子线程回传PID
            Utils.printError("....");
            parseCommandAndStart();
        }else {
            parseCommandAndStart();
        }
    }

    private void parseCommandAndStart(){
        Utils.print("parseCommandAndStart");
        String logcatCommand = ADB;
        if (filterTag != null && !"".equals(filterTag)){
            String[] tags = filterTag.split(" ");
            for (String tag:tags) {
                logcatCommand = logcatCommand + tag + ":" + LEVEL[filterLevel];
                logcatCommand += " ";
            }
        }else {
            logcatCommand = logcatCommand + "*:"+LEVEL[filterLevel];
        }
        if (filterPid >= 1){
            String PID_GREP = "'(\\s*"+filterPid+")'";
            logcatCommand = logcatCommand + SEPARATE + GREP + PID_GREP;
        }else if (filterPid == -1){
            return;
        }
        adbUtils.runLogCatCommand(logcatCommand);
    }

    protected void stopLogcat(){
        Utils.print("stopLogcat");
        adbUtils.stopLogCatCommand();
    }

    protected void setFilter(String filterPackage,String filterTag,String filterKey){
        Utils.print("setFilter filterPackage="+filterPackage+" filterTag="+filterTag+" filterKey="+filterKey);
        this.filterPackage = filterPackage;
        this.filterTag = filterTag;
        this.filterKey = filterKey;
        if (mainFrame.isStartStatus()){
            adbUtils.stopLogCatCommand();
            startLogcat();
        }
    }

    protected void setLevel(int level){
        Utils.print("setLevel level="+level);
        if (filterLevel != level){
            filterLevel = level;
            if (mainFrame.isStartStatus()){
                adbUtils.stopLogCatCommand();
                startLogcat();
            }
        }
    }

    protected void cleanLogcat(){
        logCats.clear();
    }

    protected Color getColorByRow(int row){
        if (logCats.size() > row){
            return logCats.get(row).getColor();
        }
        return Color.BLACK;
    }

    protected void setStop(boolean stop) {
        this.stop = stop;
    }

    protected void setFilterPid(int filterPid) {
        Utils.print("setFilterPid filterPid="+filterPid);
        this.filterPid = filterPid;
    }

    protected void pushLogcat(LogCat logCat){
//        Utils.print("pushLogcat logCat="+logCat.toString());
//        if (logCats.size() > MAX_COUNT){
//            logCats.remove(0);
//        }
        if (!stop){
            logCats.add(logCat);
            mainFrame.updateTable(logCat);
        }
    }

}
