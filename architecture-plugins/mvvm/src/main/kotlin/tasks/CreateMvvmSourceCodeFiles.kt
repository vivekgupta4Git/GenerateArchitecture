package tasks

import MvvmArchPlugin.Companion.mvvmSubPath
import MvvmArchPlugin.Companion.packageName
import MvvmArchPlugin.Companion.projectDir
import MvvmArchPlugin.Companion.useKotlin
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
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import tasks.mvvm.model.CreateModels
import utils.TaskUtil.capitalizeFirstChar
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.makeGoodName
import utils.TaskUtil.modifyPackageName
import java.io.File

/**
 *@author Vivek Gupta on
 */
abstract class CreateMvvmSourceCodeFiles : DefaultTask() {
    @Option(
        option = "sub-path",
        description = """Generates mvvm architecture inside the sub-path.
    This plugin generates stuffs under main source set i.e main/packageName/,
    so if sub-path is given then main/packageName/subPath/""",
    )
    fun setSubPath(subPath: String) {
        mvvmSubPath = subPath
    }

    @Option(
        option = "preferKotlin",
        description = """ This plugin generates code assuming you have kotlin sourceSets
    but if you have java sourceSets and you want to generate structure in the java sourceSets you can set this flag to false by
    using option --no-preferKotlin""",
    )
    fun setPreferKotlin(prefer: Boolean) {
        useKotlin = prefer
    }

    @TaskAction
    fun action() {
        project.tasks.getByName(MvvmPluginConstant.TASK_CREATE_MODELS, CreateModels::class).action()

        /**
         * model >
         *         interfaces
         *         domainModels
         *         dataSources
         *         repositories
         *         entities
         *         networkModels
         *
         */
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

        // write network model
        val networkModelsPackageName = "$modifiedPackage.networkModels"
        val networkModelClassName = "${mvvmSubPath.makeGoodName()}NetworkModel"

        // write entity model
        val entityPackageName = "$modifiedPackage.entities"
        val entityName = "${mvvmSubPath.makeGoodName()}Entity"

        // write rest api
        val restApiName = "${mvvmSubPath.makeGoodName()}RestApi"
        val restApiPackageName = "$modifiedPackage.restApi"

        // write dao
        val daoPackageName = "$modifiedPackage.dao"
        val daoName = "${mvvmSubPath.makeGoodName()}Dao"

        // write data source
        val dataSourcePackageName = "$modifiedPackage.dataSources"
        val remoteDataSourceName = "${mvvmSubPath.makeGoodName()}RemoteDataSource"
        val remoteDependency = DependencyClass(restApiPackageName, restApiName)
        val remoteDomainModel = DependencyClass(networkModelsPackageName, networkModelClassName)
        projectDir?.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = remoteDataSourceName,
            dependency = remoteDependency,
            domainModel = remoteDomainModel,
        )
        // write local source
        val localDataSourceName = "${mvvmSubPath.makeGoodName()}LocalDataSource"
        val localDependency = DependencyClass(daoPackageName, daoName)
        val localDomainModel = DependencyClass(entityPackageName, entityName)
        projectDir?.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = localDataSourceName,
            dependency = localDependency,
            domainModel = localDomainModel,
            isRemote = false,
        )

        /*createModelFile(
            File(projectPath),
            modelExtension.name.get(),
            modelExtension.insideDirectory.get(),
            mvvmSubPath
        )
        createInterfaceFile(
            File(projectPath),
            modelExtension.name.get(),
            modelExtension.insideDirectory.get(),
            mvvmSubPath
        )
        createDataSourceFile(
            File(projectPath),
            modelExtension.name.get(),
            modelExtension.insideDirectory.get(),
            mvvmSubPath
        )


        val viewExtension = extension.view
           createModelFile(
            File(projectPath),
            viewExtension.name.get(),
            viewExtension.insideDirectory.get(),
            mvvmSubPath
        )
        val viewModelExtension = extension.viewModel

        createModelFile(
            File(projectPath),
            viewModelExtension.name.get(),
            viewModelExtension.insideDirectory.get(),
            mvvmSubPath
        )*/
    }

    private fun File.writeDataSource(
        dataSourcePackageName: String,
        dataSourceName: String,
        dependency: DependencyClass,
        domainModel: DependencyClass,
        isRemote: Boolean = true,
    ) {
        val returnType =
            if (isRemote) {
                Result::class.asClassName().parameterizedBy(
                    List::class.asClassName().parameterizedBy(
                        ClassName(domainModel.packageName, domainModel.className),
                    ),
                )
            } else {
                Flow::class.asClassName().parameterizedBy(
                    List::class.asClassName().parameterizedBy(
                        ClassName(domainModel.packageName, domainModel.className),
                    ),
                )
            }

        val funSpec =
            if (isRemote) {
                FunSpec
                    .builder("getAll${domainModel.className.capitalizeFirstChar()}")
                    .addModifiers(KModifier.SUSPEND)
                    .returns(returnType)
            } else {
                FunSpec
                    .builder("getAll${domainModel.className.capitalizeFirstChar()}")
                    .returns(returnType)
            }

        val finalFunSpec =
            if (isRemote) {
                funSpec.addRemoteDataSourceStatements(
                    dependency,
                )
            } else {
                funSpec.addLocalDataSourceStatements(dependency)
            }
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

    private fun FunSpec.Builder.addRemoteDataSourceStatements(dependency: DependencyClass) =
        this
            .addStatement("val result = ${dependency.className.lowerFirstChar()}.getAll${mvvmSubPath.makeGoodName()}()")
            .beginControlFlow("return if(result.isSuccessful && result.body() != null)")
            .addStatement("Result.success(result.body()!!)")
            .nextControlFlow("else")
            .addStatement("Result.failure(Throwable(%S))", "Unable to fetch")
            .endControlFlow()

    private fun FunSpec.Builder.addLocalDataSourceStatements(dependency: DependencyClass) =
        this
            .addStatement("return  ${dependency.className.lowerFirstChar()}.getAll${mvvmSubPath.makeGoodName()}()")

    companion object {
        fun Project.registerCreateMvvmSourceFiles(): TaskProvider<CreateMvvmSourceCodeFiles> =
            this.tasks.register(MvvmPluginConstant.TASK_CREATE_MVVM_SOURCE_CODES, CreateMvvmSourceCodeFiles::class.java) {
                // this task needs project's package name and other stuffs to generate the code
                dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_CREATE_MVVM_SOURCE_CODES_DESCRIPTION
            }
    }
}

data class DependencyClass(
    val packageName: String,
    val className: String,
)
