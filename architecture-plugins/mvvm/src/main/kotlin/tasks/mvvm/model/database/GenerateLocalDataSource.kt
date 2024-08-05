package tasks.mvvm.model.database

import MvvmArchPlugin
import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
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
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.capitalizeFirstChar
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateLocalDataSource : OptionTask() {

    @TaskAction
    fun action()  {
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

        val dataSourcePackageName = "$modifiedPackage.dataSources"

        // write local source
        val localDataSourceName = "${mvvmSubPath.makeGoodName()}LocalDataSource"
        val localDependency = DependencyClass(daoPackageName, daoName)
        val localDomainModel = DependencyClass(entityPackageName, entityName)
        MvvmArchPlugin.projectDir?.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = localDataSourceName,
            dependency = localDependency,
            domainModel = localDomainModel,
        )
    }

    private fun File.writeDataSource(
        dataSourcePackageName: String,
        dataSourceName: String,
        dependency: DependencyClass,
        domainModel: DependencyClass,
    ) {
        val returnType =
            Flow::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainModel.packageName, domainModel.className),
                ),
            )

        val funSpec =
            FunSpec
                .builder("getAll${domainModel.className.capitalizeFirstChar()}")
                .returns(returnType)

        val finalFunSpec = funSpec.addLocalDataSourceStatements(dependency)

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

    private fun FunSpec.Builder.addLocalDataSourceStatements(dependency: DependencyClass) =
        this.addStatement("return  ${dependency.className.lowerFirstChar()}.getAll${mvvmSubPath.makeGoodName()}()")

    companion object {
        fun Project.registerTaskGenerateLocalDataSource(): TaskProvider<GenerateLocalDataSource> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_LOCAL_DATA_SOURCE, GenerateLocalDataSource::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_DAO)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_LOCAL_DATA_SOURCE_DESCRIPTION
            }
    }
}
