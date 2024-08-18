package tasks.mvvm.model.network

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
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import service.ProjectPathService
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.capitalizeFirstChar
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateRemoteDataSource : OptionTask() {
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

        val networkModelsPackageName = explicitPath.ifEmpty {  "$modifiedPackage.networkModels" }
        val networkModelClassName = "${domainName.makeGoodName()}NetworkModel"

        val restApiName =  "${domainName.makeGoodName()}RestApi"
        val restApiPackageName = explicitPath.ifEmpty {  "$modifiedPackage.restApi" }

        val dataSourcePackageName = explicitPath.ifEmpty {  "$modifiedPackage.dataSources" }

        val remoteDataSourceName = "${domainName.makeGoodName()}RemoteDataSource"
        val remoteDependency = DependencyClass(restApiPackageName, restApiName)
        val remoteDomainModel = DependencyClass(networkModelsPackageName, networkModelClassName)
        projectDir.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = remoteDataSourceName,
            dependency = remoteDependency,
            domainModel = remoteDomainModel,
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
                                            ClassName(dependency.packageName, dependency.className)
                                        ).build()
                                )
                                .addParameter(
                                    ParameterSpec.builder("dispatcher",
                                        ClassName("kotlinx.coroutines","CoroutineDispatcher"))
                                        .defaultValue( "${ClassName("kotlinx.coroutines","Dispatchers.IO")}")
                                        .build()
                                )
                                .build()
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
                        .addFunction(getAll(dependency, domainModel, domainName))
                        .addFunction(getById(dependency, domainModel, domainName))
                        .addFunction(insert(dependency, domainModel, domainName))
                        .addFunction(delete(dependency, domainModel, domainName))
                        .addFunction(update(dependency, domainModel, domainName))
                        .build()

                ).build()
        fileSpec.writeTo(this)
    }
    private fun getById(
        dependency: DependencyClass,
        domainModel: DependencyClass,
        domainName: String,
    ) : FunSpec{
        val returnType =
            Result::class.asClassName().parameterizedBy(
                ClassName(domainModel.packageName, domainModel.className),
            )
        val funSpec = FunSpec.builder("get${domainName.capitalizeFirstChar()}ById")
            .addModifiers(KModifier.SUSPEND)
            .addParameter(ParameterSpec.builder("id", String::class).build())
            .returns(returnType)
        return  funSpec
            .beginControlFlow("return withContext(dispatcher)")
            .addStatement("val result = ${dependency.className.lowerFirstChar()}.get${domainName.makeGoodName()}ById(id)")
            .commonControlFlow()
            .endControlFlow()
            .build()
    }
    private fun getAll(
        dependency: DependencyClass,
        domainModel: DependencyClass,
        domainName: String,
    ) : FunSpec{
        val returnType =
            Result::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainModel.packageName, domainModel.className),
                ),
            )

        val funSpec =
            FunSpec
                .builder("getAll${domainName.capitalizeFirstChar()}")
                .addModifiers(KModifier.SUSPEND)
                .returns(returnType)

        return funSpec
            .beginControlFlow("return withContext(dispatcher)")
            .addStatement("val result = ${dependency.className.lowerFirstChar()}.getAll${domainName.makeGoodName()}()")
            .commonControlFlow()
            .endControlFlow()
            .build()
    }

    private fun insert(
        dependency: DependencyClass,
        domainModel: DependencyClass,
        domainName: String
    ) : FunSpec{
        val returnType =
            Result::class.asClassName().parameterizedBy(
                    ClassName(domainModel.packageName, domainModel.className),
            )

        val funSpec =
            FunSpec
                .builder("insert${domainName.capitalizeFirstChar()}")
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec
                    .builder(
                        domainModel.className.lowerFirstChar(),
                        ClassName(domainModel.packageName,domainModel.className)
                    ).build())
                .returns(returnType)

        return funSpec
            .beginControlFlow("return withContext(dispatcher)")
            .addStatement("val result = ${dependency.className.lowerFirstChar()}" +
                    ".insert${domainName.makeGoodName()}(${domainModel.className.lowerFirstChar()})")
            .commonControlFlow()
            .endControlFlow()
            .build()
    }
    private fun delete(
        dependency: DependencyClass,
        domainModel: DependencyClass,
        domainName: String
    ) : FunSpec
    {
        val returnType =
            Result::class.asClassName().parameterizedBy(
                ClassName(domainModel.packageName, domainModel.className),
            )

        val funSpec =
            FunSpec
                .builder("delete${domainName.capitalizeFirstChar()}")
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec.builder("id", String::class).build())

                .returns(returnType)

        return funSpec
            .beginControlFlow("return withContext(dispatcher)")
            .addStatement("val result = ${dependency.className.lowerFirstChar()}" +
                    ".delete${domainName.makeGoodName()}(id)")
            .commonControlFlow()
            .endControlFlow()
            .build()
    }
    private fun update(
        dependency: DependencyClass,
        domainModel: DependencyClass,
        domainName: String
    ) : FunSpec
    {
        val returnType =
            Result::class.asClassName().parameterizedBy(
                ClassName(domainModel.packageName, domainModel.className),
            )

        val funSpec =
            FunSpec
                .builder("update${domainName.capitalizeFirstChar()}")
                .addModifiers(KModifier.SUSPEND)
                .addParameter(ParameterSpec.builder("id", String::class).build())
                .addParameter(ParameterSpec
                    .builder(
                        domainModel.className.lowerFirstChar(),
                        ClassName(domainModel.packageName,domainModel.className)
                    ).build())
                .returns(returnType)

        return funSpec
            .beginControlFlow("return withContext(dispatcher)")
            .addStatement("val result = ${dependency.className.lowerFirstChar()}" +
                    ".update${domainName.makeGoodName()}(id,${domainModel.className.lowerFirstChar()})")
            .commonControlFlow()
            .endControlFlow()
            .build()
    }


    private fun FunSpec.Builder.commonControlFlow() = this
        .beginControlFlow("if(result.isSuccessful)")
        .addStatement("val body = result.body()")
        .beginControlFlow("if(body != null)")
        .addStatement("Result.success(body)")
        .nextControlFlow("else")
        .addStatement("Result.failure(Throwable(%S))", "response body is empty/null")
        .endControlFlow()
        .nextControlFlow("else")
        .addStatement("Result.failure(${ClassName("retrofit2", "HttpException")}(result))")
        .endControlFlow()

    companion object {
        fun Project.registerTaskGenerateRemoteDataSource(
            serviceProvider: Provider<ProjectPathService>,
        ): TaskProvider<GenerateRemoteDataSource> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_REMOTE_DATA_SOURCE, GenerateRemoteDataSource::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_REST_API)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_REMOTE_DATA_SOURCE_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}
