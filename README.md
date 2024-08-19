# Mvvm Plugin - Architecture Plugin

## Overview

Welcome to the **Mvvm Plugin**, part of the **Architecture Plugin** group. 
This Gradle plugin automates the generation of source files following the MVVM (Model-View-ViewModel) architecture pattern, 
streamlining your development process by creating the necessary boilerplate code for you. 
It is designed to enhance productivity and enforce a clean architecture by generating models, entities, network models, mappers, data sources (remote and local), 
repositories, and ViewModels.

## Features

- **Automatic Code Generation**: Generates essential components for MVVM architecture, including Models, Entities, Network Models, and Mappers.
- **Data Layer Creation**: Automatically creates Data Sources (Remote and Local) and a Repository to manage data flow.
- **ViewModel Generation**: Generates ViewModel classes tailored to your architecture.
- **Customizable Configuration**: Configure paths, namespaces, and other settings to suit your project needs.

## MVVM Architecture Benefits

One of the core principles of the MVVM (Model-View-ViewModel) architecture is the separation of concerns. This separation allows different layers of the application (Model, ViewModel, and View) to evolve independently without affecting each other. **This plugin generates code that enables you to refactor a layer without impacting the source code of other layers.**

This capability ensures that your application remains modular, maintainable, and easier to scale over time.


## Installation

Add the following to your `build.gradle` (Project-level) file:

```groovy
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "io.github.vivekgupta4git:mvvm-arch:<latest-version>"
    }
}
```
To use the Mvvm Plugin in your project, add the following to your `build.gradle` (Module-level) file:
```groovy
plugins {
    id 'io.github.vivekgupta4git.mvvm-arch' version '<plugin-version>'
}
```
Replace <plugin-version> with the latest version of the plugin available on the Gradle Plugin Portal.

## Configuration
You can configure the plugin using the configureMvvm { } extension block in your build.gradle file:
```groovy
configureMvvm {
    model{
        name = "domain"
    }
    view{
        name = "Ui"     //no support yet
    }
    viewModel{
        name = "viewModels"
    }
}
```
## Example Usage
Once the plugin is configured, simply run the Gradle task to generate the MVVM components:
```bash 
./gradlew createMvvm --feature <domain-name>
```
There are options to configure the generated source files. 
For details on the available options and how to configure them, 
use the following command to get help on the `createMvvm` task:
```bash
./gradlew help --task createMvvm
```
## Available Tasks

The following tasks are available for the Architecture Plugin:

### Architecture Plugin tasks

- **`createModels`**: Generates all source files related to the Model part of MVVM architecture.

- **`createMvvm`**: Creates the directory structure according to the MVVM Architecture.

- **`createViewModel`**: Generates ViewModel files for your application.

- **`generateDao`**: Generates Data Access Object (DAO) for the database.

- **`generateDomainModel`**: Generates domain model source files.

- **`generateEntityModel`**: Generates entity model source files.

- **`generateLocalDataSource`**: Generates local data source files.

- **`generateMapper`**: Generates mappers between domain, entity, and network models.

- **`generateNetworkModel`**: Generates network model source files.

- **`generateRemoteDataSource`**: Generates remote data source files.

- **`generateRepository`**: Generates repository implementation for the repository interface.

- **`generateRepositoryInterface`**: Generates repository interface source files.

- **`generateRestApi`**: Generates REST API source files.

- **`getProjectPackage`**: Sets the project's package name to the build service, which can be used by other tasks (specific to Android projects).

## Running Tasks
You can run individual tasks to generate specific source code files. For example, to generate a domain model for a feature named "asthma," use:
```bash
./gradlew generateDomainModel --feature=asthma 
```
Task Dependencies
Some tasks depend on other tasks to generate the required source code. For example, the generateMapper task requires the following tasks to be completed first:
- **`generateDomainModel`**: Generates domain model source files.
- **`generateEntityModel`**: Generates entity model source files.
- **`generateNetworkModel`**: Generates network model source files.

Therefore, running the generateMapper task will result in the generation of these source files, along with the mapper source files

## How to Get a List of Tasks
To view the tasks available from the Architecture Plugin and other plugins, run:
```bash
./gradlew tasks
````
If you want to see the all the tasks used by this plugin you can use following command
```bash
./gradlew tasks --group="Architecture Plugin"
```

## Future Enhancements

- In future updates, the Mvvm Plugin will include the ability to generate source files for the View Layers. 
  This enhancement will enable the plugin to fully automate the creation of the MVVM architecture 
  by including View Layer components alongside the existing Data Layer and ViewModel components.

- In upcoming versions, the Mvvm Plugin will include tasks to generate test cases for the MVVM components. 
  These tasks will help you automatically create unit and integration tests for the generated Models, ViewModels,
  Repositories, and other components, ensuring better test coverage and code quality.

