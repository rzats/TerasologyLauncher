/*
 * Copyright 2016 MovingBlocks
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

package org.terasology.launcher.updater;

import com.vdurmont.semver4j.Semver;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.launcher.util.BundleUtils;
import org.terasology.launcher.version.TerasologyLauncherVersionInfo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public final class LauncherUpdater {

    private static final Logger logger = LoggerFactory.getLogger(LauncherUpdater.class);

    private final Semver currentVersion;

    public LauncherUpdater(TerasologyLauncherVersionInfo currentVersionInfo) {
        //TODO: might not be valid semver, catch or use Try<..>
        currentVersion = new Semver(currentVersionInfo.getVersion());
    }

    //TODO: catch invalid semver and return Try<..> or Option<..> instead
    private Semver versionOf(GHRelease release) {
        return new Semver(release.getTagName().replaceAll("^v(.*)$", "$1"));
    }

    /**
     * This method indicates if a new launcher version is available.
     * <br>
     * Compares the current launcher version number to the upstream version number if an internet connection is available.
     *
     * @return a {@link GHRelease} if an update is available, null otherwise
     */
    //TODO: return Option<GitHubRelease>
    public GHRelease updateAvailable() {
        //TODO: only check of both version are defined and valid semver?
        try {
            final GitHub github = GitHub.connectAnonymously();
            final GHRepository repository = github.getRepository("MovingBlocks/TerasologyLauncher");
            final GHRelease latestRelease = repository.getLatestRelease();
            final Semver latestVersion = versionOf(latestRelease);

            if (latestVersion.isGreaterThan(currentVersion)) {
                return latestRelease;
            }
        } catch (IOException e) {
            logger.warn("Update check failed: {}", e.getMessage());
        }
        return null;
    }

    public boolean showUpdateDialog(Stage parentStage, final GHRelease release) {
        FutureTask<Boolean> dialog = getUpdateDialog(parentStage, release);

        Platform.runLater(dialog);
        boolean result = false;
        try {
            result = dialog.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Uh oh, something went wrong with the update dialog!", e);
        }
        return result;
    }

    private FutureTask<Boolean> getUpdateDialog(Stage parentStage, GHRelease release) {
        final String infoText = "  " +
                BundleUtils.getLabel("message_update_current") +
                "  " +
                currentVersion.getValue() +
                "  \n" +
                "  " +
                BundleUtils.getLabel("message_update_latest") +
                "  " +
                versionOf(release).getValue() +
                "  ";

        return new FutureTask<>(() -> {
            Parent root = BundleUtils.getFXMLLoader("update_dialog").load();
            ((TextArea) root.lookup("#infoTextArea")).setText(infoText);
            ((TextArea) root.lookup("#changelogTextArea")).setText(release.getBody());

            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(BundleUtils.getLabel("message_update_launcher_title"));
            alert.setHeaderText(BundleUtils.getLabel("message_update_launcher"));
            alert.getDialogPane().setContent(root);
            alert.initOwner(parentStage);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setResizable(true);

            return alert.showAndWait()
                    .filter(response -> response == ButtonType.YES)
                    .isPresent();
        });
    }
}
