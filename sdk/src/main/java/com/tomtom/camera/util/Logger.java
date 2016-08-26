/*
 * Copyright (C) 2012-2016. TomTom International BV (http://tomtom.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.camera.util;

import android.util.Log;

/**
 * Wrapper class , which makes it possible to implement custom logger. If not, there is default
 * android logger
 */
public final class Logger {

    /**
     * This interface provides functions, which logger should implement, in order to have different
     * level of logging
     */
    public interface LoggerInterface {
        void debug(String tag, String message);
        void info(String tag, String message);
        void warning(String tag, String message);
        void error(String tag, String message);
        void exception(Throwable t);
    }

    private static final LoggerInterface DEFAULT = new LoggerInterface() {
        @Override
        public void debug(String tag, String message) {
            Log.d(tag, message);
        }

        @Override
        public void info(String tag, String message) {
            Log.i(tag, message);
        }

        @Override
        public void warning(String tag, String message) {
            Log.w(tag, message);
        }

        @Override
        public void error(String tag, String message) {
            Log.e(tag, message);
        }

        @Override
        public void exception(Throwable t) {
            t.printStackTrace();
        }
    };

    private static LoggerInterface sLoggerInterface = null;


    /**
     * Declares lowest log level which will be handled.
     */
    static LogLevel sLogLevel = LogLevel.DEBUG;

    /**
     * Enum which defines different level of logging
     */
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR, WTF
    }


    /**
     * should be used for debug level of logging
     * @param tag string which helps to distinguish source of log message
     * @param message string which will be displayed in log
     */
    public static void debug(String tag, String message) {
        if(sLoggerInterface != null) {
            sLoggerInterface.debug(tag, message);
        }
        else {
            DEFAULT.debug(tag, message);
        }
    }

    /**
     * should be used for info level of logging
     * @param tag string which helps to distinguish source of log message
     * @param message string which will be displayed in log
     */
    public static void info(String tag, String message) {
        if(sLoggerInterface != null) {
            sLoggerInterface.info(tag, message);
        }
        else {
            DEFAULT.info(tag, message);
        }
    }

    /**
     * should be used for warning level of logging
     * @param tag string which helps to distinguish source of log message
     * @param message string which will be displayed in log
     */
    public static void warning(String tag, String message) {
        if(sLoggerInterface != null) {
            sLoggerInterface.warning(tag, message);
        }
        else {
            DEFAULT.warning(tag, message);
        }
    }

    /**
     * should be used for error level of logging
     * @param tag string which helps to distinguish source of log message
     * @param message string which will be displayed in log
     */
    public static void error(String tag, String message) {
        if(sLoggerInterface != null) {
            sLoggerInterface.error(tag, message);
        }
        else {
            DEFAULT.error(tag, message);
        }
    }

    /**
     * Appends stack trace to log
     * @param throwable object which is thrown, when exception happened
     */
    public static void exception(Throwable throwable) {
        if(sLoggerInterface != null) {
            sLoggerInterface.exception(throwable);
        }
        else {
            DEFAULT.exception(throwable);
        }
    }

    /**
     * Sets custom logger object
     * @param loggerInterface object which is custom implementation of {@link LoggerInterface}
     */
    public static void setLoggerInterface(LoggerInterface loggerInterface) {
        sLoggerInterface = loggerInterface;
    }

    /**
     * Sets logger level for project
     * @param logLevel {@link LogLevel}
     */
    public static final void setLogLevel(LogLevel logLevel) {
        sLogLevel = logLevel;
    }

    /**
     * Returns logger level for project
     * @return  {@link LogLevel}
     */
    public static LogLevel getLogLevel() {
        return sLogLevel;
    }
}
