/*
 * Copyright (C) 2023 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.githooks;

import org.gradle.api.provider.MapProperty;

/**
 * Git Hooks plugin extension for configuring git hooks.
 */
public interface GitHooksPluginExtension {

    MapProperty<String, String> getHooks();
}
