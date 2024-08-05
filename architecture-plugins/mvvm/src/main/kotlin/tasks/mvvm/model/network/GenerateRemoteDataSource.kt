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
        val mvvmSubPath =
            projectPathService
                .get()
                .parameters.mvvmSubPath
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

        val networkModelsPackageName = "$modifiedPackage.networkModels"
        val networkModelClassName = "${mvvmSubPath.makeGoodName()}NetworkModel"

        val restApiName = "${mvvmSubPath.makeGoodName()}RestApi"
        val restApiPackageName = "$modifiedPackage.restApi"

        val dataSourcePackageName = "$modifiedPackage.dataSources"

        val remoteDataSourceName = "${mvvmSubPath.makeGoodName()}RemoteDataSource"
        val remoteDependency = DependencyClass(restApiPackageName, restApiName)
        val remoteDomainModel = DependencyClass(networkModelsPackageName, networkModelClassName)
        projectDir.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = remoteDataSourceName,
            dependency = remoteDependency,
            domainModel = remoteDomainModel,
            mvvmSubPath = mvvmSubPath,
        )
    }

    private fun File.writeDataSource(
        dataSourcePackageName: String,
        dataSourceName: String,
        dependency: DependencyClass,
        domainModel: DependencyClass,
        mvvmSubPath: String,
    ) {
        val returnType =
            Result::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainModel.packageName, domainModel.className),
                ),
            )

        val funSpec =
            FunSpec
                .builder("getAll${domainModel.className.capitalizeFirstChar()}")
                .addModifiers(KModifier.SUSPEND)
                .returns(returnType)

        val finalFunSpec = funSpec.addRemoteDataSourceStatements(dependency, mvvmSubPath)

        val fileSpec =
            FileSpec
                .builder(dataSourcePackageName, dataSourceName)
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
                                ).build(),
                        ).addProperty(
                            PropertySpec
                                .builder(
                                    dependency.className.lowerFirstChar(),
                                    ClassName(dependency.packageName, dependency.className),
                                ).initializer(dependency.className.lowerFirstChar())
                                .addModifiers(KModifier.PRIVATE)
                                .build(),
                        ).addFunction(
                            finalFunSpec
                                .build(),
                        ).build(),
                ).build()
        fileSpec.writeTo(this)
    }

    private fun FunSpec.Builder.addRemoteDataSourceStatements(
        dependency: DependencyClass,
        mvvmSubPath: String,
    ) = this
        .addStatement("val result = ${dependency.className.lowerFirstChar()}.getAll${mvvmSubPath.makeGoodName()}()")
        .beginControlFlow("return if(result.isSuccessful && result.body() != null)")
        .addStatement("Result.success(result.body()!!)")
        .nextControlFlow("else")
        .addStatement("Result.failure(Throwable(%S))", "Unable to fetch")
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
