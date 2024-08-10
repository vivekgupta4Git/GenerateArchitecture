package tasks.mvvm.model.repository

import MvvmPluginConstant
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
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

abstract class GenerateRepositorySourceFile : OptionTask() {

    @TaskAction
    fun action() {
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
        val domainModelClassName = "${domainName}Model"
        val repositoryPackageName = "$modifiedPackage.repository"

        val repositoryInterfaceName = "${domainName}Repository"

        val repositoryClassName = "${domainName}RepositoryImpl"
        val dataSourcePackageName = "$modifiedPackage.dataSources"

        val remoteDataSourceName = "${domainName}RemoteDataSource"
        val localDataSourceName = "${domainName}LocalDataSource"

        val entityPackageName = "$modifiedPackage.entities"
        val entityName = "${domainName}Entity"

        val dtoPackageName = "$modifiedPackage.dto"
        projectDir.writeRepositoryClass(
            repositoryInterfaceName = repositoryInterfaceName,
            domainName = domainName,
            packageName = repositoryPackageName,
            className = repositoryClassName,
            remoteSourceDependency = DependencyClass(dataSourcePackageName, remoteDataSourceName),
            localSourceDependency = DependencyClass(dataSourcePackageName, localDataSourceName),
            domainDependency = DependencyClass(domainModelsPackageName, domainModelClassName),
            entityDependency = DependencyClass(entityPackageName, entityName),
            dtoPackageName = dtoPackageName
        )
    }

    private fun File.writeRepositoryClass(
        repositoryInterfaceName : String,
        domainName: String,
        packageName: String,
        className: String,
        remoteSourceDependency: DependencyClass,
        localSourceDependency: DependencyClass,
        domainDependency: DependencyClass,
        entityDependency: DependencyClass,
        dtoPackageName: String
    ) {
        FileSpec.builder(packageName, className)
            .addImport("kotlinx.coroutines.flow", "flow")
            .addImport("kotlinx.coroutines.flow", "emitAll")
            .addImport("kotlinx.coroutines.flow", "map")
            .addImport(dtoPackageName, "toList${domainDependency.className}")
            .addImport(dtoPackageName, "toList${entityDependency.className}")
            .addType(
                TypeSpec.classBuilder(className)
                    .addSuperinterface(ClassName(packageName,repositoryInterfaceName))
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(
                                ParameterSpec.builder(
                                    remoteSourceDependency.className.lowerFirstChar(),
                                    ClassName(
                                        remoteSourceDependency.packageName,
                                        remoteSourceDependency.className
                                    )
                                )
                                    .build()
                            )
                            .addParameter(
                                ParameterSpec.builder(
                                    localSourceDependency.className.lowerFirstChar(),
                                    ClassName(
                                        localSourceDependency.packageName,
                                        localSourceDependency.className
                                    )
                                )
                                    .build()
                            )
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            remoteSourceDependency.className.lowerFirstChar(),
                            ClassName(
                                remoteSourceDependency.packageName,
                                remoteSourceDependency.className
                            )
                        )
                            .addModifiers(KModifier.PRIVATE)
                            .initializer(remoteSourceDependency.className.lowerFirstChar())
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            localSourceDependency.className.lowerFirstChar(),
                            ClassName(
                                localSourceDependency.packageName,
                                localSourceDependency.className
                            )
                        )
                            .initializer(localSourceDependency.className.lowerFirstChar())
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    .addFunction(
                        getAll(
                            domainName = domainName,
                            domainDependency = domainDependency,
                            remoteDependency = remoteSourceDependency,
                            localDependency = localSourceDependency,
                            entityDependency = entityDependency
                        )
                    )
                    .addFunction(
                        getById(
                            domainName = domainName,
                            domainDependency = domainDependency,
                            remoteDependency = remoteSourceDependency,
                            localDependency = localSourceDependency,
                            entityDependency = entityDependency
                        )
                    )
                    .build()
            )
            .build()
            .writeTo(this)
    }

    private fun getAll(
        domainName: String,
        domainDependency: DependencyClass,
        remoteDependency: DependencyClass,
        localDependency: DependencyClass,
        entityDependency: DependencyClass
    ): FunSpec {
        val returnType = Flow::class.asClassName().parameterizedBy(
            Result::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainDependency.packageName, domainDependency.className)
                )
            )
        )
        return FunSpec.builder("getAll${domainName}")
            .addModifiers(KModifier.OVERRIDE)
            .returns(returnType)
            .beginControlFlow("return flow<Result<List<${domainDependency.className}>>> \n")
            .addStatement("\temitAll(${localDependency.className.lowerFirstChar()}.getAll${domainName}()")
            .addStatement("·\t\t.map { Result.success(it.toList${domainDependency.className}()) })")
            .addStatement("\n${remoteDependency.className.lowerFirstChar()}.getAll${domainName}()")
            .addStatement(
                "\t.onSuccess {· \n\t\t${localDependency.className.lowerFirstChar()}" +
                        ".insertAll${domainName}(*it\n\t\t\t.toList${entityDependency.className}()\n\t\t\t.toTypedArray()) \n}"
            )
            .addStatement("\t.onFailure { emit(Result.failure(it)) }")
            .endControlFlow()
            .addCode("·.%M()", MemberName("kotlinx.coroutines.flow", "distinctUntilChanged"))
            .build()
    }
    private fun getById(
        domainName: String,
        domainDependency: DependencyClass,
        remoteDependency: DependencyClass,
        localDependency: DependencyClass,
        entityDependency: DependencyClass
    ) : FunSpec{
        val returnType = Result::class.asClassName().parameterizedBy(
            ClassName(domainDependency.packageName, domainDependency.className)
        )
        return FunSpec.builder("get${domainName}ById")
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("id", String::class)
            .returns(returnType)
            .addStatement("return Result.failure(Throwable(\"Not Implemented\"))")
            .build()

    }

    companion object {
        fun Project.registerTaskGenerateRepository(serviceProvider: Provider<ProjectPathService>): TaskProvider<GenerateRepositorySourceFile> =
            this.tasks.register(
                MvvmPluginConstant.TASK_GENERATE_REPOSITORY,
                GenerateRepositorySourceFile::class.java,
            ) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_LOCAL_DATA_SOURCE)
                dependsOn(MvvmPluginConstant.TASK_GENERATE_REMOTE_DATA_SOURCE)
                dependsOn(MvvmPluginConstant.TASK_GENERATE_DTO)
                dependsOn(MvvmPluginConstant.TASK_GENERATE_REPOSITORY_INTERFACE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_REPOSITORY_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }


}