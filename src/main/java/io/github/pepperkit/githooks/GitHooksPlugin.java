/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.gradle.api.GradleScriptException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * The main Plugin, installs configured git hooks.
 */
public class GitHooksPlugin implements Plugin<Project> {

    private final Logger logger = Logging.getLogger(GitHooksPlugin.class);

    GitHooksManager gitHooksManager = new GitHooksManager(logger);

    @Override
    public void apply(Project project) {
        GitHooksPluginExtension extension = project.getExtensions()
                .create("gitHooksGradlePlugin", GitHooksPluginExtension.class);

        Task initGitHooks = project.getTasks().register("initGitHooks").get();
        initGitHooks.doLast(task -> initGitHooks(task, extension.getHooks().get()));
    }

    protected void initGitHooks(Task task, Map<String, String> hooks) {
        logger.info("Git Hooks Plugin is launched");
        logger.debug("Hooks: {}", hooks);

        List<File> existingHookFiles = gitHooksManager.getExistingHookFiles();
        if (hooks == null || hooks.isEmpty()) {
            existingHookFiles.forEach(File::delete);
            return;
        }

        gitHooksManager.checkProvidedHookNamesCorrectness(hooks);
        gitHooksManager.checkGitHooksDirAndCreateIfMissing();

        String hookToBeCreated = null;
        try {
            for (Map.Entry<String, String> hook : hooks.entrySet()) {
                hookToBeCreated = hook.getKey();
                gitHooksManager.createHook(hookToBeCreated, hook.getValue());
            }
            logger.info("Git Hooks are successfully initialized");

        } catch (IOException e) {
            throw new GradleScriptException("Cannot write hook `" + hookToBeCreated + "`", e);
        }
    }
}
