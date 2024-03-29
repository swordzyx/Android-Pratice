package sword;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import sword.logger.SwordLog;

public class CommandExecutor {
  private static String tag = "CommandExecutor";

  public interface Callback {
    void onSuccess(String command, String result);

    void onFailed(String command, String error);
  }

  public static String executeCommand(String command) {
    SwordLog.info(tag, "---------------------- executeCommand " + command + " --------------------------");
    Process process = null;
    BufferedReader reader = null;
    StringBuilder result = new StringBuilder();
    try {
      process = Runtime.getRuntime().exec(command);
      /*writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
      writer.write(command);
      writer.flush();*/
      
      reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        SwordLog.info(tag, "line: " + line);
        result.append(line);
      }
      return result.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return "error: " + e.getMessage();
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
        if (process != null) {
          process.destroy();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      SwordLog.info(tag, "---------------------- end executeCommand --------------------------");
    }
    
  }

  /**
   * 执行指定的 shell 命令
   *
   * @param isRoot  是否使用 Root 权限
   * @param command 要执行的 shell 命令
   */
  public static CommandResult execShellCommand(boolean isRoot, String command) {
    CommandResult result = null;

    Process process = null;
    BufferedWriter writer = null;
    BufferedReader successReader = null;
    BufferedReader errorReader = null;

    StringBuilder errorString = new StringBuilder();
    StringBuilder successString = new StringBuilder();
    try {
      process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");

      writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
      writer.write(command);
      writer.write("\n");
      writer.flush();
      writer.write("exit\n");
      writer.flush();

      successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

      String message = null;
      while ((message = successReader.readLine()) != null) {
        successString.append(message);
      }

      while ((message = errorReader.readLine()) != null) {
        errorString.append(message);
      }

      result = new CommandResult(errorString.toString(), successString.toString());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }

  public static class CommandResult {
    private String errorString;
    private String successString;
    String[] args = new String[5];

    CommandResult(String errorString, String successString) {
      this.errorString = errorString;
      this.successString = successString;
    }

    public String getErrorString() {
      return errorString;
    }

    public String getSuccessString() {
      return successString;
    }
  }
}
