package com.chenxu.logcat;

import java.awt.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String logcatFilterRegex = "^\\d+-\\d+\\s+\\d+:\\d+:\\d+.\\d+\\s+[VDIWE]/(\\s*\\w*)*\\(\\s+\\d+\\):\\s*.*$";//过滤logcat
    private static final String logcatExtractRegex = "^(\\d+-\\d+\\s+\\d+:\\d+:\\d+.\\d+)\\s+([VDIWE])/((\\s*\\w*)*)\\(\\s+(\\d+)\\):\\s*(.*)$";//提取logcat
    private static final String psExtractRegex = "^\\w+\\s+(\\d+)\\s+\\d+.*$";//提取PS命令中的PID
    static Pattern logcatPattern = Pattern.compile(logcatExtractRegex);
    static Pattern psPattern = Pattern.compile(psExtractRegex);

    private static final boolean DEBUG = true;

    public static void print(String text){
        if (DEBUG){
            System.out.println(text);
        }
    }

    public static void printError(String text){
        if (DEBUG){
            System.err.println(text);
        }
    }

    public static LogCat parseLineLogcat(String line){
        Matcher matcher = logcatPattern.matcher(line);
        if (matcher.find()){
            LogCat logCat = new LogCat();
            logCat.setPid(matcher.group(5).trim());
            logCat.setLevel(matcher.group(2).trim());
            logCat.setTime(matcher.group(1).trim());
            logCat.setTag(matcher.group(3).trim());
            logCat.setText(matcher.group(6).trim());
            logCat.setColor(matchColor(logCat.getLevel()));
            logCat.setOriginal(matcher.group(0));
            return logCat;
        }
        return null;
    }

    public static int parseLinePs(String line){
        Matcher matcher = psPattern.matcher(line);
        if (matcher.find()){
            return Integer.parseInt(matcher.group(1).trim());
        }
        return -1;
    }

    private static Color matchColor(String level){
        if ("E".equals(level)){
            return new Color(255, 20, 1);
        }else if ("W".equals(level)){
            return new Color(255, 189, 0);
        }else if ("I".equals(level)){
            return new Color(0, 219, 44);
        }else if ("D".equals(level)){
            return new Color(0, 170, 250);
        }else {
            return new Color(0,0,0);
        }
    }
}
