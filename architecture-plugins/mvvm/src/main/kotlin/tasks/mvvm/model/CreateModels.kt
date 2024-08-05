package tasks.mvvm.model

import MvvmPluginConstant
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByName
import tasks.OptionTask
import tasks.mvvm.model.database.GenerateDaoSourceFile
import tasks.mvvm.model.database.GenerateEntityModelSourceFile
import tasks.mvvm.model.domain.GenerateDomainModelSourceFile
import tasks.mvvm.model.network.GenerateNetworkModelSourceFile
import tasks.mvvm.model.network.GenerateRestApiSourceFile

abstract class CreateModels : OptionTask() {
    /**
     * Task will generate following structure
     * model >
     *         dao
     *         dataSources
     *         domainModels
     *         entities
     *         networkModels
     *         restApi
     *         repository
     *
     */
    @TaskAction
    fun action() {
        project.tasks.getByName(MvvmPluginConstant.TASK_GENERATE_DOMAIN_MODELS, GenerateDomainModelSourceFile::class).action()
        project.tasks.getByName(MvvmPluginConstant.TASK_GENERATE_NETWORK_MODELS, GenerateNetworkModelSourceFile::class).action()
        project.tasks.getByName(MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS, GenerateEntityModelSourceFile::class).action()
        project.tasks.getByName(MvvmPluginConstant.TASK_GENERATE_REST_API, GenerateRestApiSourceFile::class).action()
        project.tasks.getByName(MvvmPluginConstant.TASK_GENERATE_DAO, GenerateDaoSourceFile::class).action()
    }

    companion object {
        fun Project.registerTaskCreateModels(): TaskProvider<CreateModels> =
            this.tasks.register(
                MvvmPluginConstant.TASK_CREATE_MODELS,
                CreateModels::class.java,
            ) {
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_CREATE_MODELS_DESCRIPTION
            }
    }
}
