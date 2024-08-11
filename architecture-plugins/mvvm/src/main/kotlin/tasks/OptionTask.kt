package tasks

import com.android.build.gradle.BaseExtension
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import service.ProjectPathService
import utils.TaskUtil.makeGoodName

/**
 * Base class for tasks that support options.
 * @see <a href="https://docs.gradle.org/current/userguide/command_line_interface.html#sec:command_line_option_syntax">Gradle command line option documentation</a>
 */
abstract class OptionTask : DefaultTask() {
    @Internal
    val androidExtension = project.extensions.getByName("android") as BaseExtension

    @get:Internal
    abstract val projectPathService: Property<ProjectPathService>

    @Option(
        option = "feature",
        description = "The domain name of the feature to be generated having a mvvm architecture",
    )
    fun setDomainName(subPath: String) {
        projectPathService
            .get()
            .parameters.feature
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
        description = "This plugin auto detect namespace for android projects but if you prefer to " +
                "set it explicitly you can use this option by using option --no-auto-namespace and explicitly set the namespace using option --namespace"
    )
    fun setAutoNamespace(detectNamespace: Boolean) {
        projectPathService
            .get()
            .parameters.autoNamespace
            .set(detectNamespace)
    }

    @Option(
        option = "preferKotlin",
        description = """ This plugin generates code assuming you have kotlin sourceSets
    but if you have java sourceSets and you want to generate structure in the java sourceSets you can set this flag to false by
    using option --no-preferKotlin""",
    )
    fun setPreferKotlin(prefer: Boolean) {
        projectPathService
            .get()
            .parameters.useKotlin
            .set(prefer)
    }

    @Option(
        option = "explicitPath",
        description = "set explicit path for generated source files, default is empty. If you set the path," +
                "all generated source code file will use this path and ignore any namespace (or extension set by the plugin)" +
                " whether it was set explicitly or plugin automatically detected it"
    )
    fun setExplicitPath(path: String) {
        projectPathService
            .get()
            .parameters.explicitPath
            .set(path.replace('/','.'))
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

        if(autoNamespace) {
            projectPathService.get()
                .parameters.namespace
                .set(androidExtension.namespace ?: "")
        }

       if(namespace.isBlank() && !autoNamespace )
           throw Throwable("Namespace is missing,use --namespace to set project's package name")



    }
}
