package service

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class ProjectPathService :
    BuildService<ProjectPathService.ProjectPathParams>,
    AutoCloseable {
    interface ProjectPathParams : BuildServiceParameters {
        /**
         * project path in the directory structure format
         */
        val projectPath: Property<String>

        /**
         * used to set the domain name.
         */
        val feature: Property<String>

        /**
         * full package name of the project including the domain name and plugin's extension subPath
         */
        val packageName: Property<String>

        /**
         * A flag to indicate whether to use kotlin or java source set
         */
        val useKotlin: Property<Boolean>

        /**
         *  domain name of the project by using [feature] and applying transformations to get Good Name
         */
        val domainName: Property<String>

        /**
         * namespace of the project, user can set it explicitly or it will be auto detected
         */
        val namespace : Property<String>

        /**
         * A flag to detect namespace for android projects, default is true which finds the namespace
         * from AppExtension/LibraryExtension of Android project
         */
        val autoNamespace : Property<Boolean>
    }

    override fun close() {
        // do nothing
    }
}
