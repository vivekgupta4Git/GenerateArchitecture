package tasks.mvvm.model.network

import MvvmPluginConstant
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import service.ProjectPathService
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.capitalizeFirstChar
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.modifyPackageName
import utils.TaskUtil.wrapInRetrofitResponse
import java.io.File
import kotlin.math.exp

abstract class GenerateRestApiSourceFile : OptionTask() {
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
        // model extension
        val modelExtension = getExtension(project).model
        val modifiedPackage =
            modelExtension
                .insideDirectory
                .get()
                .modifyPackageName(
                    packageName,
                    modelExtension.name.get(),
                )
        val explicitPath = projectPathService.get()
            .parameters.explicitPath.get()

        val networkModelsPackageName = explicitPath.ifEmpty {  "$modifiedPackage.networkModels"}
        val networkModelClassName = "${domainName}NetworkModel"
        // write rest api
        val restApiName = "${domainName}RestApi"
        val restApiPackageName = explicitPath.ifEmpty {   "$modifiedPackage.restApi" }

        projectDir.writeRestApi(
            packageName = restApiPackageName,
            restApiName = restApiName,
            restApiReturn = DependencyClass(networkModelsPackageName, networkModelClassName),
            domainName = domainName,
        )
    }

    private fun File.writeRestApi(
        packageName: String,
        restApiName: String,
        restApiReturn: DependencyClass,
        domainName: String,
    ) {
        val fileSpec =
            FileSpec
                .builder(packageName, restApiName)
                .addType(
                    TypeSpec
                        .interfaceBuilder(restApiName)
                        .addFunction(getAll(domainName, restApiReturn))
                        .addFunction(insert(domainName, restApiReturn))
                        .addFunction(update(domainName, restApiReturn))
                        .addFunction(delete(domainName, restApiReturn))
                        .addFunction(getById(domainName, restApiReturn))
                        .build(),
                ).build()
        fileSpec.writeTo(this)
    }

    private fun getAll(
        domainName: String,
        restApiReturn: DependencyClass,
    ): FunSpec {
        val response =
            Response::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(restApiReturn.packageName, restApiReturn.className),
                ),
            )
        return FunSpec
            .builder("getAll${domainName.capitalizeFirstChar()}")
            .addModifiers(KModifier.ABSTRACT)
            .addModifiers(KModifier.SUSPEND)
            .addAnnotation(
                AnnotationSpec
                    .builder(GET::class)
                    .addMember(
                        "%S",
                        "/api/${domainName.lowercase()}",
                    ).build(),
            ).returns(response)
            .build()
    }

    private fun getById(
        domainName: String,
        restApiReturn: DependencyClass,
    ): FunSpec =
        FunSpec
            .builder("get${domainName.capitalizeFirstChar()}ById")
            .addModifiers(KModifier.ABSTRACT)
            .addModifiers(KModifier.SUSPEND)
            .addAnnotation(
                AnnotationSpec
                    .builder(GET::class)
                    .addMember(
                        "%S",
                        "/api/${domainName.lowercase()}/{id}",
                    ).build(),
            ).addParameter(
                ParameterSpec
                    .builder("id", String::class)
                    .addAnnotation(
                        AnnotationSpec.builder(Path::class).addMember("%S", "id").build(),
                    ).build(),
            ).returns(restApiReturn.wrapInRetrofitResponse())
            .build()

    private fun insert(
        domainName: String,
        restApiReturn: DependencyClass,
    ): FunSpec =
        FunSpec
            .builder("insert${domainName.capitalizeFirstChar()}")
            .addModifiers(KModifier.ABSTRACT)
            .addModifiers(KModifier.SUSPEND)
            .addAnnotation(
                AnnotationSpec
                    .builder(POST::class)
                    .addMember(
                        "%S",
                        "api/${domainName.lowercase()}",
                    ).build(),
            ).addParameter(
                ParameterSpec
                    .builder(
                        "${domainName.lowerFirstChar()}Request",
                        ClassName(restApiReturn.packageName, restApiReturn.className),
                    ).addAnnotation(
                        AnnotationSpec.builder(Body::class).build(),
                    ).build(),
            ).returns(restApiReturn.wrapInRetrofitResponse())
            .build()

    private fun update(
        domainName: String,
        restApiReturn: DependencyClass,
    ): FunSpec =
        FunSpec
            .builder("update${domainName.capitalizeFirstChar()}")
            .addModifiers(KModifier.ABSTRACT)
            .addModifiers(KModifier.SUSPEND)
            .addAnnotation(
                AnnotationSpec
                    .builder(PUT::class)
                    .addMember(
                        "%S",
                        "api/${domainName.lowercase()}/{id}",
                    ).build(),
            ).addParameter(
                ParameterSpec
                    .builder("id", String::class)
                    .addAnnotation(
                        AnnotationSpec.builder(Path::class).addMember("%S", "id").build(),
                    ).build(),
            ).addParameter(
                ParameterSpec
                    .builder(
                        "${domainName.lowerFirstChar()}Request",
                        ClassName(restApiReturn.packageName, restApiReturn.className),
                    ).addAnnotation(
                        AnnotationSpec.builder(Body::class).build(),
                    ).build(),
            ).returns(restApiReturn.wrapInRetrofitResponse())
            .build()

    private fun delete(
        domainName: String,
        restApiReturn: DependencyClass,
    ): FunSpec =
        FunSpec
            .builder("delete${domainName.capitalizeFirstChar()}")
            .addModifiers(KModifier.ABSTRACT)
            .addModifiers(KModifier.SUSPEND)
            .addAnnotation(
                AnnotationSpec
                    .builder(DELETE::class)
                    .addMember(
                        "%S",
                        "api/${domainName.lowercase()}/{id}",
                    ).build(),
            ).addParameter(
                ParameterSpec
                    .builder("id", String::class)
                    .addAnnotation(
                        AnnotationSpec.builder(Path::class).addMember("%S", "id").build(),
                    ).build(),
            ).returns(restApiReturn.wrapInRetrofitResponse())
            .build()

    companion object {
        fun Project.registerTaskGenerateRestApi(serviceProvider: Provider<ProjectPathService>): TaskProvider<GenerateRestApiSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_REST_API, GenerateRestApiSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_NETWORK_MODELS)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_REST_API_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}
