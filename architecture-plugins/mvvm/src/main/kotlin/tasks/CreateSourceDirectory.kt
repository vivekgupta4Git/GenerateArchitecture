package tasks

import MvvmPluginConstant
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
import extension.MvvmConfigurationExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import retrofit2.Response
import retrofit2.http.GET
import java.io.File
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
        val projectPath = if (mainSourceSet.dir("kotlin").asFile.exists() && useKotlin)
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
        val domainModelsPackageName = "$modifiedPackage.domainModels"
        val domainModelClassName = "${mvvmSubPath.makeGoodName()}Model"
        projectDir.writeModelClass(
            packageName = domainModelsPackageName,
            className = domainModelClassName
        )
        val restApiPackageName = "$modifiedPackage.restApi"
        val restApiName = "${mvvmSubPath.makeGoodName()}RestApi"
        projectDir.writeRestApi(
            packageName = restApiPackageName,
            restApiName = restApiName,
            restApiReturn = RestApiReturn(domainModelsPackageName, domainModelClassName)
        )

        val dataSourcePackageName = "$modifiedPackage.datasource"
        val remoteDataSourceName = "${mvvmSubPath.makeGoodName()}RemoteDataSource"
        val remoteDependency = DependencyClass(restApiPackageName, restApiName)
        val remoteDomainModel = DependencyClass(domainModelsPackageName, domainModelClassName)
        projectDir.writeDataSource(
            dataSourcePackageName = dataSourcePackageName,
            dataSourceName = remoteDataSourceName,
            dependency = remoteDependency,
            domainModel = remoteDomainModel
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

    private fun createInterfaceFile(
        dir: File,
        extensionName: String,
        subPath: String,
        mvvmSubPath: String
    ) {
        val packageName = mvvmSubPath.getPackageName()
        val modifiedPackage = subPath.modifyPackageName(packageName, extensionName)
        /*    writeRestApiInterface(dir,
                modifiedPackage,
                subModule = subPath.split('/').last(),
                modifiedPackage,
                "${extensionName.capitalizeFirstChar()}Class")*/

    }

    private fun createModelFile(
        dir: File,
        extensionName: String,
        subPath: String,
        mvvmSubPath: String
    ) {
        val packageName = mvvmSubPath.getPackageName()
        val modifiedPackage = subPath.modifyPackageName(packageName, extensionName)
        //  writeModelClass(dir, modifiedPackage,extensionName)

    }

    private fun createDataSourceFile(
        dir: File,
        extensionName: String,
        subPath: String,
        mvvmSubPath: String
    ) {
        val packageName = mvvmSubPath.getPackageName()
        val modifiedPackage = subPath.modifyPackageName(packageName, extensionName)
        writeRemoteDataSource(
            dir,
            modifiedPackage,
            subModule = subPath,
            "",
            modifiedPackage,
            "${extensionName.capitalizeFirstChar()}Class"
        )

    }

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

    private fun createInsideDirectoryIfAvailable(path: String, dir: File, field: String): File {
        val newPath = if (path.isEmpty())
            "${dir.path}/${field.lowercase()}"
        else
            "${dir.path}/${path.lowercase().replace('.', '/')}/${field.lowercase()}"

        val fileWithNewPath = File(newPath)
        //     if (!fileWithNewPath.exists())
        //       fileWithNewPath.mkdirs()

        return fileWithNewPath
    }

    private fun File.writeRestApi(
        packageName: String,
        restApiName: String,
        restApiReturn: RestApiReturn
    ) {
        val response = Response::class.asClassName().parameterizedBy(
            ClassName(restApiReturn.packageName, restApiReturn.className)
        )
        val fileSpec = FileSpec.builder(packageName, restApiName)
            .addType(
                TypeSpec.interfaceBuilder(restApiName)
                    .addFunction(
                        FunSpec.builder("get${mvvmSubPath.makeGoodName()}")
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

    private fun File.writeModelClass(packageName: String, className: String) {

        val fileSpec = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(className)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("name", String::class)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("name", String::class)
                            .initializer("name")
                            .build()
                    )
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
        domainModel: DependencyClass
    ) {
        val returnType = Result::class.asClassName().parameterizedBy(
            ClassName(domainModel.packageName, domainModel.className)
        )
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
                        FunSpec.builder("get${domainModel.className.capitalizeFirstChar()}")
                            .addModifiers(KModifier.SUSPEND)
                            .returns(returnType)
                            .addStatement("val result = ${dependency.className.lowerFirstChar()}.get${mvvmSubPath.makeGoodName()}()")
                            .beginControlFlow("return if(result.isSuccessful && result.body() != null)")
                            .addStatement("Result.success(result.body()!!)")
                            .nextControlFlow("else")
                            .addStatement("Result.failure(Throwable(%S))","Unable to fetch")
                            .endControlFlow()
                            .build()
                    )

                    .build()
            ).build()
        fileSpec.writeTo(this)
    }


    private fun writeRemoteDataSource(
        dir: File,
        packageName: String,
        subModule: String,
        modelName: String = "",
        restApiInterfacePackage: String,
        interfaceName: String = "${subModule.capitalizeFirstChar()}Api",
    ) {
        val className = "${subModule.capitalizeFirstChar()}DataSource"

        val fileSpec = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(className)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(
                                ParameterSpec.builder(
                                    "${subModule}Api",
                                    ClassName(restApiInterfacePackage, interfaceName)
                                )
                                    .build()
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("get$subModule")
                            .addStatement("val result = ${subModule}Api.get${subModule}()")
                            .build()
                    )
                    .build()
            )
            .build()
        fileSpec.writeTo(dir)
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


    private fun String.capitalizeFirstChar() = this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }

    private fun String.lowerFirstChar() = this.replaceFirstChar { char -> char.lowercase() }


}

data class RestApiReturn(val packageName: String, val className: String)
data class DependencyClass(val packageName: String, val className: String)
