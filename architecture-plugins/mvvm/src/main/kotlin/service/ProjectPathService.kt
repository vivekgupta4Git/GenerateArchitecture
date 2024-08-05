package service

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class ProjectPathService :
    BuildService<ProjectPathService.ProjectPathParams>,
    AutoCloseable {
    /**
     * An instance of the factory can be injected
     * into a task, plugin or other object by annotating
     * a public constructor or property getter method with
     * javax.inject.Inject. It is also available
     * via Project.getObjects().
     *
     *
     */
    interface ProjectPathParams : BuildServiceParameters {
        val projectPath: Property<String>
        val mvvmSubPath: Property<String>
        val packageName: Property<String>
        val useKotlin: Property<Boolean>
    }

    override fun close() {
        // do nothing
    }
}
