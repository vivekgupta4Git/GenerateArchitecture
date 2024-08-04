package tasks

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
import MvvmArchPlugin.Companion.projectDir
import MvvmArchPlugin.Companion.projectPath
import MvvmArchPlugin.Companion.useKotlin
import architecture.AndroidExtension
import extension.MvvmConfigurationExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByName
import utils.TaskUtil.getPackageName
import java.io.File

abstract class GetProjectPackage : DefaultTask() {
    private val androidExtension = project.extensions.getByName<AndroidExtension>("android")

    @TaskAction
    fun action() {
        // get the main Source set
        val mainSourceSet = project.layout.projectDirectory.dir("src/main")

        // check for main Source set
        if (!mainSourceSet.asFile.exists()) {
            throw Throwable("This plugin requires mainSourceSet (src/main)")
        }

        // getting kotlin or java source set
        projectPath =
            if (mainSourceSet.dir("kotlin").asFile.exists() && useKotlin) {
                mainSourceSet.dir("kotlin").asFile.path
            } else {
                mainSourceSet.dir("java").asFile.path
            }

        projectDir = File(projectPath)
        packageName = mvvmSubPath.getPackageName(androidExtension)
    }

    companion object {
        fun Project.registerTaskGetProjectPackage(): TaskProvider<GetProjectPackage> {
            val mvvmConfigurationExtension =
                this.extensions.create(
                    MvvmPluginConstant.EXTENSION_NAME,
                    MvvmConfigurationExtension::class.java,
                )
            return this.tasks.register(
                MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE,
                GetProjectPackage::class.java,
            ) {
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE_DESCRIPTION
                mvvmConfigurationExtension.model {
                    name.convention("model")
                    insideDirectory.convention("")
                }
                mvvmConfigurationExtension.viewModel {
                    name.convention("viewModel")
                    insideDirectory.convention("")
                }
                mvvmConfigurationExtension.view {
                    name.convention("view")
                    insideDirectory.convention("")
                }
            }
        }
    }
}
