/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * Manages all the work with git hooks.
 */
public class GitHooksManager {

    private static final Path GIT_PATH = Paths.get(".git");

    private static final Path GIT_HOOKS_PATH = Paths.get(".git", "hooks");

    private static final String SHEBANG = "#!/bin/sh";

    static final Set<String> GIT_HOOKS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "applypatch-msg",
            "commit-msg",
            "fsmonitor-watchman",
            "post-update",
            "pre-applypatch",
            "pre-commit",
            "pre-merge-commit",
            "pre-push",
            "pre-rebase",
            "pre-receive",
            "prepare-commit-msg",
            "push-to-checkout",
            "update"
    )));

    private static final Set<PosixFilePermission> HOOK_FILE_PERMISSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
            )));

    private final Logger logger;

    /**
     * Creates GitHooksManager with the provided plugin. Mojo is used to obtain the correct logger.
     * @param logger logger
     */
    public GitHooksManager(Logger logger) {
        this.logger = logger;
    }

    /**
     * Checks that provided hook names are valid git hook names.
     * @param hooks map of hookName -> hookValue
     * @throws IllegalStateException if one of the hook names is not a valid git hook name
     */
    void checkProvidedHookNamesCorrectness(Map<String, String> hooks) {
        for (Map.Entry<String, String> entry : hooks.entrySet()) {
            if (!GIT_HOOKS.contains(entry.getKey())) {
                throw new IllegalStateException(
                        "`" + entry.getKey() + "` is not a git hook. Available hooks are: " + GIT_HOOKS);
            }
        }
    }

    /**
     * Checks that git hooks directory exists, and creates it if it doesn't.
     * @throws IllegalStateException if git repository was not initialized
     *                               or there's an error on creating git hooks directory
     */
    void checkGitHooksDirAndCreateIfMissing() {
        if (!Files.exists(GIT_PATH)) {
            throw new IllegalStateException("It seems that it's not a git repository. " +
                    "Plugin goals should be executed from the root of the project.");
        }

        if (!Files.exists(GIT_HOOKS_PATH)) {
            try {
                Files.createDirectories(GIT_HOOKS_PATH);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create directory " + GIT_HOOKS_PATH, e);
            }
        }
    }

    /**
     * Returns the list of currently installed hooks.
     * @return the list of existing hook files
     */
    List<File> getExistingHookFiles() {
        return GIT_HOOKS.stream()
                .map(this::getHookPath)
                .filter(h -> Files.exists(Paths.get(h)))
                .map(File::new)
                .collect(Collectors.toList());
    }

    /**
     * Writes hook file with the specified name and value.
     * @param hookName hook's name
     * @param hookValue hook file's content to write
     * @throws IOException if an error occurs on trying to write the file
     */
    void createHook(String hookName, String hookValue) throws IOException {
        String hookPath = getHookPath(hookName);
        String fullHookValue = SHEBANG + "\n" + hookValue.replaceAll("[ ]{2,}", "");

        Optional<String> existingHookValue = readHook(hookName);
        if (existingHookValue.isPresent() && existingHookValue.get().equals(fullHookValue)) {
            logger.info("The hook `" + hookName + "` has not changed, skipping");

        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(hookPath))) {
                logger.info("Writing `" + hookName + "` hook");
                writer.write(fullHookValue);

                Path hookFilePath = Paths.get(hookPath);
                if (hookFilePath.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                    Set<PosixFilePermission> currentPermissions = Files.getPosixFilePermissions(hookFilePath);
                    if (!currentPermissions.containsAll(HOOK_FILE_PERMISSIONS)) {
                        Files.setPosixFilePermissions(hookFilePath, HOOK_FILE_PERMISSIONS);
                    }
                }
            }
        }
    }

    private String getHookPath(String hookName) {
        return GIT_HOOKS_PATH + "/" + hookName;
    }

    Optional<String> readHook(String hookName) throws IOException {
        Path hookFilePath = Paths.get(getHookPath(hookName));
        if (!Files.exists(hookFilePath)) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(hookFilePath)));
    }
}
