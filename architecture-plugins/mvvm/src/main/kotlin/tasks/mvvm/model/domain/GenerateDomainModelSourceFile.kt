package tasks.mvvm.model.domain

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
import MvvmArchPlugin.Companion.projectDir
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import tasks.OptionTask
import utils.TaskUtil.addSuperIfNullable
import utils.TaskUtil.getExtension
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File
import java.io.Serializable

abstract class GenerateDomainModelSourceFile : OptionTask() {
    @TaskAction
    fun action() {
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

        val domainModelsPackageName = "$modifiedPackage.domainModels"
        val domainModelClassName = "${mvvmSubPath.makeGoodName()}Model"
        projectDir?.writeModelClass(
            packageName = domainModelsPackageName,
            className = domainModelClassName,
        )
    }

    private fun File.writeModelClass(
        packageName: String,
        className: String,
        isNullable: Boolean = false,
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
                                .addParameter("id", Int::class.asTypeName().copy(nullable = isNullable))
                                .addParameter("name", String::class.asTypeName().copy(nullable = isNullable))
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("id", Int::class.asTypeName().copy(nullable = isNullable))
                                .initializer("id")
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder("name", String::class.asTypeName().copy(nullable = isNullable))
                                .initializer("name")
                                .build(),
                        ).addSuperIfNullable<Serializable>(isNullable)
                        .build(),
                ).build()
        fileSpec.writeTo(this)
    }

    companion object {
        fun Project.registerTaskGenerateDomainModels(): TaskProvider<GenerateDomainModelSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_DOMAIN_MODELS, GenerateDomainModelSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)

                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_DOMAIN_MODELS_DESCRIPTION
            }
    }
}
