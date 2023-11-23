/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks.steps;

import java.io.IOException;

import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenStepDefinitions extends BasePluginSysTest {

    @Then("it throws not a git repository error")
    public void throwsNotGitRepoError() {
        assertThat(cmdResult.getStdout())
                .contains("FAILED");
    }

    @Then("these hooks are installed to git correctly")
    public void hooksAreInstalledCorrectly() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("cat", ".git/hooks/pre-commit");
        assertThat(cmdResult.getStdout())
                .contains("pre-commit hook is invoked")
                .doesNotContain("pre-push hook is invoked");

        cmdResult = container.execInContainer("cat", ".git/hooks/pre-push");
        assertThat(cmdResult.getStdout())
                .contains("pre-push hook is invoked")
                .doesNotContain("pre-commit hook is invoked");
    }

    @Then("previously added hooks are deleted")
    public void previouslyAddedHooksAreDeleted() throws IOException, InterruptedException {
        cmdResult = container.execInContainer("cat", ".git/hooks/pre-commit");
        assertThat(cmdResult.getStdout())
                .doesNotContainIgnoringCase("echo \"pre-commit hook is invoked\"");

        cmdResult = container.execInContainer("cat", ".git/hooks/pre-push");
        assertThat(cmdResult.getStdout())
                .doesNotContainIgnoringCase("echo \"pre-push hook is invoked\"");
    }

    @Then("it throws hook is not installed error")
    public void throwsHookIsNotInstalledError() {
        assertThat(cmdResult.getStdout())
                .contains("ERROR")
                .contains("The specified hook")
                .contains("is not installed.");
    }
}
