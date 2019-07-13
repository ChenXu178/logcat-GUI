package com.chenxu.logcat;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class AdbUtils {
    private Process logcatProcess,psProcess;
    private Thread logcatExecThread,psExecThread;
    private Control control;

    public static final String ADB = "adb logcat";

    private boolean runLogcatThread = false;
    private boolean runPsThread = false;

    public AdbUtils(Control control){
        this.control = control;
    }

    protected void runLogCatCommand(String command){
        Utils.print("runLogCatCommand command="+command);
        try{
            logcatProcess = Runtime.getRuntime().exec(command);
            printLogCatInfo(logcatProcess);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void runPsCommand(String command){
        Utils.print("runPsCommand command="+command);
        try {
            psProcess = Runtime.getRuntime().exec(command);
            printPsInfo(psProcess);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void stopLogCatCommand(){
        try {
            if (logcatProcess != null && logcatProcess.isAlive() && runLogcatThread){
                Utils.print("stopLogcatProcess");
                logcatProcess.destroy();
                runLogcatThread = false;
            }
            if (psProcess != null && psProcess.isAlive() && runPsThread){
                Utils.print("stopPsProcess");
                runPsThread = false;
                psProcess.destroy();
                control.setFilterPid(-1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void printLogCatInfo(Process process){
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        runLogcatThread = true;
        logcatExecThread = new Thread(){
            @Override
            public void run() {
                String tmp1 = null;
                String tmp2 = null;
                try {
                    while (((tmp1 = input.readLine()) != null || (tmp2 = errInput.readLine()) != null) && runLogcatThread){
                        if (tmp1 != null){
                            LogCat logCat = Utils.parseLineLogcat(tmp1);
                            if (logCat != null){
                                control.pushLogcat(logCat);
                            }
                        }
                        if (tmp2 != null){
                            Utils.printError("printLogCatInfo:"+tmp2);
                        }
                    }
                    input.close();
                    errInput.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                runLogcatThread = false;
            }

        };
        logcatExecThread.start();
    }

    private void printPsInfo(Process process){
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        runPsThread = true;
        psExecThread = new Thread(){
            @Override
            public void run() {
                String tmp1 = null;
                String tmp2 = null;
                try {
                    while (((tmp1 = input.readLine()) != null || (tmp2 = errInput.readLine()) != null) && runPsThread){
                        if (tmp1 != null){
                            Utils.print("printPsInfo:"+tmp1);
                            int pid = Utils.parseLinePs(tmp1);
                            control.setFilterPid(pid);
                            input.close();
                            errInput.close();
                            runPsThread = false;
                            return;
                        }
                        if (tmp2 != null){
                            Utils.printError("printPsInfo:"+tmp2);
                            control.setFilterPid(-1);
                            input.close();
                            errInput.close();
                            runPsThread = false;
                            return;
                        }
                    }
                    control.setFilterPid(-1);
                    input.close();
                    errInput.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                runPsThread = false;
            }

        };
        psExecThread.start();
    }

    public boolean isRunPsThread() {
        return runPsThread;
    }
}
