package tasks.mvvm.model.repository

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
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
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateRepositoryInterface  : OptionTask(){

    @TaskAction
    fun action(){
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
        val repositoryClassName = "${domainName}Repository"


        projectDir.writeRepositoryInterface(
            domainName = domainName,
            packageName = repositoryPackageName,
            className = repositoryClassName,
            domainDependency = DependencyClass(domainModelsPackageName, domainModelClassName),
        )
    }
    private fun File.writeRepositoryInterface(
        domainName: String,
        packageName: String,
        className: String,
        domainDependency: DependencyClass,

    ){
        FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.interfaceBuilder(className)
                    .addFunction(getAll(domainName, domainDependency))
                    .addFunction(getById(domainName,domainDependency))
                    .build()
            )
            .build()
            .writeTo(this)

    }
    private fun getAll(
        domainName: String,
        domainDependency: DependencyClass,
    ) : FunSpec{
        val returnType = Flow::class.asClassName().parameterizedBy(
            Result::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainDependency.packageName, domainDependency.className)
                )
            )
        )

        return FunSpec.builder("getAll${domainName}")
            .addModifiers(KModifier.ABSTRACT)
            .returns(returnType)
            .build()
    }
    private fun getById(
        domainName: String,
        domainDependency: DependencyClass,
    ) : FunSpec{
        val returnType = Result::class.asClassName().parameterizedBy(
            ClassName(domainDependency.packageName, domainDependency.className).copy(nullable = true))
        return FunSpec.builder("get${domainName}ById")
            .addParameter(
                ParameterSpec.builder("id", String::class).build()
            )
            .addModifiers(KModifier.ABSTRACT)
            .addModifiers(KModifier.SUSPEND)
            .returns(returnType)
            .build()
    }


    companion object{
        fun Project.registerTaskGenerateRepositoryInterface(serviceProvider: Provider<ProjectPathService>): TaskProvider<GenerateRepositoryInterface> =
            this.tasks.register(
                MvvmPluginConstant.TASK_GENERATE_REPOSITORY_INTERFACE,
                GenerateRepositoryInterface::class.java,
            ) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_DOMAIN_MODELS)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_REPOSITORY_INTERFACE_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}