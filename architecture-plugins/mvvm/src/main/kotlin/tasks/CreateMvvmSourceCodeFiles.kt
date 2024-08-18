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
import tasks.mvvm.viewmodel.CreateViewModel
import utils.TaskUtil.makeGoodName

/**
 *@author Vivek Gupta on
 */
abstract class CreateMvvmSourceCodeFiles : OptionTask() {

    @TaskAction
    override fun action() {
        super.action()
        /**
         * model >
         *         domainModels
         *         dataSources
         *         entities
         *         repositories
         *         networkModels
         **/
       project.tasks.getByName(MvvmPluginConstant.TASK_CREATE_MODELS, CreateModels::class).action()
       project.tasks.getByName(MvvmPluginConstant.TASK_CREATE_VIEW_MODEL, CreateViewModel::class).action()
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
