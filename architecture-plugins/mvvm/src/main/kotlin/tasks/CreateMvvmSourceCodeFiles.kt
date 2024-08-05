package tasks

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.useKotlin
import MvvmPluginConstant
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import tasks.mvvm.model.CreateModels

/**
 *@author Vivek Gupta on
 */
abstract class CreateMvvmSourceCodeFiles : DefaultTask() {
    @Option(
        option = "sub-path",
        description = """Generates mvvm architecture inside the sub-path.
    This plugin generates stuffs under main source set i.e main/packageName/,
    so if sub-path is given then main/packageName/subPath/""",
    )
    fun setSubPath(subPath: String) {
        mvvmSubPath = subPath
    }

    @Option(
        option = "preferKotlin",
        description = """ This plugin generates code assuming you have kotlin sourceSets
    but if you have java sourceSets and you want to generate structure in the java sourceSets you can set this flag to false by
    using option --no-preferKotlin""",
    )
    fun setPreferKotlin(prefer: Boolean) {
        useKotlin = prefer
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
        fun Project.registerCreateMvvmSourceFiles(): TaskProvider<CreateMvvmSourceCodeFiles> =
            this.tasks.register(MvvmPluginConstant.TASK_CREATE_MVVM_SOURCE_CODES, CreateMvvmSourceCodeFiles::class.java) {
                // this task needs project's package name and other stuffs to generate the code
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_CREATE_MVVM_SOURCE_CODES_DESCRIPTION
            }
    }
}

data class DependencyClass(
    val packageName: String,
    val className: String,
)
