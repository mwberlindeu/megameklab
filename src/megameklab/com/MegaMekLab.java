<<<<<<< HEAD
/*
 * MegaMekLab - Copyright (C) 2008
 *
 * Original author - jtighe (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package megameklab.com;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import megamek.MegaMek;
import megamek.common.QuirksHandler;
import megamek.common.logging.DefaultMmLogger;
import megamek.common.logging.LogConfig;
import megamek.common.logging.LogLevel;
import megamek.common.logging.MMLogger;
import megamek.common.preference.PreferenceManager;
import megameklab.com.ui.Mek.MainUI;

//MWBerlin: potentially relevant for large wet navy vessels - first priority (construction)

public class MegaMekLab {
    public static final String VERSION = "0.45.2-SNAPSHOT";

    private static MMLogger logger = null;

    public static void main(String[] args) {
        final String METHOD_NAME = "main(String[])";
        
    	System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name","MegaMekLab");

        String logFileName = "./logs/megameklablog.txt";
        Locale.setDefault(Locale.US);

        //Taharqa: I am not sure why this is here, so I am commenting it
        //out for awhile because I suspect it might be responsible for the
        //partial unit.cache problem in MHQ.
        //new File("./data/mechfiles/units.cache").delete();

        boolean logs = true;
        boolean vehicle = false;
        boolean battlearmor = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-vehicle")) {
                vehicle = true;
            }

            if (arg.equalsIgnoreCase("-battlearmor")) {
                battlearmor = true;
            }

            if (arg.equalsIgnoreCase("-nolog")) {
                logs = false;
            }
        }

        setupLogging(logs, logFileName);
        showInfo();
        
        if (vehicle) {
            Runtime runtime = Runtime.getRuntime();

            getLogger().log(MegaMekLab.class, METHOD_NAME, LogLevel.INFO,
                            "Memory Allocated [" +
                            (runtime.maxMemory() / 1000) + "]");
            // Need at least 200m to run MegaMekLab
            if (runtime.maxMemory() < 200000000) {
                try {
                    String[] call =
                        { "java", "-Xmx256m", "-splash:data/images/splash/megameklabsplashvehicle.jpg", "-jar", "MegaMekLab.jar", "-vehicle" };

                    if (!logs) {
                        call = new String[]
                            { "java", "-Xmx256m", "-splash:data/images/splash/megameklabsplashvehicle.jpg", "-jar", "MegaMekLab.jar", "-vehicle", "-nolog" };
                    }
                    runtime.exec(call);
                    System.exit(0);
                } catch (Exception ex) {
                    getLogger().error(MegaMekLab.class, METHOD_NAME, ex);
                }
            }

            new megameklab.com.ui.Vehicle.MainUI();
        } else if (battlearmor) {
            Runtime runtime = Runtime.getRuntime();

            getLogger().log(MegaMekLab.class, METHOD_NAME, LogLevel.INFO,
                            "Memory Allocated [" +
                            (runtime.maxMemory() / 1000) + "]");
            // Need at least 200m to run MegaMekLab
            if (runtime.maxMemory() < 200000000) {
                try {
                    String[] call =
                        { "java", "-Xmx256m", "-splash:data/images/splash/megameklabsplashbattlearmor.jpg", "-jar", "MegaMekLab.jar", "-battlearmor" };

                    if (!logs) {
                        call = new String[]
                            { "java", "-Xmx256m", "-splash:data/images/splash/megameklabsplashbattlearmor.jpg", "-jar", "MegaMekLab.jar", "-battlearmor", "-nolog" };
                    }
                    runtime.exec(call);
                    System.exit(0);
                } catch (Exception ex) {
                    getLogger().error(MegaMekLab.class, METHOD_NAME, ex);
                }
            }

            new megameklab.com.ui.BattleArmor.MainUI();
        } else {

            Runtime runtime = Runtime.getRuntime();

            getLogger().log(MegaMekLab.class, METHOD_NAME, LogLevel.INFO,
                            "Memory Allocated [" +
                            (runtime.maxMemory() / 1000) + "]");
            // Need at least 200m to run MegaMekLab
            if (runtime.maxMemory() < 200000000) {
                try {

                    String[] call =
                        { "java", "-Xmx256m", "-jar", "MegaMekLab.jar" };

                    if (!logs) {
                        call = new String[]
                            { "java", "-Xmx256m", "-jar", "MegaMekLab.jar", "-nolog" };
                    }
                    runtime.exec(call);
                    System.exit(0);
                } catch (Exception ex) {
                    getLogger().error(MegaMekLab.class, METHOD_NAME, ex);
                }
            }
            try {
                // Needed for record sheet printing, and also displayed in unit preview.
                QuirksHandler.initQuirksList();
            } catch (IOException e) {
                // File is probably missing.
                getLogger().log(MegaMekLab.class, METHOD_NAME, LogLevel.INFO,
                        "Could not load quirks file.");
            }
            new MainUI();
        }
    }

    private static void setupLogging(final boolean logs,
                                     final String logFileName) {
        if (logs) {
            try {
                File logPath = new File("./logs/");
                if (!logPath.exists()) {
                    logPath.mkdir();
                }
                MegaMek.resetLogFile(logFileName);
                PrintStream ps =
                        new PrintStream(
                                new BufferedOutputStream(
                                        new FileOutputStream(logFileName,
                                                             true),
                                        64));
                System.setOut(ps);
                System.setErr(ps);
            } catch (Exception ex) {
                System.err.println("Unable to redirect output");
            }
        } else {
            LogConfig.getInstance().disableAll();
        }
    }

    public static MMLogger getLogger() {
        if (null == logger) {
            logger = DefaultMmLogger.getInstance();
        }
        return logger;
    }
    
    /**
     * Prints some information about MegaMekLab. Used in logfiles to figure out the
     * JVM and version of MegaMekLab.
     */
    private static void showInfo() {
        final String METHOD_NAME = "showInfo";
        final long TIMESTAMP = new File(PreferenceManager
                .getClientPreferences().getLogDirectory()
                + File.separator
                + "timestamp").lastModified();
        // echo some useful stuff
        String msg = "Starting MegaMekLab v" + VERSION + " ..."; //$NON-NLS-1$ //$NON-NLS-2$
        if (TIMESTAMP > 0) {
            msg += "\n\tCompiled on " + new Date(TIMESTAMP).toString(); //$NON-NLS-1$
        }
        msg += "\n\tToday is " + new Date().toString(); //$NON-NLS-1$
        msg += "\n\tJava vendor " + System.getProperty("java.vendor"); //$NON-NLS-1$ //$NON-NLS-2$
        msg += "\n\tJava version " + System.getProperty("java.version"); //$NON-NLS-1$ //$NON-NLS-2$
        msg += "\n\tPlatform " //$NON-NLS-1$
               + System.getProperty("os.name") //$NON-NLS-1$
               + " " //$NON-NLS-1$
               + System.getProperty("os.version") //$NON-NLS-1$
               + " (" //$NON-NLS-1$
               + System.getProperty("os.arch") //$NON-NLS-1$
               + ")"; //$NON-NLS-1$
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
        msg += "\n\tTotal memory available to MegaMek: " + NumberFormat.getInstance().format(maxMemory) + " kB"; //$NON-NLS-1$ //$NON-NLS-2$
        getLogger().log(MegaMekLab.class, METHOD_NAME, LogLevel.INFO, msg);
    }
=======
/*
 * MegaMekLab - Copyright (C) 2008
 *
 * Original author - jtighe (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package megameklab.com;

import megamek.MegaMek;
import megamek.common.Configuration;
import megamek.common.EquipmentType;
import megamek.common.MechSummaryCache;
import megamek.common.QuirksHandler;
import megamek.common.preference.PreferenceManager;
import megameklab.com.ui.StartupGUI;
import megameklab.com.util.CConfig;
import megameklab.com.util.UnitUtil;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MegaMekLab {
    public static void main(String[] args) {
    	System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name","MegaMekLab");
        redirectOutput();
        // Register any fonts in the fonts directory
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<Font> fontList = new ArrayList<>();
        collectFontsFromDir(Configuration.fontsDir(), fontList);
        for (Font font : fontList) {
            ge.registerFont(font);
        }
        startup();
    }

    private static void redirectOutput() {
        try {
            System.out.println("Redirecting output to megameklablog.txt");
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            final String logFilename = "logs" + File.separator + "megameklablog.txt";
            MegaMek.resetLogFile(logFilename);
            PrintStream ps = new PrintStream(
                    new BufferedOutputStream(
                            new FileOutputStream(logFilename,
                                                 true),
                            64));
            System.setOut(ps);
            System.setErr(ps);
        } catch (Exception e) {
            LogManager.getLogger().error("Unable to redirect output to megameklablog.txt", e);
        }
    }

    /**
     * Recursively search a directory and attempt to create a truetype font from
     * every file with the ttf suffix
     *
     * @param dir  The directory to search
     * @param list The list to add fonts to as they are created
     */
    private static void collectFontsFromDir(File dir, List<Font> list) {
        File[] files = dir.listFiles();
        if (null != files) {
            for (File f : files) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    collectFontsFromDir(f, list);
                } else if (f.getName().toLowerCase().endsWith(".ttf")) {
                    try {
                        list.add(Font.createFont(Font.TRUETYPE_FONT, f));
                    } catch (IOException | FontFormatException ex) {
                        LogManager.getLogger().error("Error creating font from " + f, ex);
                    }
                }
            }
        }
    }

    /**
     * Prints some information about MegaMekLab. Used in logfiles to figure out the
     * JVM and version of MegaMekLab.
     */
    public static void showInfo() {
        final long TIMESTAMP = new File(PreferenceManager.getClientPreferences().getLogDirectory()
                + File.separator + "timestamp").lastModified();
        // echo some useful stuff
        String msg = "Starting MegaMekLab v" + MMLConstants.VERSION;
        if (TIMESTAMP > 0) {
            msg += "\n\tCompiled on " + new Date(TIMESTAMP);
        }
        msg += "\n\tToday is " + LocalDate.now()
                + "\n\tJava Vendor: " + System.getProperty("java.vendor")
                + "\n\tJava Version: " + System.getProperty("java.version")
                + "\n\tPlatform: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")"
                + "\n\tTotal memory available to MegaMek: "
                + NumberFormat.getInstance().format(Runtime.getRuntime().maxMemory()) + " GB";
        LogManager.getLogger().info(msg);
    }
    
    private static void startup() {
        showInfo();
        Locale.setDefault(Locale.US);
        EquipmentType.initializeTypes();
        MechSummaryCache.getInstance();
        try {
            QuirksHandler.initQuirksList();
        } catch (Exception e) {
            LogManager.getLogger().warn("Could not load quirks");
        }
        CConfig.load();
        UnitUtil.loadFonts();

        // Add additional themes
        UIManager.installLookAndFeel("Flat Light", "com.formdev.flatlaf.FlatLightLaf");
        UIManager.installLookAndFeel("Flat IntelliJ", "com.formdev.flatlaf.FlatIntelliJLaf");
        UIManager.installLookAndFeel("Flat Dark", "com.formdev.flatlaf.FlatDarkLaf");
        UIManager.installLookAndFeel("Flat Darcula", "com.formdev.flatlaf.FlatDarculaLaf");

        setLookAndFeel();
        //create a start up frame and display it
        StartupGUI sud = new StartupGUI();
        sud.setVisible(true);
    }
    
    private static void setLookAndFeel() {
        try {
            String plaf = CConfig.getParam(CConfig.CONFIG_PLAF, UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(plaf);
        } catch (Exception e) {
            LogManager.getLogger().error(e);
       }
    }
    
    /**
     * Helper function that calculates the maximum screen width available locally.
     * @return Maximum screen width.
     */
    public static double calculateMaxScreenWidth() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        double maxWidth = 0;
        for (GraphicsDevice g : gs) {
            Rectangle b = g.getDefaultConfiguration().getBounds();
            if (b.getWidth() > maxWidth) {   // Update the max size found on this monitor
                maxWidth = b.getWidth();
            }
        }
        
        return maxWidth;
    }
>>>>>>> origin/master
}