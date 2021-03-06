/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.launcher.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.launcher.gui.javafx.VersionItem;
import org.terasology.launcher.packages.Package;
import org.terasology.launcher.packages.PackageManager;

import java.io.IOException;

public final class DeleteTask extends Task<Void> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteTask.class);

    private final PackageManager packageManager;
    private final VersionItem target;
    private Runnable cleanup;

    public DeleteTask(PackageManager packageManager, VersionItem target) {
        this.packageManager = packageManager;
        this.target = target;
    }

    @Override
    protected Void call() {
        final Package targetPkg = target.getLinkedPackage();
        try {
            packageManager.remove(targetPkg);
        } catch (IOException e) {
            logger.error("Failed to remove package: {}-{}",
                    targetPkg.getId(), targetPkg.getVersion(), e);
        }
        return null;
    }

    @Override
    protected void succeeded() {
        target.installedProperty().set(false);
    }

    @Override
    protected void done() {
        if (cleanup != null) {
            Platform.runLater(cleanup);
        }
    }

    public void onDone(Runnable cleanupCallback) {
        cleanup = cleanupCallback;
    }
}
