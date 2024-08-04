package tasks.mvvm.model.network

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
import MvvmArchPlugin.Companion.projectDir
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import retrofit2.Response
import retrofit2.http.GET
import tasks.DependencyClass
import utils.TaskUtil.getExtension
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateRestApiSourceFile : DefaultTask() {
    @TaskAction
    fun action() {
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

        val networkModelsPackageName = "$modifiedPackage.networkModels"
        val networkModelClassName = "${mvvmSubPath.makeGoodName()}NetworkModel"
        // write rest api
        val restApiName = "${mvvmSubPath.makeGoodName()}RestApi"
        val restApiPackageName = "$modifiedPackage.restApi"

        projectDir?.writeRestApi(
            packageName = restApiPackageName,
            restApiName = restApiName,
            restApiReturn = DependencyClass(networkModelsPackageName, networkModelClassName),
        )
    }

    private fun File.writeRestApi(
        packageName: String,
        restApiName: String,
        restApiReturn: DependencyClass,
    ) {
        val response =
            Response::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(restApiReturn.packageName, restApiReturn.className),
                ),
            )
        val fileSpec =
            FileSpec
                .builder(packageName, restApiName)
                .addType(
                    TypeSpec
                        .interfaceBuilder(restApiName)
                        .addFunction(
                            FunSpec
                                .builder("getAll${mvvmSubPath.makeGoodName()}")
                                .addModifiers(KModifier.ABSTRACT)
                                .addModifiers(KModifier.SUSPEND)
                                .addAnnotation(
                                    AnnotationSpec
                                        .builder(GET::class)
                                        .addMember(
                                            "%S",
                                            "/api/${mvvmSubPath.makeGoodName().lowercase()}",
                                        ).build(),
                                ).returns(response)
                                .build(),
                        ).build(),
                ).build()
        fileSpec.writeTo(this)
    }

    companion object {
        fun Project.registerTaskGenerateRestApi(): TaskProvider<GenerateRestApiSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_REST_API, GenerateRestApiSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_NETWORK_MODELS)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_REST_API_DESCRIPTION
            }
    }
}
