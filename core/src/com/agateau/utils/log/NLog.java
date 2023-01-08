/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.utils.log;

import java.util.Vector;

/**
 * A simple logging system
 *
 * <p>Features:
 *
 * <ul>
 *   <li>3 log levels: debug, info, error
 *   <li>Automatically prefix the log message with the name of the calling method
 *   <li>Pluggable "printers" to collect logs
 * </ul>
 */
public class NLog {
    private static final Vector<Printer> sPrinters = new Vector<>();
    private static int sStackDepth = -1;

    public enum Level {
        DEBUG,
        INFO,
        ERROR,
    }

    public interface Printer {
        void print(Level level, String tag, String message);
    }

    public static void d(Object obj, Object... args) {
        print(Level.DEBUG, obj, args);
    }

    public static void i(Object obj, Object... args) {
        print(Level.INFO, obj, args);
    }

    public static void e(Object obj, Object... args) {
        print(Level.ERROR, obj, args);
    }

    public static void backtrace() {
        StackTraceElement[] lst = Thread.currentThread().getStackTrace();
        for (int idx = 2, n = lst.length; idx < n; ++idx) {
            NLog.d("bt: %s", lst[idx]);
        }
    }

    public static void addPrinter(Printer printer) {
        sPrinters.add(printer);
    }

    static synchronized void print(Level level, Object obj, Object... args) {
        if (sStackDepth < 0) {
            initStackDepth();
        }
        print(level, getCallerMethod(), obj, args);
    }

    static synchronized void print(Level level, String tag, Object obj, Object... args) {
        String message;
        if (obj == null) {
            message = "(null)";
        } else {
            String format = obj.toString();
            message = args.length > 0 ? String.format(format, args) : format;
        }
        if (sPrinters.isEmpty()) {
            sPrinters.add(new SystemErrPrinter());
        }
        for (Printer printer : sPrinters) {
            printer.print(level, tag, message);
        }
    }

    private static void initStackDepth() {
        final StackTraceElement[] lst = Thread.currentThread().getStackTrace();
        for (int i = 0, n = lst.length; i < n; ++i) {
            if (lst[i].getMethodName().equals("initStackDepth")) {
                sStackDepth = i;
                return;
            }
        }
    }

    private static String getCallerMethod() {
        final StackTraceElement stackTraceElement =
                Thread.currentThread().getStackTrace()[sStackDepth + 3];
        final String fullClassName = stackTraceElement.getClassName();
        final String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        final String method = stackTraceElement.getMethodName();
        return className + "." + method;
    }
}
