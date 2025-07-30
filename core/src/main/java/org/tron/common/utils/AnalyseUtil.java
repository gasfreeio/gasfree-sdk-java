package org.tron.common.utils;

public class AnalyseUtil {
    private static Analyser instance;

    public interface Analyser {
        void log(Throwable e);
    }

    private AnalyseUtil(){}

    public static void init(Analyser analyser) {
        instance = analyser;
    }

    public static void log(Throwable e) {
        if (instance != null) {
            instance.log(e);
        }
    }
}
