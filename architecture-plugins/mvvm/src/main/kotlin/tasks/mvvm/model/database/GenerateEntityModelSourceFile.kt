package tasks.mvvm.model.database

import MvvmPluginConstant
import androidx.room.Entity
import androidx.room.PrimaryKey
import architecture.Model
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import extension.MvvmConfigurationExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import service.ProjectPathService
import tasks.OptionTask
import utils.TaskUtil.getExtension
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateEntityModelSourceFile : OptionTask() {
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

        val modelExtension = project.extensions.getByType<Model>()
        val modifiedPackage =
            modelExtension
                .insideDirectory
                .modifyPackageName(
                    packageName,
                    modelExtension.name,
                )
        val explicitPath = projectPathService
            .get()
            .parameters.explicitPath
            .get()

        val entityPackageName = explicitPath.ifEmpty {  "$modifiedPackage.entities"}
        val entityName = "${mvvmSubPath.makeGoodName()}Entity"
        projectDir.writeEntityClass(
            packageName = entityPackageName,
            entityName = entityName,
        )
    }

    private fun File.writeEntityClass(
        packageName: String,
        entityName: String,
    ) {
        val fileSpec =
            FileSpec
                .builder(packageName, entityName)
                .addType(
                    TypeSpec
                        .classBuilder(entityName)
                        .addModifiers(KModifier.DATA)
                        .addAnnotation(
                            AnnotationSpec
                                .builder(Entity::class)
                                //  .addMember("tableName = %S", "${entityName.substringBefore("Entity").lowercase()}s")
                                .build(),
                        ).primaryConstructor(
                            FunSpec
                                .constructorBuilder()
                                .addParameter(
                                    ParameterSpec
                                        .builder("id", String::class)
                                        .addAnnotation(
                                            AnnotationSpec.builder(PrimaryKey::class).build(),
                                        ).build(),
                                ).addParameter("name", String::class.asClassName().copy(nullable = true))
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("name", String::class.asClassName().copy(nullable = true))
                                .initializer("name")
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("id", String::class)
                                .initializer("id")
                                .build(),
                        ).build(),
                ).build()
        fileSpec.writeTo(this)
    }

    companion object {
        fun Project.registerTaskGenerateEntityModels(
            serviceProvider: Provider<ProjectPathService>,
        ): TaskProvider<GenerateEntityModelSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS, GenerateEntityModelSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}
