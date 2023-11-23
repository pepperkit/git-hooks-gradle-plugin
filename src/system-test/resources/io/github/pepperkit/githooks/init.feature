Feature: Initialize git hooks via initHooks goal

  Scenario: Installs configured hooks
    Given there's a gradle project with git-hooks plugin configured
    And git repository is set up for the project
    When initHooks goal of the plugin is launched with hooks presented in plugin's configuration
    Then these hooks are installed to git correctly

  Scenario: Throws error if git repository is not set up
    Given there's a gradle project with git-hooks plugin configured
    And git repository is not set up for the project
    When initHooks goal of the plugin is launched with hooks presented in plugin's configuration
    Then it throws not a git repository error

  Scenario: Removes hooks if they were deleted from the configuration
    Given there's a gradle project with git-hooks plugin configured
    And git repository is set up for the project
    And initHooks goal was launched before with hooks presented in configuration
    When initHooks goal of the plugin is launched with hooks deleted from plugin's configuration
    Then previously added hooks are deleted
