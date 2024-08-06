package tasks

import MvvmPluginConstant
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import service.ProjectPathService
import tasks.mvvm.model.CreateModels
import utils.TaskUtil.makeGoodName

/**
 *@author Vivek Gupta on
 */
abstract class CreateMvvmSourceCodeFiles : DefaultTask() {
    @get:Internal
    abstract val projectPathService: Property<ProjectPathService>

    @Option(
        option = "sub-path",
        description = """Generates mvvm architecture inside the sub-path.
    This plugin generates stuffs under main source set i.e main/packageName/,
    so if sub-path is given then main/packageName/subPath/""",
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

    @TaskAction
    fun action() {
        /**
         * model >
         *         domainModels
         *         dataSources
         *         entities
         *         repositories
         *         networkModels
         **/
        project.tasks.getByName(MvvmPluginConstant.TASK_CREATE_MODELS, CreateModels::class).action()
    }

    companion object {
        fun Project.registerCreateMvvmSourceFiles(serviceProvider: Provider<ProjectPathService>): TaskProvider<CreateMvvmSourceCodeFiles> =
            this.tasks.register(MvvmPluginConstant.TASK_CREATE_MVVM_SOURCE_CODES, CreateMvvmSourceCodeFiles::class.java) {
                // this task needs project's package name and other stuffs to generate the code
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_CREATE_MVVM_SOURCE_CODES_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}

data class DependencyClass(
    val packageName: String,
    val className: String,
)
