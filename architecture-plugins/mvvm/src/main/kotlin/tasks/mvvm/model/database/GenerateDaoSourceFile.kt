package tasks.mvvm.model.database

import MvvmPluginConstant
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import architecture.Model
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import extension.MvvmConfigurationExtension
import kotlinx.coroutines.flow.Flow
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import service.ProjectPathService
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.capitalizeFirstChar
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.modifyPackageName
import java.io.File
import kotlin.math.exp

abstract class GenerateDaoSourceFile : OptionTask() {
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

        val projectDir = File(projectPath)
        val domainName =
            projectPathService
                .get()
                .parameters.domainName
                .get()

        // model extension
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
        // write entity model
        val entityPackageName = explicitPath.ifEmpty { "$modifiedPackage.entities" }
        val entityName = "${domainName}Entity"

        // write dao
        val daoPackageName = explicitPath.ifEmpty {"$modifiedPackage.dao"}
        val daoName = "${domainName}Dao"
        projectDir.writeDao(
            packageName = daoPackageName,
            daoName = daoName,
            entityDependency = DependencyClass(entityPackageName, entityName),
            domainName = domainName,
        )
    }

    private fun File.writeDao(
        packageName: String,
        daoName: String,
        entityDependency: DependencyClass,
        domainName: String,
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
                        .addFunction(getAll(domainName, entityDependency, response))
                        .addFunction(insertAll(domainName, entityDependency))
                        .addFunction(insert(domainName, entityDependency))
                        .addFunction(getById(domainName, entityDependency))
                        .addFunction(delete(domainName, entityDependency))
                        .addFunction(update(domainName, entityDependency))
                        .build(),
                ).build()
        fileSpec.writeTo(this)
    }

    private fun insertAll(
        domainName: String,
        entityDependency: DependencyClass,
    ) = FunSpec
        .builder("insertAll$domainName")
        .addAnnotation(
            AnnotationSpec
                .builder(Insert::class)
                .addMember("onConflict = %T.REPLACE", ClassName("androidx.room", "OnConflictStrategy"))
                .build(),
        ).addModifiers(KModifier.ABSTRACT)
        .addParameter(
            ParameterSpec
                .builder(
                    "${domainName.lowerFirstChar()}Entity",
                    ClassName(entityDependency.packageName, entityDependency.className),
                ).addModifiers(KModifier.VARARG)
                .build(),
        ).build()

    private fun getAll(
        domainName: String,
        entityDependency: DependencyClass,
        response: ParameterizedTypeName,
    ) = FunSpec
        .builder("getAll$domainName")
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec
                .builder(Query::class)
                .addMember(
                    "%S",
                    "SELECT * FROM ${entityDependency.className.capitalizeFirstChar()}",
                ).build(),
        ).returns(response)
        .build()

    private fun insert(
        domainName: String,
        entityDependency: DependencyClass,
    ) = FunSpec
        .builder("insert$domainName")
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec
                .builder(Insert::class)
                .addMember("onConflict = %T.REPLACE", ClassName("androidx.room", "OnConflictStrategy"))
                .build(),
        ).addParameter(
            ParameterSpec
                .builder(
                    "${domainName.lowerFirstChar()}Entity",
                    ClassName(entityDependency.packageName, entityDependency.className),
                ).build(),
        ).build()

    private fun getById(
        domainName: String,
        entityDependency: DependencyClass,
    ) = FunSpec
        .builder("get${domainName}ById")
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec
                .builder(Query::class)
                .addMember(
                    "%S",
                    "SELECT * FROM ${entityDependency.className.capitalizeFirstChar()} WHERE id = :id",
                ).build(),
        ).addParameter(
            ParameterSpec
                .builder(
                    "id",
                    String::class,
                ).build(),
        ).returns(
            ClassName(entityDependency.packageName, entityDependency.className).copy(nullable = true),
        ).build()

    private fun delete(
        domainName: String,
        entityDependency: DependencyClass,
    ) = FunSpec
        .builder("delete$domainName")
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec
                .builder(Delete::class)
                .build(),
        ).addParameter(
            ParameterSpec
                .builder(
                    "${domainName.lowerFirstChar()}Entity",
                    ClassName(entityDependency.packageName, entityDependency.className),
                ).build(),
        ).build()

    private fun update(
        domainName: String,
        entityDependency: DependencyClass,
    ) = FunSpec
        .builder("update$domainName")
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec
                .builder(Update::class)
                .addMember("onConflict = %T.REPLACE", ClassName("androidx.room", "OnConflictStrategy"))
                .build(),
        ).addParameter(
            ParameterSpec
                .builder(
                    "${domainName.lowerFirstChar()}Entity",
                    ClassName(entityDependency.packageName, entityDependency.className),
                ).build(),
        ).build()

    companion object {
        fun Project.registerTaskGenerateDao(serviceProvider: Provider<ProjectPathService>): TaskProvider<GenerateDaoSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_DAO, GenerateDaoSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_DAO_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}
