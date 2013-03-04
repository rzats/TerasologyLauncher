/*
 * Copyright (c) 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasologylauncher.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasologylauncher.Settings;
import org.terasologylauncher.updater.GameData;
import org.terasologylauncher.util.OperatingSystem;
import org.terasologylauncher.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrBarsack
 * @author Skaldarnar
 */
public class TerasologyStarter {

    private static final Logger logger = LoggerFactory.getLogger(TerasologyStarter.class);

    public static boolean startGame() {
        OperatingSystem os = OperatingSystem.getOS();
        if (os.isWindows()) {
            return startWindows();
        } else if (os.isMac()) {
            return startMac();
        } else if (os.isUnix()) {
            return startLinux();
        } else {
            logger.error("Unknown operating system - cannot start game! {}", os);
        }
        return false;
    }

    private static boolean startLinux() {
        List<String> parameters = new ArrayList<String>();
        parameters.add("java");
        parameters.addAll(Settings.createParameters());
        parameters.add("-jar");
        parameters.add(GameData.getGameJar().getName());
        ProcessBuilder pb = new ProcessBuilder(parameters);
        pb.directory(Utils.getWorkingDirectory());
        try {
            pb.start();
        } catch (IOException e) {
            logger.error("Could not start game!", e);
            return false;
        }
        return true;
    }

    private static boolean startMac() {
        List<String> parameters = new ArrayList<String>();
        parameters.add("java");
        parameters.addAll(Settings.createParameters());
        parameters.add("-jar");
        parameters.add(GameData.getGameJar().getName());
        ProcessBuilder pb = new ProcessBuilder(parameters);
        pb.directory(Utils.getWorkingDirectory());
        try {
            pb.start();
        } catch (IOException e) {
            logger.error("Could not start game!", e);
            return false;
        }
        return true;
    }

    private static boolean startWindows() {
        List<String> parameters = new ArrayList<String>();
        parameters.add("java");
        parameters.addAll(Settings.createParameters());
        parameters.add("-jar");
        parameters.add(GameData.getGameJar().getName());
        ProcessBuilder pb = new ProcessBuilder(parameters);
        pb.directory(Utils.getWorkingDirectory());
        try {
            pb.start();
        } catch (IOException e) {
            logger.error("Could not start game!", e);
            return false;
        }
        return true;
    }
}