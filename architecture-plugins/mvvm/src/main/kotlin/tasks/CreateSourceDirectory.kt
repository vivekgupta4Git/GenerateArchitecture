package tasks

import MvvmArchPlugin
import MvvmArchPlugin.Companion.projectPath
import MvvmPluginConstant
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import architecture.AndroidExtension
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import extension.MvvmConfigurationExtension
import kotlinx.coroutines.flow.Flow
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import retrofit2.Response
import retrofit2.http.GET
import java.io.File
import java.io.Serializable
import java.util.Locale


/**
 *@author Vivek Gupta on
 */
abstract class CreateSourceDirectory : DefaultTask() {
    private var useKotlin = true
    private var mvvmSubPath: String = "feature"
    private val androidExtension = project.extensions.getByName<AndroidExtension>("android")

    @Option(
        option = "sub-path", description = """Generates mvvm inside the sub-path.
    Mvvm generates stuffs under main source directory + package name; 
    so if sub-path is given then mainSourceDirectory/packageName/subPath"""
    )
    fun setSubPath(subPath: String) {
        this.mvvmSubPath = subPath
    }

    @Option(
        option = "preferKotlin",
        description = """ This plugin generates code assuming you have kotlin sourceSets
    but if you have java sourceSets and you want to generate structure in the java sourceSets you can set this flag to false by
    using option --no-preferKotlin"""
    )
    fun setPreferKotlin(prefer: Boolean) {
        useKotlin = prefer
    }

    @TaskAction
    fun action() {
        //get the main Source set
        val mainSourceSet = project.layout.projectDirectory.dir("src/main")

        //check for main Source set
        if (!mainSourceSet.asFile.exists())
            throw Throwable("This plugin requires mainSourceSet (src/main)")

        //getting kotlin or java source set
         projectPath = if (mainSourceSet.dir("kotlin").asFile.exists() && useKotlin)
            mainSourceSet.dir("kotlin").asFile.path
        else
            mainSourceSet.dir("java").asFile.path

        //get mvvm Extension
        val extension = getExtension()

        //model extension
        val modelExtension = extension.model

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
        val projectDir = File(projectPath)
        val packageName = mvvmSubPath.getPackageName()
        val modifiedPackage = modelExtension
            .insideDirectory.get()
            .modifyPackageName(
                packageName,
                modelExtension.name.get()
            )

        //write domain model

        val domainModelsPackageName = "$modifiedPackage.domainModels"
        val domainModelClassName = "${mvvmSubPath.makeGoodName()}Model"
        projectDir.writeModelClass(
            packageName = domainModelsPackageName,
            className = domainModelClassName
        )
        //write network model
        val networkModelsPackageName = "$modifiedPackage.networkModels"
        val networkModelClassName = "${mvvmSubPath.makeGoodName()}NetworkModel"
        projectDir.writeModelClass(
            packageName = networkModelsPackageName,
            className = networkModelClassName,
            isNullable = true
        )
        //write entity model
        val entityPackageName = "$modifiedPackage.entities"
        val entityName = "${mvvmSubPath.makeGoodName()}Entity"
        projectDir.writeEntityClass(
            packageName = entityPackageName,
            entityName = entityName
        )

        //write rest api
        val restApiPackageName = "$modifiedPackage.restApi"
        val restApiName = "${mvvmSubPath.makeGoodName()}RestApis"
        projectDir.writeRestApi(
            packageName = restApiPackageName,
            restApiName = restApiName,
            restApiReturn = DependencyClass(networkModelsPackageName, networkModelClassName)
        )

        //write dao
        val daoPackageName = "$modifiedPackage.dao"
        val daoName = "${mvvmSubPath.makeGoodName()}Dao"
        projectDir.writeDao(
            packageName = daoPackageName,
            daoName = daoName,
            entityDependency = DependencyClass(entityPackageName,entityName),
        )
        //write data source
        val dataSourcePackageName = "$modifiedPackage.dataSources"
        val remoteDataSourceName = "${mvvmSubPath.makeGoodName()}RemoteDataSource"
        val remoteDependency = DependencyClass(restApiPackageName, restApiName)
        val remoteDomainModel = DependencyClass(networkModelsPackageName, networkModelClassName)
        projectDir.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = remoteDataSourceName,
            dependency = remoteDependency,
            domainModel = remoteDomainModel
        )
        //write local source
        val localDataSourceName = "${mvvmSubPath.makeGoodName()}LocalDataSource"
        val localDependency = DependencyClass(daoPackageName, daoName)
        val localDomainModel = DependencyClass(entityPackageName, entityName)
        projectDir.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = localDataSourceName,
            dependency = localDependency,
            domainModel = localDomainModel,
            isRemote = false
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

    private fun File.writeRestApi(
        packageName: String,
        restApiName: String,
        restApiReturn: DependencyClass
    ) {
        val response = Response::class.asClassName().parameterizedBy(
            List::class.asClassName().parameterizedBy(
            ClassName(restApiReturn.packageName, restApiReturn.className)
            )
        )
        val fileSpec = FileSpec.builder(packageName, restApiName)
            .addType(
                TypeSpec.interfaceBuilder(restApiName)
                    .addFunction(
                        FunSpec.builder("getAll${mvvmSubPath.makeGoodName()}")
                            .addModifiers(KModifier.ABSTRACT)
                            .addModifiers(KModifier.SUSPEND)
                            .addAnnotation(
                                AnnotationSpec.builder(GET::class)
                                    .addMember(
                                        "%S",
                                        "/api/${mvvmSubPath.makeGoodName().lowercase()}"
                                    )
                                    .build()
                            )
                            .returns(response)
                            .build()
                    )
                    .build()
            )
            .build()
        fileSpec.writeTo(this)
    }

   private fun File.writeDao(
       packageName: String,
       daoName: String,
       entityDependency : DependencyClass,
    ) {

        val response = Flow::class.asClassName().parameterizedBy(
            List::class.asClassName().parameterizedBy(
            ClassName(entityDependency.packageName, entityDependency.className)
        ))
        val fileSpec = FileSpec.builder(packageName, daoName)
            .addType(
                TypeSpec.interfaceBuilder(daoName)
                    .addAnnotation(AnnotationSpec.builder(Dao::class).build())
                    .addFunction(
                        FunSpec.builder("getAll${mvvmSubPath.makeGoodName()}")
                            .addModifiers(KModifier.ABSTRACT)
                            .addAnnotation(
                                AnnotationSpec.builder(Query::class)
                                    .addMember(
                                        "%S",
                                        "SELECT * FROM ${entityDependency.className.capitalizeFirstChar()}"
                                    )
                                    .build()
                            )
                            .returns(response)
                            .build()
                    )
                    .build()
            )
            .build()
        fileSpec.writeTo(this)
    }
    private fun File.writeEntityClass(packageName: String,
                                      entityName: String) {
        val fileSpec = FileSpec.builder(packageName, entityName)
            .addType(
                TypeSpec.classBuilder(entityName)
                    .addModifiers(KModifier.DATA)
                    .addAnnotation(AnnotationSpec.builder(Entity::class).build())
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(ParameterSpec.builder("id", Int::class)
                                .addAnnotation(
                                    AnnotationSpec.builder(PrimaryKey::class).build()
                                )
                                .build()
                            )
                            .addParameter("name", String::class.asTypeName().copy(nullable = true))
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("name", String::class.asTypeName().copy(nullable = true))
                            .initializer("name")
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("id",Int::class)
                            .initializer("id")
                            .build()
                    )
                    .build()
            )
            .build()
        fileSpec.writeTo(this)
    }

    private fun File.writeModelClass(packageName: String, className: String,isNullable :Boolean = false) {

        val fileSpec = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(className)
                    .addModifiers(KModifier.DATA)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("id",Int::class.asTypeName().copy(nullable = isNullable))
                            .addParameter("name", String::class.asTypeName().copy(nullable = isNullable))
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("id",Int::class.asTypeName().copy(nullable = isNullable))
                            .initializer("id")
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("name", String::class.asTypeName().copy(nullable = isNullable))
                            .initializer("name")
                            .build()
                    )
                    .addSuperIfNullable<Serializable>(isNullable)
                    .build()
            )
            .build()
        fileSpec.writeTo(this)
        /*
             def kotlinFile = new File(dir, "ModelClass.kt")
        kotlinFile.withWriter('UTF-8') {
            writer ->
                fileSpec.writeTo(writer)
        }
         */
        //  val file = File(dir, "ModelClass.kt")
        //  val outStream = file.writer(Charset.forName("UTF-8"))
        //  fileSpec.writeTo(outStream)

    }

    private fun File.writeDataSource(
        dataSourcePackageName: String,
        dataSourceName: String,
        dependency: DependencyClass,
        domainModel: DependencyClass,
        isRemote : Boolean = true
    ) {
        val returnType = if(isRemote)Result::class.asClassName().parameterizedBy(
            List::class.asClassName().parameterizedBy(
            ClassName(domainModel.packageName, domainModel.className))
        )
        else
            Flow::class.asClassName().parameterizedBy(
                List::class.asClassName().parameterizedBy(
                    ClassName(domainModel.packageName, domainModel.className)
                ))

        val funSpec = if(isRemote) FunSpec.builder("getAll${domainModel.className.capitalizeFirstChar()}")
            .addModifiers(KModifier.SUSPEND)
            .returns(returnType)
        else
            FunSpec.builder("getAll${domainModel.className.capitalizeFirstChar()}")
                .returns(returnType)

        val finalFunSpec = if(isRemote) funSpec.addRemoteDataSourceStatements(dependency) else funSpec.addLocalDataSourceStatements(dependency)
        val fileSpec = FileSpec.builder(dataSourcePackageName, dataSourceName)
            .addType(
                TypeSpec
                    .classBuilder(dataSourceName)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(
                                ParameterSpec.builder(
                                    dependency.className.lowerFirstChar(),
                                    ClassName(dependency.packageName, dependency.className)
                                )
                                    .build()
                            )
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(
                            dependency.className.lowerFirstChar(),
                            ClassName(dependency.packageName, dependency.className)
                        )
                            .initializer(dependency.className.lowerFirstChar())
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    .addFunction(
                            finalFunSpec
                            .build()
                    )

                    .build()
            ).build()
        fileSpec.writeTo(this)
    }

    private fun FunSpec.Builder.addRemoteDataSourceStatements(dependency: DependencyClass)= this
        .addStatement("val result = ${dependency.className.lowerFirstChar()}.getAll${mvvmSubPath.makeGoodName()}()")
            .beginControlFlow("return if(result.isSuccessful && result.body() != null)")
            .addStatement("Result.success(result.body()!!)")
            .nextControlFlow("else")
            .addStatement("Result.failure(Throwable(%S))","Unable to fetch")
            .endControlFlow()

    private fun FunSpec.Builder.addLocalDataSourceStatements(dependency: DependencyClass) = this
        .addStatement("return  ${dependency.className.lowerFirstChar()}.getAll${mvvmSubPath.makeGoodName()}()")



    private fun String.capitalizeFirstChar() = this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }

    private fun String.lowerFirstChar() = this.replaceFirstChar { char -> char.lowercase() }

    private fun String.modifyPackageName(pkg: String?, ext: String): String {
        val extName = ".${ext.replaceFirstChar { it.lowercase() }}"

        if (this.isEmpty())
            return if (pkg != null)
                pkg + extName
            else
                extName
        else {
            var separatedSubPath = ""
            val collection = this.split('/')
            collection.forEach {
                separatedSubPath = "$separatedSubPath.${it.lowerFirstChar()}"
            }
            return if (pkg != null)
                pkg + ".${separatedSubPath.trim()}" + extName
            else
                ".${separatedSubPath.trim()}" + extName
        }

    }


    private fun String.getPackageName(): String? {
        return if (this.isEmpty())
            androidExtension.namespace
        else {
            var separatedMvvmSubPath = ""
            val collection = this.replace('.', '/').split('/')
            collection.forEach {
                separatedMvvmSubPath = "$separatedMvvmSubPath.${it.lowerFirstChar()}"
            }

            androidExtension.namespace + separatedMvvmSubPath.trim()
        }
    }

    private fun getExtension(): MvvmConfigurationExtension {
        val extension = project.extensions.getByType<MvvmConfigurationExtension>()
        if (extension.model.name.get().isBlank() || extension.view.name.get()
                .isBlank() || extension.viewModel.name.get().isBlank()
        )
            throw Throwable("${MvvmPluginConstant.EXTENSION_NAME} is not properly configured; Please check for blank String")

        return extension
    }

    private fun String.makeGoodName() =
        this.replace('.', '/').split('/').last().capitalizeFirstChar()

    private inline fun <reified T>TypeSpec.Builder.addSuperIfNullable(isNullable: Boolean) = if(isNullable) this.addSuperinterface(T::class) else this

}

data class DependencyClass(val packageName: String, val className: String)
