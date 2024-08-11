package tasks.mvvm.model.network

import MvvmPluginConstant
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import service.ProjectPathService
import tasks.OptionTask
import utils.TaskUtil.addSuperIfNullable
import utils.TaskUtil.getExtension
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File
import java.io.Serializable

abstract class GenerateNetworkModelSourceFile : OptionTask() {
    @TaskAction
    override fun action() { super.action()
        val projectPath =
            projectPathService
                .get()
                .parameters.projectPath
                .get()
        val packageName =
            projectPathService
                .get()
                .parameters.packageName
                .get()
        val mvvmSubPath =
            projectPathService
                .get()
                .parameters.feature
                .get()
        val projectDir = File(projectPath)
        // get mvvm Extension
        val extension = getExtension(project)

        // model extension
        val modelExtension = extension.model

        // modify package based on the model extension -inside directory
        val modifiedPackage =
            modelExtension.insideDirectory
                .get()
                .modifyPackageName(
                    packageName,
                    modelExtension.name.get(),
                )

        val networkModelsPackageName = "$modifiedPackage.networkModels"
        val networkModelClassName = "${mvvmSubPath.makeGoodName()}NetworkModel"
        projectDir.writeModelClass(
            packageName = networkModelsPackageName,
            className = networkModelClassName,
        )
    }

    private fun File.writeModelClass(
        packageName: String,
        className: String,
    ) {
        val fileSpec =
            FileSpec
                .builder(packageName, className)
                .addType(
                    TypeSpec
                        .classBuilder(className)
                        .addModifiers(KModifier.DATA)
                        .primaryConstructor(
                            FunSpec
                                .constructorBuilder()
                                .addParameter("id", String::class.asClassName().copy(nullable = true))
                                .addParameter("name", String::class.asClassName().copy(nullable = true))
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("id", String::class.asClassName().copy(nullable = true))
                                .initializer("id")
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("name", String::class.asClassName().copy(nullable = true))
                                .initializer("name")
                                .build(),
                        ).addSuperIfNullable<Serializable>(true)
                        .build(),
                ).build()
        fileSpec.writeTo(this)
    }

    companion object {
        fun Project.registerTaskGenerateNetworkModels(
            serviceProvider: Provider<ProjectPathService>,
        ): TaskProvider<
            GenerateNetworkModelSourceFile,
        > =
            this.tasks.register(
                MvvmPluginConstant.TASK_GENERATE_NETWORK_MODELS,
                GenerateNetworkModelSourceFile::class.java,
            ) {
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_NETWORK_MODELS_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}
