package tasks

import architecture.AndroidExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import service.ProjectPathService
import utils.TaskUtil.makeGoodName

/**
 * Base class for tasks that support options.
 * @see <a href="https://docs.gradle.org/current/userguide/command_line_interface.html#sec:command_line_option_syntax">Gradle command line option documentation</a>
 */
abstract class OptionTask : DefaultTask() {
    @Internal
    val androidExtension = project.extensions.getByName("android") as AppExtension

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
        projectPathService
            .get()
            .parameters.domainName
            .set(subPath.makeGoodName())
    }
    @Option(
        option = "namespace",
        description = "set namespace for generated source files"
    )
    fun setNamespace(namespace: String) {
        projectPathService
            .get()
            .parameters.namespace
            .set(namespace)
    }

    @Option(
        option = "auto-namespace",
        description = "auto detect namespace for android projects, no need to set it explicitly"
    )
    fun setAutoNamespace(detectNamespace: Boolean) {

        projectPathService
            .get()
            .parameters.autoNamespace
            .set(detectNamespace)
    }


    @TaskAction
    open fun action(){

        val namespace = projectPathService
            .get()
            .parameters.namespace
            .get()
        val autoNamespace = projectPathService
            .get()
            .parameters.autoNamespace
            .get()

       if(namespace.isBlank() && !autoNamespace )
           throw Throwable("Namespace is missing")

        if(autoNamespace){
            projectPathService.get()
                .parameters.namespace
                .set(androidExtension.namespace ?: "default")
        }


        println("namespace is ${projectPathService.get().parameters.namespace.get()}")
    }
}
