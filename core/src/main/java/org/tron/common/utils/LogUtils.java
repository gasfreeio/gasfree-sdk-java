
package org.tron.common.utils;

import android.util.Log;

/**
 * A tool class for log input, which can dynamically modify the output level
 */
public class LogUtils {

    /**
     * log output level  NONE
     */
    public static final int LEVEL_NONE = 0;
    /**
     * log output level  V
     */
    public static final int LEVEL_VERBOSE = 1;
    /**
     * log output level  D
     */
    public static final int LEVEL_DEBUG = 2;
    /**
     * log output level  I
     */
    public static final int LEVEL_INFO = 3;
    /**
     * log output level  W
     */
    public static final int LEVEL_WARN = 4;
    /**
     * log output level  E
     */
    public static final int LEVEL_ERROR = 5;

    /**
     * log output TAG
     */
    private static String mTag = "Tron";

    /**
     * Whether to allow log output
     */
    private static int mDebuggable = LEVEL_NONE;

    /**
     * variable for timing
     */
    private static long mTimestamp = 0;
    /**
     * lock object for writing files
     */
    private static final Object mLogLock = new Object();

    public static void init(int debugLevel) {
        if (debugLevel >= LEVEL_NONE && debugLevel <= LEVEL_ERROR) {
            mDebuggable = debugLevel;
        }
    }

    /**
     * output LOG as level v
     */
    public static void v(String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level v
     */
    public static void v(String mTag, String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level d
     */
    public static void d(String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, "" + msg);

        }
    }

    /**
     * output LOG as level d
     */
    public static void d(String mTag, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, "" + msg);

        }
    }

    /**
     * output LOG as level i
     */
    public static void i(String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level i
     */
    public static void i(String mTag, String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level w
     */
    public static void w(String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level w
     */
    public static void w(String mTag, String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "" + msg);
        }
    }

    /**
     * output Throwable LOG as level w
     */
    public static void w(Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "", tr);
        }
    }

    /**
     * output Throwable & LOG as level w
     */
    public static void w(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(mTag, "" + msg, tr);
        }
    }

    /**
     * output LOG as level e
     */
    public static void e(String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level e
     */
    public static void e(String mTag, String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "" + msg);
        }
    }

    /**
     * output Throwable LOG as level e
     */
    public static void e(Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "", tr);
        }
    }

    /**
     * output Throwable & LOG as level e
     */
    public static void e(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(mTag, "" + msg, tr);
        }
    }
}
