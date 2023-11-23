/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks.steps;

import java.io.IOException;

import io.cucumber.java.en.Given;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenStepDefinitions extends BasePluginSysTest {

    @Given("there's a gradle project with git-hooks plugin configured")
    public void testProjectWithPluginConfigured() {
        // Project is set in the docker image
    }

    @Given("git repository is not set up for the project")
    public void repoIsNotSetUp() throws IOException, InterruptedException {
        container.execInContainer("rm", "-rf", ".git");
    }

    @Given("git repository is set up for the project")
    public void repoIsSetUp() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("git", "init");
    }

    @Given("initHooks goal was launched before with hooks presented in configuration")
    @Given("initHooks goal was launched before with the specified hook presented in configuration")
    public void initWithHooksConfigured() throws IOException, InterruptedException {
        container.execInContainer("gradle", "-i", "--no-daemon", "--settings-file",
                "pre_commit_push_hooks-settings.gradle", "initGitHooks");

        cmdResult = container.execInContainer("cat", ".git/hooks/pre-commit");
        assertThat(cmdResult.getStdout())
                .contains("pre-commit hook is invoked");
    }

    @Given("initHooks goal was launched before with no hooks presented in configuration")
    @Given("initHooks goal was launched before with the specified hook not presented in configuration")
    public void initWithNoHooksConfigured() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("gradle", "-i", "--no-daemon", "--settings-file",
                "no_hooks-settings.gradle", "initGitHooks");
        assertThat(cmdResult.getStdout())
                .contains("BUILD SUCCESS");
    }
}
