package tasks

import architecture.AndroidExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import java.io.File

/**
 *@author Vivek Gupta on
 */
abstract class CreateSourceDirectory : DefaultTask() {
    private var useKotlin = true
    private var mvvmSubPath: String = ""

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
        val projectPath = if (mainSourceSet.dir("kotlin").asFile.exists())
            mainSourceSet.dir("kotlin").asFile.path
        else
            mainSourceSet.dir("java").asFile.path

        val androidExtension = project.extensions.getByName<AndroidExtension>("android")
        val nameSpace = androidExtension.namespace?.replace('.','/')
        val finalMvvmPath = if(mvvmSubPath.isNotBlank())
                                     "$projectPath/$nameSpace/$mvvmSubPath"
                            else
                                    "$projectPath/$nameSpace"

        val file = File(finalMvvmPath)

        if(!file.exists())
            file.mkdirs()

        println(file.path)
    }

}