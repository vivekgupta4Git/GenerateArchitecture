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
import kotlin.math.exp

abstract class GenerateRepositorySourceFile : OptionTask() {

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
        val explicitPath = projectPathService.get()
            .parameters.explicitPath.get()

        val domainModelsPackageName = explicitPath.ifBlank {  "$modifiedPackage.domainModels" }
        val domainModelClassName = "${domainName}Model"
        val repositoryPackageName = explicitPath.ifBlank {  "$modifiedPackage.repository" }

        val repositoryInterfaceName = "${domainName}Repository"

        val repositoryClassName = "${domainName}RepositoryImpl"
        val dataSourcePackageName = explicitPath.ifBlank {  "$modifiedPackage.dataSources" }

        val remoteDataSourceName = "${domainName}RemoteDataSource"
        val localDataSourceName = "${domainName}LocalDataSource"

        val entityPackageName = explicitPath.ifBlank {  "$modifiedPackage.entities" }
        val entityName = "${domainName}Entity"

        val dtoPackageName = explicitPath.ifBlank {  "$modifiedPackage.mapper" }
        val networkModelPackageName =  explicitPath.ifBlank {  "$modifiedPackage.networkModels" }
        val networkModelName = "${domainName}NetworkModel"
        projectDir.writeRepositoryClass(
            repositoryInterfaceName = repositoryInterfaceName,
            domainName = domainName,
            packageName = repositoryPackageName,
            className = repositoryClassName,
            remoteSourceDependency = DependencyClass(dataSourcePackageName, remoteDataSourceName),
            localSourceDependency = DependencyClass(dataSourcePackageName, localDataSourceName),
            domainDependency = DependencyClass(domainModelsPackageName, domainModelClassName),
            entityDependency = DependencyClass(entityPackageName, entityName),
            dtoPackageName = dtoPackageName,
            networkModelDependency = DependencyClass(networkModelPackageName, networkModelName)
        )
    }

    private fun File.writeRepositoryClass(
        repositoryInterfaceName: String,
        domainName: String,
        packageName: String,
        className: String,
        remoteSourceDependency: DependencyClass,
        localSourceDependency: DependencyClass,
        domainDependency: DependencyClass,
        entityDependency: DependencyClass,
        dtoPackageName: String,
        networkModelDependency: DependencyClass
    ) {
        FileSpec.builder(packageName, className)
            .addImport("kotlinx.coroutines.flow", "flow")
            .addImport("kotlinx.coroutines.flow", "emitAll")
            .addImport("kotlinx.coroutines.flow", "map")
            .addImport(dtoPackageName, "toList${domainDependency.className}")
            .addImport(dtoPackageName, "toList${entityDependency.className}")
            .addImport(dtoPackageName, "to${entityDependency.className}")
            .addImport(dtoPackageName, "to${domainDependency.className}")
            .addImport(dtoPackageName, "to${networkModelDependency.className}")
            .addImport(dtoPackageName, "to${entityDependency.className}")
            .addImport(dtoPackageName, "to${networkModelDependency.className}")
            .addType(
                TypeSpec.classBuilder(className)
                    .addSuperinterface(ClassName(packageName, repositoryInterfaceName))
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
                    .addFunction(
                        insert(
                            domainName = domainName,
                            domainDependency = domainDependency,
                            remoteDependency = remoteSourceDependency,
                            localDependency = localSourceDependency,
                            entityDependency = entityDependency,
                            networkModelDependency = networkModelDependency
                        )
                    )
                    .addFunction(
                        update(
                            domainName = domainName,
                            domainDependency = domainDependency,
                            remoteDependency = remoteSourceDependency,
                            localDependency = localSourceDependency,
                            entityDependency = entityDependency,
                            networkModelDependency = networkModelDependency
                        )
                    )
                    .addFunction(
                        delete(
                            domainName = domainName,
                            domainDependency = domainDependency,
                            remoteDependency = remoteSourceDependency,
                            localDependency = localSourceDependency,
                            entityDependency = entityDependency,
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
            .addStatement("·\t\t.map { localData -> Result.success(localData.toList${domainDependency.className}()) })")
            .addStatement("\n${remoteDependency.className.lowerFirstChar()}.getAll${domainName}()")
            .addStatement(
                "\t.onSuccess {remoteData ->· \n\t\t${localDependency.className.lowerFirstChar()}" +
                        ".insertAll${domainName}(*remoteData\n\t\t\t.toList${entityDependency.className}()\n\t\t\t.toTypedArray()) \n}"
            )
            .addStatement("\t.onFailure { emit(Result.failure(it)) }")
            .addStatement("\temitAll(${localDependency.className.lowerFirstChar()}.getAll${domainName}()")
            .addStatement("·\t\t.map { localData -> Result.success(localData.toList${domainDependency.className}()) })")
            .endControlFlow()
            .addCode("·.%M()", MemberName("kotlinx.coroutines.flow", "distinctUntilChanged"))
            .build()
    }

    /*
       featureRemoteDataSource.getFeatureById(id).onSuccess { data ->
          featureLocalDataSource.insertFeature(data.toFeatureEntity())
      }

      featureLocalDataSource.getFeatureById(id)?.let {
          return Result.success(it.toFeatureModel())
      }

      return Result.failure(Throwable(""))
     */
    private fun getById(
        domainName: String,
        domainDependency: DependencyClass,
        remoteDependency: DependencyClass,
        localDependency: DependencyClass,
        entityDependency: DependencyClass
    ): FunSpec {
        val returnType = Result::class.asClassName().parameterizedBy(
            ClassName(domainDependency.packageName, domainDependency.className)
        )
        return FunSpec.builder("get${domainName}ById")
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("id", String::class)
            .returns(returnType)
            .addCode(
                """
                ${remoteDependency.className.lowerFirstChar()}.get${domainName}ById(id).onSuccess { data ->
                    ${localDependency.className.lowerFirstChar()}.insert${domainName}(data.to${entityDependency.className}())
                }
                
                ${localDependency.className.lowerFirstChar()}.get${domainName}ById(id)?.let {
                    return Result.success(it.to${domainDependency.className}())
                }
            """.trimIndent()
            )
            .addStatement("\nreturn Result.failure(Throwable(\"Not Found\"))")
            .build()

    }

    private fun insert(
        domainName: String,
        domainDependency: DependencyClass,
        remoteDependency: DependencyClass,
        localDependency: DependencyClass,
        entityDependency: DependencyClass,
        networkModelDependency: DependencyClass
    ): FunSpec {
        /*
        val result =
              featureRemoteDataSource.insertFeature(feature.toFeatureEntity().toFeatureNetworkModel())
          return if (result.isSuccess) {
              result.getOrNull()?.let {
                  featureLocalDataSource.insertFeature(it.toFeatureEntity())
              }
              Result.success("inserted Successfully")
          } else {
              Result.failure(result.exceptionOrNull()!!)
          }
         */
        return FunSpec.builder("insert${domainName}")
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(
                ParameterSpec.builder(
                    domainName.lowerFirstChar(),
                    ClassName(domainDependency.packageName, domainDependency.className)
                )
                    .build()
            )
            .returns(Result::class.asClassName().parameterizedBy(String::class.asClassName()))
            .addStatement(
                """
                val result = ${remoteDependency.className.lowerFirstChar()}
                        .insert${domainName}(${domainName.lowerFirstChar()}
                                        .to${entityDependency.className}().to${networkModelDependency.className}())

           """.trimIndent()
            )
            .addStatement("return if (result.isSuccess) {")
            .addStatement("	result.getOrNull()?.let {")
            .addStatement("		${localDependency.className.lowerFirstChar()}.insert${domainName}(it.to${entityDependency.className}())")
            .addStatement("	}")
            .addStatement("	Result.success(%S)", "Inserted Successfully")
            .addStatement("} else {")
            .addStatement("	Result.failure(Throwable(result.exceptionOrNull()))")
            .addStatement("}")
            .build()

    }

    private fun update(
        domainName: String,
        domainDependency: DependencyClass,
        remoteDependency: DependencyClass,
        localDependency: DependencyClass,
        entityDependency: DependencyClass,
        networkModelDependency: DependencyClass
    ): FunSpec {
        /*
        val result =
              featureRemoteDataSource.updateFeature(feature.toFeatureEntity().toFeatureNetworkModel())
          return if (result.isSuccess) {
              result.getOrNull()?.let {
                  featureLocalDataSource.updateFeature(it.toFeatureEntity())
              }
              Result.success("updated Successfully")
          } else {
              Result.failure(Throwable(result.exceptionOrNull()))
          }
         */
        return FunSpec.builder("update${domainName}")
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(
                ParameterSpec.builder(
                    domainName.lowerFirstChar(),
                    ClassName(domainDependency.packageName, domainDependency.className)
                )
                    .build()
            )
            .returns(Result::class.asClassName().parameterizedBy(String::class.asClassName()))
            .addStatement(
                """
                val result = ${remoteDependency.className.lowerFirstChar()}.update${domainName}(
                                    ${domainName.lowerFirstChar()}.id,
                                    ${domainName.lowerFirstChar()}.to${entityDependency.className}().to${networkModelDependency.className}()
                )
           """.trimIndent()
            )
            .addStatement("return if (result.isSuccess) {")
            .addStatement("	result.getOrNull()?.let {")
            .addStatement("		${localDependency.className.lowerFirstChar()}.update${domainName}(it.to${entityDependency.className}())")
            .addStatement("	}")
            .addStatement("	Result.success(%S)", "Updated Successfully")
            .addStatement("} else {")
            .addStatement("	Result.failure(Throwable(result.exceptionOrNull()))")
            .addStatement("}")
            .build()

    }

    private fun delete(
        domainName: String,
        domainDependency: DependencyClass,
        remoteDependency: DependencyClass,
        localDependency: DependencyClass,
        entityDependency: DependencyClass,
    ): FunSpec {
        /*
        val result =
              featureRemoteDataSource.deleteFeature(feature.toFeatureEntity().toFeatureNetworkModel())
          return if (result.isSuccess) {
              result.getOrNull()?.let {
                  featureLocalDataSource.deleteFeature(it.toFeatureEntity())
              }
              Result.success("deleted Successfully")
          } else {
              Result.failure(Throwable(result.exceptionOrNull()))
          }
         */
        return FunSpec.builder("delete${domainName}")
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(
                ParameterSpec.builder(
                    domainName.lowerFirstChar(),
                    ClassName(domainDependency.packageName, domainDependency.className)
                )
                    .build()
            )
            .returns(Result::class.asClassName().parameterizedBy(String::class.asClassName()))
            .addStatement(
                """
                val result = ${remoteDependency.className.lowerFirstChar()}.delete${domainName}(${domainName.lowerFirstChar()}.id)
           """.trimIndent()
            )
            .addStatement("return if (result.isSuccess) {")
            .addStatement("	result.getOrNull()?.let {")
            .addStatement("		${localDependency.className.lowerFirstChar()}.delete${domainName}(it.to${entityDependency.className}())")
            .addStatement("	}")
            .addStatement("	Result.success(%S)", "Deleted Successfully")
            .addStatement("} else {")
            .addStatement("	Result.failure(Throwable(result.exceptionOrNull()))")
            .addStatement("}")
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
                dependsOn(MvvmPluginConstant.TASK_GENERATE_MAPPER)
                dependsOn(MvvmPluginConstant.TASK_GENERATE_REPOSITORY_INTERFACE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_REPOSITORY_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }


}