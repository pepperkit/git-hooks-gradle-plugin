# Git Hooks Gradle Plugin
[![Java CI with Gradle](https://github.com/pepperkit/git-hooks-gradle-plugin/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/pepperkit/git-hooks-gradle-plugin/actions/workflows/build.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=pepperkit_git-hooks-gradle-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=pepperkit_git-hooks-gradle-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=pepperkit_git-hooks-gradle-plugin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=pepperkit_git-hooks-gradle-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=pepperkit_git-hooks-gradle-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=pepperkit_git-hooks-gradle-plugin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=pepperkit_git-hooks-gradle-plugin&metric=security_rating)](https://sonarcloud.io/dashboard?id=pepperkit_git-hooks-gradle-plugin)

Gradle plugin for easy git hooks configuration.

## Usage
Add the plugin into your `build.gradle`, configure the hooks, and optionally configure the build task to be dependant
initGitHooks task, to install the hooks each time the project is rebuild.

The example with *pre-commit* and *pre-push* hooks configured, will look like this:
```groovy
plugins {
    id 'io.github.pepperkit.git-hooks-gradle-plugin' version '1.0.0'
}

apply plugin: 'java'
apply plugin: 'io.github.pepperkit.git-hooks-gradle-plugin'

compileJava {
    // Initialize git hooks each time the project is compiled 
    dependsOn initGitHooks
}

gitHooksGradlePlugin {
    hooks = [
            // Run checkstyle plugin before each commit attempt
            'pre-commit' : 'gradle -q checkstyleMain',
            // Run unit tests before each push attempt
            'pre-push' : 'gradle -q test'
    ]
}

```

Hook's content is any command line script, which is considered successful if exit code is equal to `0`, and not otherwise.
If execution of the script is successful, git action will be proceeded, if not - it will be cancelled.

## Project's structure
```
└── src
    ├── main                # code of the plugin
    ├── test                # unit tests
    └── system-test         # system tests
        └── resources       # system tests scenarios and pre-configured gradle files needed for the tests
```

More about pepperkit projects could be found on its website: https://pepperkit.github.io/
