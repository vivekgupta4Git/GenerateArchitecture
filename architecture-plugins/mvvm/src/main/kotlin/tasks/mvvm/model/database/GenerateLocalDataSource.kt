package tasks.mvvm.model.database

import MvvmPluginConstant
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.flow.Flow
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import service.ProjectPathService
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateLocalDataSource : OptionTask() {
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
        val domainName =
            projectPathService
                .get()
                .parameters.domainName
                .get()
        val projectDir = File(projectPath)
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

        val explicitPath = projectPathService
            .get()
            .parameters.explicitPath
            .get()

        // write entity model
        val entityPackageName = explicitPath.ifEmpty {  "$modifiedPackage.entities"}
        val entityName = "${domainName}Entity"
        // write dao
        val daoPackageName = explicitPath.ifEmpty {  "$modifiedPackage.dao" }
        val daoName = "${domainName}Dao"

        val dataSourcePackageName = explicitPath.ifEmpty { "$modifiedPackage.dataSources" }

        // write local source
        val localDataSourceName = "${domainName}LocalDataSource"
        val localDependency = DependencyClass(daoPackageName, daoName)
        val localDomainModel = DependencyClass(entityPackageName, entityName)
        projectDir.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = localDataSourceName,
            dependency = localDependency,
            domainModel = localDomainModel,
            domainName = domainName,
        )
    }

    private fun File.writeDataSource(
        dataSourcePackageName: String,
        dataSourceName: String,
        dependency: DependencyClass,
        domainModel: DependencyClass,
        domainName: String,
    ) {
        val fileSpec =
            FileSpec
                .builder(dataSourcePackageName, dataSourceName)
                .addImport("kotlinx.coroutines","withContext")
                .addImport("kotlinx.coroutines.flow","flowOn")
                .addType(
                    TypeSpec
                        .classBuilder(dataSourceName)
                        .primaryConstructor(
                            FunSpec
                                .constructorBuilder()
                                .addParameter(
                                    ParameterSpec
                                        .builder(
                                            dependency.className.lowerFirstChar(),
                                            ClassName(dependency.packageName, dependency.className),
                                        ).build(),
                                )
                                .addParameter(
                                    ParameterSpec.builder("dispatcher",
                                        ClassName("kotlinx.coroutines","CoroutineDispatcher"))
                                        .defaultValue( "${ClassName("kotlinx.coroutines","Dispatchers.IO")}")
                                        .build()
                                )
                                .build(),
                        ).addProperty(
                            PropertySpec
                                .builder(
                                    dependency.className.lowerFirstChar(),
                                    ClassName(dependency.packageName, dependency.className),
                                ).initializer(dependency.className.lowerFirstChar())
                                .addModifiers(KModifier.PRIVATE)
                                .build(),
                        )
                        .addProperty(
                            PropertySpec.builder("dispatcher",
                                ClassName("kotlinx.coroutines","CoroutineDispatcher"))
                                .initializer("dispatcher")
                                .addModifiers(KModifier.PRIVATE)
                                .build()
                        )
                        .addFunction(getAll(domainName, domainModel, dependency))
                        .addFunction(insert(domainName, domainModel, dependency))
                        .addFunction(update(domainName, domainModel, dependency))
                        .addFunction(delete(domainName, domainModel, dependency))
                        .addFunction(insertAll(domainName, domainModel, dependency))
                        .addFunction(getById(domainName, domainModel, dependency))
                        .build(),
                ).build()
        fileSpec.writeTo(this)
    }

    private fun getById(
        domainName: String,
        domainModel: DependencyClass,
        dependency: DependencyClass,
    ): FunSpec  {
        val returnType = ClassName(domainModel.packageName, domainModel.className).copy(nullable = true)
        val daoMethod = "get${domainName}ById"
        return FunSpec
            .builder(daoMethod)
            .addParameter(ParameterSpec.builder("id", String::class).build())
            .addModifiers(KModifier.SUSPEND)
            .returns(returnType)
            .beginControlFlow(" return withContext(dispatcher)")
            .addStatement(" ${dependency.className.lowerFirstChar()}.$daoMethod(id)")
            .endControlFlow()
            .build()
    }

    private fun getAll(
        domainName: String,
        domainModel: DependencyClass,
        dependency: DependencyClass,
    ): FunSpec {
        val returnType =
            Flow::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainModel.packageName, domainModel.className),
                ),
            )

        return FunSpec
            .builder("getAll$domainName")
            .returns(returnType)
            .addStatement("return  ${dependency.className.lowerFirstChar()}.getAll$domainName().flowOn(dispatcher)")
            .build()
    }

    private fun insertAll(
        domainName: String,
        domainModel: DependencyClass,
        dependency: DependencyClass,
    ): FunSpec {
        val variableName = domainModel.className.lowerFirstChar()
        val daoMethod = "insertAll$domainName"
        return FunSpec
            .builder(daoMethod) // same name as dao method
            .addModifiers(KModifier.SUSPEND)
            .addParameter(
                ParameterSpec
                    .builder(
                        variableName,
                        ClassName(domainModel.packageName, domainModel.className),
                    ).addModifiers(KModifier.VARARG)
                    .build(),
            )
            .beginControlFlow(" return withContext(dispatcher)")
            .addStatement(
                " ${dependency.className.lowerFirstChar()}.$daoMethod" +
                    "(*$variableName)")
            .endControlFlow()
            .build()
    }

    private fun insert(
        domainName: String,
        domainModel: DependencyClass,
        dependency: DependencyClass,
    ): FunSpec {
        val variableName = domainModel.className.lowerFirstChar()
        val daoMethod = "insert$domainName"
        return FunSpec
            .builder(daoMethod) // same name as dao method
            .addModifiers(KModifier.SUSPEND)
            .addParameter(
                ParameterSpec
                    .builder(
                        variableName,
                        ClassName(domainModel.packageName, domainModel.className),
                    ).build(),
            )
            .beginControlFlow(" return withContext(dispatcher)")
            .addStatement(
                " ${dependency.className.lowerFirstChar()}.$daoMethod" +
                    "($variableName)")
            .endControlFlow()
            .build()
    }

    private fun update(
        domainName: String,
        domainModel: DependencyClass,
        dependency: DependencyClass,
    ): FunSpec {
        val variableName = domainModel.className.lowerFirstChar()
        val daoMethod = "update$domainName"
        return FunSpec
            .builder(daoMethod) // same name as dao method
            .addModifiers(KModifier.SUSPEND)
            .addParameter(
                ParameterSpec
                    .builder(
                        variableName,
                        ClassName(domainModel.packageName, domainModel.className),
                    ).build(),
            )
            .beginControlFlow(" return withContext(dispatcher)")
            .addStatement(
                " ${dependency.className.lowerFirstChar()}.$daoMethod" +
                    "($variableName)",
            ).endControlFlow().build()
    }

    private fun delete(
        domainName: String,
        domainModel: DependencyClass,
        dependency: DependencyClass,
    ): FunSpec {
        val variableName = domainModel.className.lowerFirstChar()
        val daoMethod = "delete$domainName"
        return FunSpec
            .builder(daoMethod) // same name as dao method
            .addModifiers(KModifier.SUSPEND)
            .addParameter(
                ParameterSpec
                    .builder(
                        variableName,
                        ClassName(domainModel.packageName, domainModel.className),
                    ).build(),
            )
            .beginControlFlow(" return withContext(dispatcher)")
            .addStatement(
                " ${dependency.className.lowerFirstChar()}.$daoMethod" +
                    "($variableName)",
            ).endControlFlow().build()
    }

    companion object {
        fun Project.registerTaskGenerateLocalDataSource(
            serviceProvider: Provider<ProjectPathService>,
        ): TaskProvider<GenerateLocalDataSource> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_LOCAL_DATA_SOURCE, GenerateLocalDataSource::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_DAO)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_LOCAL_DATA_SOURCE_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }

    }
}
