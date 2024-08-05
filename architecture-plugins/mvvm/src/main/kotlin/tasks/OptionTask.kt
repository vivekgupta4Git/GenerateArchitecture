package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option
import service.ProjectPathService

abstract class OptionTask : DefaultTask() {
    @get:Internal
    abstract val projectPathService: Property<ProjectPathService>

    @Option(
        option = "sub-path",
        description = "generate source files under sub-path",
    )
    fun setSubPath(subPath: String) {
        projectPathService
            .get()
            .parameters.mvvmSubPath
            .set(subPath)
    }
}
