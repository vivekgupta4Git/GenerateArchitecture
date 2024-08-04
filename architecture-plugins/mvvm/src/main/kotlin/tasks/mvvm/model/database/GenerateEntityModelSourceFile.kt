package tasks.mvvm.model.database

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
import MvvmArchPlugin.Companion.projectDir
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import utils.TaskUtil.getExtension
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateEntityModelSourceFile : DefaultTask() {
    @TaskAction
    fun action() {
        // get mvvm Extension
        val extension = getExtension(project)
        val modelExtension = extension.model
        val modifiedPackage =
            modelExtension
                .insideDirectory
                .get()
                .modifyPackageName(
                    packageName,
                    modelExtension.name.get(),
                )
        val entityPackageName = "$modifiedPackage.entities"
        val entityName = "${mvvmSubPath.makeGoodName()}Entity"
        projectDir?.writeEntityClass(
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
                        .addAnnotation(AnnotationSpec.builder(Entity::class).build())
                        .primaryConstructor(
                            FunSpec
                                .constructorBuilder()
                                .addParameter(
                                    ParameterSpec
                                        .builder("id", Int::class)
                                        .addAnnotation(
                                            AnnotationSpec.builder(PrimaryKey::class).build(),
                                        ).build(),
                                ).addParameter("name", String::class.asTypeName().copy(nullable = true))
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("name", String::class.asTypeName().copy(nullable = true))
                                .initializer("name")
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("id", Int::class)
                                .initializer("id")
                                .build(),
                        ).build(),
                ).build()
        fileSpec.writeTo(this)
    }

    companion object {
        fun Project.registerTaskGenerateEntityModels(): TaskProvider<GenerateEntityModelSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS, GenerateEntityModelSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS_DESCRIPTION
            }
    }
}
