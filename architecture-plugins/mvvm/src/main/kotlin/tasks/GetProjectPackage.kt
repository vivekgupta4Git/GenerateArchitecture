package tasks

import architecture.AndroidExtension
import extension.MvvmConfigurationExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByName
import service.ProjectPathService
import utils.TaskUtil.getPackageName

abstract class GetProjectPackage : OptionTask() {

    @TaskAction
    override fun action() {
        super.action()
        // get the main Source set
        val mainSourceSet = project.layout.projectDirectory.dir("src/main")

        // check for main Source set
        if (!mainSourceSet.asFile.exists()) {
            throw Throwable("This plugin requires mainSourceSet (src/main)")
        }

        // getting kotlin or java source set
        val projectPath =
            if (mainSourceSet.dir("kotlin").asFile.exists() &&
                projectPathService
                    .get()
                    .parameters.useKotlin
                    .get()
            ) {
                mainSourceSet.dir("kotlin").asFile.path
            } else {
                mainSourceSet.dir("java").asFile.path
            }
        val packageName =
            projectPathService
                .get()
                .parameters.mvvmSubPath
                .get()
                .getPackageName(androidExtension)

        with(projectPathService.get().parameters) {
            this.projectPath.set(projectPath)
            this.packageName.set(packageName)
        }
    }

    companion object {
        fun Project.registerTaskGetProjectPackage(serviceProvider: Provider<ProjectPathService>): TaskProvider<GetProjectPackage> {
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
                // connection with service
                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
        }
    }
}
