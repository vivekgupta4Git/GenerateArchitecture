package tasks.mvvm.model.database

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
import MvvmArchPlugin.Companion.projectDir
import androidx.room.Dao
import androidx.room.Query
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.flow.Flow
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.capitalizeFirstChar
import utils.TaskUtil.getExtension
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateDaoSourceFile : OptionTask() {
    @TaskAction
    fun action() {
        // get mvvm Extension
        val extension = getExtension(project)

        // model extension
        val modelExtension = extension.model
        val modifiedPackage =
            modelExtension
                .insideDirectory
                .get()
                .modifyPackageName(
                    packageName,
                    modelExtension.name.get(),
                )

        // write entity model
        val entityPackageName = "$modifiedPackage.entities"
        val entityName = "${mvvmSubPath.makeGoodName()}Entity"

        // write dao
        val daoPackageName = "$modifiedPackage.dao"
        val daoName = "${mvvmSubPath.makeGoodName()}Dao"
        projectDir?.writeDao(
            packageName = daoPackageName,
            daoName = daoName,
            entityDependency = DependencyClass(entityPackageName, entityName),
        )
    }

    private fun File.writeDao(
        packageName: String,
        daoName: String,
        entityDependency: DependencyClass,
    ) {
        val response =
            Flow::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(entityDependency.packageName, entityDependency.className),
                ),
            )
        val fileSpec =
            FileSpec
                .builder(packageName, daoName)
                .addType(
                    TypeSpec
                        .interfaceBuilder(daoName)
                        .addAnnotation(AnnotationSpec.builder(Dao::class).build())
                        .addFunction(
                            FunSpec
                                .builder("getAll${mvvmSubPath.makeGoodName()}")
                                .addModifiers(KModifier.ABSTRACT)
                                .addAnnotation(
                                    AnnotationSpec
                                        .builder(Query::class)
                                        .addMember(
                                            "%S",
                                            "SELECT * FROM ${entityDependency.className.capitalizeFirstChar()}",
                                        ).build(),
                                ).returns(response)
                                .build(),
                        ).build(),
                ).build()
        fileSpec.writeTo(this)
    }

    companion object {
        fun Project.registerTaskGenerateDao(): TaskProvider<GenerateDaoSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_DAO, GenerateDaoSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_DAO_DESCRIPTION
            }
    }
}
