package service

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class ProjectPathService :
    BuildService<ProjectPathService.ProjectPathParams>,
    AutoCloseable {
    interface ProjectPathParams : BuildServiceParameters {
        val projectPath: Property<String>
        val mvvmSubPath: Property<String>
        val packageName: Property<String>
        val useKotlin: Property<Boolean>
        val domainName: Property<String>
        val namespace : Property<String>
        val autoNamespace : Property<Boolean>
    }

    override fun close() {
        // do nothing
    }
}
