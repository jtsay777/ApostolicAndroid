package com.jway.apostolic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.Toast;

/**
 * Utility functions for debug.
 *
 * @author umbalaconmeogia
 */
public class DebugUtil {

  private static final String TAG = "DebugUtil";

  /**
   * For the log methods to be involved or not.
   */
  public static boolean printLog = true;

  /**
   * Toast a message.
   * @param context
   * @param message
   */
  public static void toast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  /**
   * Display a message on a dialog.
   * @param context
   * @param message Message to be shown.
   */
  public static void alert(Context context, String message) {
    alert(context, null, message);
  }

  /**
   * Display a message on a dialog.
   * @param context
   * @param title Dialog title.
   * @param message Message to be shown.
   */
  public static void alert(Context context, String title, String message) {
    ErrorUtil.alert(context, title, message, "OK");
  }

  /**
   * Log method calling of a method that call this m().
   * @param params The method calling parameters.
   */
  public static void m(Object... params) {
    if (printLog) {
      CallerInfo callerInfo = new CallerInfo(2, params);
      Log.println(Log.DEBUG,
          callerInfo.className,
          callerInfo.methodName + "(" + toString(callerInfo.params) + ")");
    }
  }

  /**
   * A proxy for Log.d api that kills log messages in release build.
   * @param tag
   * @param msg
   */
  public static void d(String tag, String msg) {
    if (printLog) {
      Log.println(Log.DEBUG, tag, msg);
    }
  }

  /**
   * Wrapper for Log.d method.
   * <p>
   * The Log.d() tag parameter is set to the caller class name.
   * @param msg
   */
  public static void d(String msg) {
    if (printLog) {
      log(Log.DEBUG, msg);
    }
  }

  public static void i(String msg) {
    if (printLog) {
      log(Log.INFO, msg);
    }
  }

  public static void e(String msg) {
    if (printLog) {
      log(Log.ERROR, msg);
    }
  }

  private static void log(int level, String msg) {
    if (printLog) {
      CallerInfo callerInfo = new CallerInfo(3, msg);
      Log.println(level, callerInfo.className, msg);
    }
  }

  private static class CallerInfo {
    public String className;
    public String methodName;
    public List<String> params;

    /**
     * Get the caller info in the stack trace.
     * @param stackTraceLevel
     * @param params
     */
    public CallerInfo(int stackTraceLevel, Object... params) {
      // Array of parameters converted to strings.
      this.params = new ArrayList<String>();
      for (Object param: params) {
        this.params.add(param == null ? ((String) param) : param.toString());
      }

      // Get caller method.
      // stackTraceLevel + 2: plus 2 for getStackTrace() and CallerInfo().
      StackTraceElement caller =
        Thread.currentThread().getStackTrace()[stackTraceLevel + 2];
      String[] classNames = caller.getClassName().split("\\.");
      className = classNames[classNames.length - 1];
      methodName = caller.getMethodName();
    }
  }

  /**
   * Convert an array to string.
   * @param objects
   * @return
   */
  public static String arrayToString(Object[] objects) {
    String result = null;
    if (objects != null) {
      result = "{" + toString(objects) + "}";
    }
    return result;
  }

  /**
   * Convert a float array to string.
   * @param values
   * @return
   */
  public static String arrayToString(float[] values) {
    String result = null;
    if (values != null) {
      result = "{" + toString(values) + "}";
    }
    return result;
  }

  /**
   * Convert a integer array to string.
   * @param values
   * @return
   */
  public static String arrayToString(int[] values) {
    String result = null;
    if (values != null) {
      result = "{" + toString(values) + "}";
    }
    return result;
  }

  /**
   * Convert float array to string, separated by comma.
   * @param values
   * @return
   */
  public static String toString(float[] values) {
    String result = null;
    if (values != null) {
      ArrayList<String> list = new ArrayList<String>();
      for (float value: values) {
        list.add(Float.toString(value));
      }
      result = StringUtil.join(list);
    }
    return result;
  }

  /**
   * Convert integer array to string, separated by comma.
   * @param values
   * @return
   */
  public static String toString(int[] values) {
    String result = null;
    if (values != null) {
      ArrayList<String> list = new ArrayList<String>();
      for (int value: values) {
        list.add(Integer.toString(value));
      }
      result = StringUtil.join(list);
    }
    return result;
  }

  /**
   * Convert object array to string, separated by comma.
   * @param objects
   * @return
   */
  public static String toString(Object[] objects) {
    String result = null;
    if (objects != null) {
      ArrayList<String> list = new ArrayList<String>();
      for (Object object: objects) {
        list.add(object == null ? ((String) object) : object.toString());
      }
      result = StringUtil.join(list);
    }
    return result;
  }

  /**
   * Convert object list to string, separated by comma.
   * @param objects
   * @return
   */
  public static String toString(List<?> objects) {
    return toString(objects.toArray());
  }

  /**
   * Get the MeasureSpec mode name.
   * @param measureSpec Width or height MeasureSpec.
   * @return Measure spec mode name as AT_MOST, EXACTLY or UNSPECIFIED.
   */
  public static String getMeasureSpecMode(int measureSpec) {
    int mode = MeasureSpec.getMode(measureSpec);
    String name = "--unknown--";
    if (mode == MeasureSpec.AT_MOST) {
      name = "AT_MOST";
    } else if (mode == MeasureSpec.EXACTLY) {
      name = "EXACTLY";
    } else if (mode == MeasureSpec.UNSPECIFIED) {
      name = "UNSPECIFIED";
    }
    return name;
  }

  /**
   * Log free memory.
   * @param messageFormat The message format,
   *     should contain %d as a place holder for the free memory.
   *     See {@link String#format(String, Object...)}
   */
  public static long freeMem(String messageFormat) {
    long freeMemory = Runtime.getRuntime().freeMemory();
    Log.d(TAG, String.format(messageFormat, freeMemory));
    return freeMemory;
  }

  /**
   * Log free memory.
   */
  public static long freeMem() {
    return freeMem("Free memory: %d");
  }

  /**
   * Wrapper for {@link Thread#sleep(long)}.
   * This will throw exception as RuntimeException.
   * @param time The time to sleep in milliseconds.
   */
  public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      throw ErrorUtil.runtimeException(e);
    }
  }
}
