package tasks

import MvvmPluginConstant
import architecture.AndroidExtension
import extension.MvvmConfigurationExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import java.io.File


/**
 *@author Vivek Gupta on
 */
abstract class CreateSourceDirectory : DefaultTask() {
    private var useKotlin = true
    private var mvvmSubPath: String = ""
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
        val mainSourceSet = project.layout.projectDirectory.dir("src/main")

        if (!mainSourceSet.asFile.exists())
            throw Throwable("This plugin requires mainSourceSet (src/main)")

        val projectPath = if (mainSourceSet.dir("kotlin").asFile.exists())
            mainSourceSet.dir("kotlin").asFile.path
        else
            mainSourceSet.dir("java").asFile.path

        val nameSpace = androidExtension.namespace?.replace('.', '/')
        val finalMvvmPath = if (mvvmSubPath.isNotBlank())
            "$projectPath/$nameSpace/$mvvmSubPath"
        else
            "$projectPath/$nameSpace"

        val file = File(finalMvvmPath)

        if (!file.exists())
            file.mkdirs()

        println(file.path)
        val extension = getExtension()
        val modelExtension = extension.model
        val modelFile =
            createSubPathIfAvailable(modelExtension.subPath.get(), file, modelExtension.name.get())

      /*  createModelFile(
            modelFile,
            modelExtension.name.get(),
            modelExtension.subPath.get(),
            mvvmSubPath
        )*/


        val viewExtension = extension.view
        val viewFile =
            createSubPathIfAvailable(viewExtension.subPath.get(), file, viewExtension.name.get())

        val viewModelExtension = extension.view
        val viewModelFile = createSubPathIfAvailable(
            viewModelExtension.subPath.get(),
            file,
            viewModelExtension.name.get()
        )
    }

    private fun createModelFile(
        dir: File,
        extensionName: String,
        subPath: String,
        mvvmSubPath: String
    ) {
        val packageName = mvvmSubPath.getPackageName()
        val modifiedPackage = subPath.modifyPackageName(packageName, extensionName)
       // writeModelClass(dir, modifiedPackage)

    }

/*    private fun writeModelClass(dir: File, packageName: String) {
        val fileSpec = FileSpec.builder(packageName.lowercase(), "ModelClass")
            .addType(
                TypeSpec.classBuilder("Model")
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

        val kotlinFile = File(dir, "ModelClass.kt")
        fileSpec.writeTo(kotlinFile)

    }*/


    private fun String.modifyPackageName(pkg: String?, ext: String): String {
        return if (this.isEmpty())
            if (pkg != null)
                pkg + ".${ext}"
            else
                ".$ext"
        else {
            var separatedSubPath = ""
            val collection = this.split('/')
            collection.forEach {
                separatedSubPath = "$separatedSubPath.$it"
            }
            pkg + separatedSubPath.trim() + ext
        }

    }


    private fun String.getPackageName(): String? {
        return if (this.isEmpty())
            androidExtension.namespace
        else {
            var separatedMvvmSubPath = ""
            val collection = this.split('/')
            collection.forEach {
                separatedMvvmSubPath = "$separatedMvvmSubPath.$it"
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

    private fun createSubPathIfAvailable(path: String, dir: File, field: String): File {
        val newPath = if (path.isEmpty()) "${dir.path}/${field.lowercase()}" else "${dir.path}/${
            path.lowercase().replace('.', '/')
        }/${field.lowercase()}"
        val fileWithNewPath = File(newPath)
        if (!fileWithNewPath.exists())
            fileWithNewPath.mkdirs()

        return fileWithNewPath
    }

}