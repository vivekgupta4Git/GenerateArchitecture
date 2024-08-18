package tasks

import extension.MvvmConfigurationExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
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

        val namespace = projectPathService
            .get()
            .parameters.namespace
            .get()
        val domainName = projectPathService
            .get()
            .parameters.domainName
            .get()
        val packageName = "$namespace.${domainName.lowercase()}"

        with(projectPathService.get().parameters) {
            this.projectPath.set(projectPath)
            this.packageName.set(packageName)
        }

    }

    companion object {
        fun Project.registerTaskGetProjectPackage(serviceProvider: Provider<ProjectPathService>): TaskProvider<GetProjectPackage> {

            return this.tasks.register(
                MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE,
                GetProjectPackage::class.java,
            ) {
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE_DESCRIPTION
                // connection with service
                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
        }
    }
}
