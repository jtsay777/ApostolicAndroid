package com.jway.apostolic;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Utility functions on error.
 *
 * @author umbalaconmeogia
 *
 */
public class ErrorUtil {

  /**
   * Wrap an exception by RuntimeException.
   * <p>
   * The exception is not wrapped if it has already been a RuntimeException.
   * @param e The exception to be wrapped.
   * @return A RuntimeException object.
   */
  public static RuntimeException runtimeException(Exception e) {
    return e instanceof RuntimeException ?
        (RuntimeException) e :
        new RuntimeException("Exception wrapped by RuntimeException", e);
  }

  /**
   * Display a message on a dialog.
   * @param context
   * @param title
   * @param message
   */
  public static void alert(Context context, String title, String message) {
    alert(context, title, message, "OK");
  }

  /**
   * Display a message on a dialog.
   * @param context
   * @param title
   * @param message
   * @param buttonTitle
   */
  public static void alert(Context context, String title, String message, String buttonTitle) {
    new AlertDialog.Builder(context)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(buttonTitle, null)
        .show();
  }

}
