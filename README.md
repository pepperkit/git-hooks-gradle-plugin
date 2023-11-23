# git-hooks-gradle-plugin
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
