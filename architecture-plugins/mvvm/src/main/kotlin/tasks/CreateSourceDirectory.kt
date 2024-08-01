package tasks

import MvvmPluginConstant
import architecture.AndroidExtension
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import extension.MvvmConfigurationExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.util.Locale


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

        val projectPath = if (mainSourceSet.dir("kotlin").asFile.exists() && useKotlin)
            mainSourceSet.dir("kotlin").asFile.path
        else
            mainSourceSet.dir("java").asFile.path

      //  val nameSpace = androidExtension.namespace?.replace('.', '/')
     //   val finalMvvmPath = if (mvvmSubPath.isNotBlank())
     //       "$projectPath/$nameSpace/$mvvmSubPath"
     //   else
     //       "$projectPath/$nameSpace"

       // val file = File(finalMvvmPath)

      //  if (!file.exists())
        //    file.mkdirs()

        val extension = getExtension()
        val modelExtension = extension.model
      //  val modelFile =
      //      createInsideDirectoryIfAvailable(modelExtension.insideDirectory.get(), file, modelExtension.name.get())
      //  println("Model file = ${modelFile.path}")

        createModelFile(
            File(projectPath),
            modelExtension.name.get(),
            modelExtension.insideDirectory.get(),
            mvvmSubPath
        )


        val viewExtension = extension.view
      //  val viewFile =
      //      createInsideDirectoryIfAvailable(viewExtension.insideDirectory.get(), file, viewExtension.name.get())
        createModelFile(
            File(projectPath),
            viewExtension.name.get(),
            viewExtension.insideDirectory.get(),
            mvvmSubPath
        )
        val viewModelExtension = extension.viewModel
      //  val viewModelFile = createInsideDirectoryIfAvailable(
     //       viewModelExtension.insideDirectory.get(),
     //       file,
      //      viewModelExtension.name.get()
       // )
        createModelFile(
            File(projectPath),
            viewModelExtension.name.get(),
            viewModelExtension.insideDirectory.get(),
            mvvmSubPath
        )
    }

    private fun createModelFile(
        dir: File,
        extensionName: String,
        subPath: String,
        mvvmSubPath: String
    ) {
        val packageName = mvvmSubPath.getPackageName()
        println("package Name = $packageName")
        val modifiedPackage = subPath.modifyPackageName(packageName, extensionName)
        println("modified Package = $modifiedPackage")
        println("director = ${dir.path}")
        writeModelClass(dir, modifiedPackage,extensionName)

    }

    private fun writeModelClass(dir: File, packageName: String,ext: String) {
        val className = "${ext.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }}Class"

        val fileSpec = FileSpec.builder(packageName, "${className}File")
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
            if (pkg != null)
                pkg + ".${separatedSubPath.trim()}" + ".$ext"
            else
                 ".${separatedSubPath.trim()}" + ".$ext"
        }

    }


    private fun String.getPackageName(): String? {
        return if (this.isEmpty())
            androidExtension.namespace
        else {
            var separatedMvvmSubPath = ""
            val collection = this.lowercase().replace('.','/').split('/')
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

}