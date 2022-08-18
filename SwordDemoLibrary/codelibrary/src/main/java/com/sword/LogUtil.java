package com.sword;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class LogUtil {
  private static final String TAG = "Sword";
  private static final String LOG_SWITCH_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LoggerSwitch";
  private static final File LOG_SWITCH_FILE = new File(LOG_SWITCH_DIR
      + File.separator
      + Encryption.md5(LOG_SWITCH_DIR + File.separator + "logger_switch"));
  
  private static final File logFile = new File(SwordApplication.getGlobalContext().getExternalFilesDir(null).getAbsoluteFile(), "log.log");
  @SuppressLint("SimpleDateFormat")
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String packageName = SwordApplication.getGlobalContext().getPackageName(); 
  
  private static boolean isDebug() {
    return LOG_SWITCH_FILE.exists();
  }
  
  static {
    if (!Objects.requireNonNull(logFile.getParentFile()).exists()) {
      logFile.getParentFile().mkdirs();
    }
    if (!logFile.exists()) {
      try {
        logFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    Log.d(TAG, "logFilePath: " + logFile.getAbsolutePath());
  }

  public static void debug(String msg) {
    debug("", msg);
  }

  public static void debug(String tag, String msg) {
    String t = TAG;
    if (!TextUtils.isEmpty(tag)) {
      t = TAG + "_" + tag;
    }
    Log.d(t, msg);

    writeToLogFile(sdf.format(new Date()) + " " + packageName + "D/" + t + ": " + msg);
  }
    
  public static void error(String msg) {
    error("", msg);
  }
  
  public static void error(String tag, String msg) {

    String t = TAG;
    if (!TextUtils.isEmpty(tag)) {
      t = TAG + "_" + tag;
    }
    Log.e(t, msg);
    writeToLogFile(sdf.format(new Date()) + " " + packageName + "E/" + t + ": " + msg);
  }

  public static void warn(String msg) {
    warn("", msg);
  }
  
  public static void warn(String tag, String msg) {
    String t = TAG;
    if (!TextUtils.isEmpty(tag)) {
      t = TAG + "_" + tag;
    }
    Log.w(t, msg);
    writeToLogFile(sdf.format(new Date()) + " " + packageName + "W/" + t + ": " + msg);
  }
  
  
  private static void writeToLogFile(String content) {
    Log.d(TAG, "write to log File: " + content);
    try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true)))) {
      writer.write(content);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}