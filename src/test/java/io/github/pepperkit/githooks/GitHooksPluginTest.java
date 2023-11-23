/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.GradleScriptException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class GitHooksPluginTest {

    GitHooksManager gitHooksManagerMock;

    GitHooksPlugin plugin;

    @BeforeEach
    void beforeEach() {
        gitHooksManagerMock = mock(GitHooksManager.class);
        plugin = new GitHooksPlugin();
        plugin.gitHooksManager = gitHooksManagerMock;
    }

    @Test
    void executesNothingIfHooksAreNotProvided() throws GradleScriptException, IOException {
        plugin.initGitHooks(null, null);

        verify(gitHooksManagerMock, times(0)).checkProvidedHookNamesCorrectness(any());
        verify(gitHooksManagerMock, times(0)).checkGitHooksDirAndCreateIfMissing();
        verify(gitHooksManagerMock, times(0)).createHook(any(), any());
    }

    @Test
    void createsCorrectHooks() throws GradleScriptException, IOException {
        Map<String, String> hooks = new HashMap<>();
        hooks.put("pre-commit", "mvn -B checkstyle:checkstyle");
        hooks.put("pre-push", "mvn -B verify");

        plugin.initGitHooks(null, hooks);

        verify(gitHooksManagerMock, times(1)).checkProvidedHookNamesCorrectness(hooks);
        verify(gitHooksManagerMock, times(1)).checkGitHooksDirAndCreateIfMissing();
        verify(gitHooksManagerMock, times(1))
                .createHook("pre-commit", hooks.get("pre-commit"));
        verify(gitHooksManagerMock, times(1))
                .createHook("pre-push", hooks.get("pre-push"));
    }

    @Test
    void initThrowsGradleScriptExceptionIfCreatingOfHookFails() throws GradleScriptException, IOException {
        Map<String, String> hooks = new HashMap<>();
        hooks.put("pre-commit", "mvn -B checkstyle:checkstyle");
        hooks.put("pre-push", "mvn -B verify");

        doThrow(new IOException()).when(gitHooksManagerMock)
                .createHook("pre-push", hooks.get("pre-push"));

        GradleScriptException excThrown =
                assertThrows(GradleScriptException.class, () -> plugin.initGitHooks(null, hooks));
        assertThat(excThrown.getMessage()).contains("pre-push");
    }
}
