/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks.steps;

import java.io.IOException;

import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenStepDefinitions extends BasePluginSysTest {

    @When("initHooks goal of the plugin is launched with hooks presented in plugin's configuration")
    public void initGoalIsLaunchedWithHooks() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("gradle", "-i", "--no-daemon", "--settings-file",
                "pre_commit_push_hooks-settings.gradle", "initGitHooks");
    }

    @When("initHooks goal of the plugin is launched with hooks deleted from plugin's configuration")
    @When("initHooks goal of the plugin is launched with another plugin's configuration")
    public void hooksDeleted() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("gradle", "-i", "--no-daemon", "--settings-file",
                "no_hooks-settings.gradle", "initGitHooks");
        assertThat(cmdResult.getStdout())
                .contains("BUILD SUCCESS");
    }

    @When("initHooks goal of the plugin is launched with the same plugin's configuration")
    public void initWithHooksConfigured() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("gradle", "-i", "--no-daemon", "--settings-file",
                "pre_commit_push_hooks-settings.gradle", "initGitHooks");
        assertThat(cmdResult.getStdout())
                .contains("BUILD SUCCESS");

        cmdResult = container.execInContainer("cat", ".git/hooks/pre-commit");
        assertThat(cmdResult.getStdout())
                .contains("pre-commit hook is invoked");
    }
}
