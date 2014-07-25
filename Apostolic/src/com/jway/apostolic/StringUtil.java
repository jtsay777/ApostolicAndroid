package com.jway.apostolic;

import java.util.List;

/**
 * Utility functions on string.
 *
 * @author umbalaconmeogia
 *
 */
public class StringUtil {

  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * Convert a string from camel case to underscore.
   * @param s
   * @return
   */
  public static String camelToUnderscore(String s) {
    // Add space to split camel words.
    s = s.replaceAll(
        String.format("%s|%s|%s",
         "(?<=[A-Z])(?=[A-Z][a-z])",
         "(?<=[^A-Z])(?=[A-Z])",
         "(?<=[A-Za-z])(?=[^A-Za-z])"
        ),
        " "
    );
    // Split string by space.
    String[] words = s.split(" ");
    // Convert words to lowercase.
    for (int i = words.length - 1; i >= 0; i--) {
      words[i] = words[i].toLowerCase();
    }
    // Join by underscore.
    return join(words, "_");
  }

  /**
   * Join a list of strings by comma.
   * @param strings
   * @return
   */
  public static String join(List<String> strings) {
    return join(strings.toArray(new String[0]), ", ");
  }

  /**
   * Join a list of strings by a separator.
   * @param strings
   * @param separator
   * @return
   */
  public static String join(List<String> strings, String separator) {
    return join(strings.toArray(new String[0]), separator);
  }

  /**
   * Join an array of strings by comma.
   * @param strings
   * @return
   */
  public static String join(String[] strings) {
    return join(strings, ", ");
  }

  /**
   * Join an array of strings by a separator.
   * @param strings
   * @param separator
   * @return
   */
  public static String join(String[] strings, String separator) {
    StringBuffer sb = new StringBuffer();
    if (strings.length > 0) {
      sb.append(strings[0]);
      for (int i=1; i < strings.length; i++) {
        sb.append(separator);
        sb.append(strings[i]);
      }
    }
    return sb.toString();
  }
}
