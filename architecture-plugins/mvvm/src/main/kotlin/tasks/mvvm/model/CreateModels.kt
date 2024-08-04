package tasks.mvvm.model

import MvvmPluginConstant
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByName
import tasks.mvvm.model.database.GenerateEntityModelSourceFile
import tasks.mvvm.model.domain.GenerateDomainModelSourceFile
import tasks.mvvm.model.network.GenerateNetworkModelSourceFile

abstract class CreateModels : DefaultTask() {
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
    }

    companion object {
        fun Project.registerTaskCreateModels(): TaskProvider<CreateModels> =
            this.tasks.register(
                MvvmPluginConstant.TASK_CREATE_MODELS,
                CreateModels::class.java,
            ) {
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_CREATE_MODELS_DESCRIPTION
            }
    }
}
