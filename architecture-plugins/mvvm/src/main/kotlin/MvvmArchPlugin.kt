import extension.MvvmConfigurationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import tasks.CreateSourceDirectory
import tasks.GetProjectPackage
import java.io.File

/**
 * @author Vivek Gupta
 */
class MvvmArchPlugin : Plugin<Project>{
   companion object{
       var useKotlin = true
       var projectPath : String = ""
       var mvvmSubPath : String = "feature"
       var packageName : String? = ""
       var projectDir : File? = null
   }
    override fun apply(project: Project) {
     // val task =
          project.tasks.register(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE,GetProjectPackage::class.java){
              group = MvvmPluginConstant.PLUGIN_GROUP
              description = MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE_DESCRIPTION
          }
          project.registerTaskCreateSourceDirectory()
    }
}

fun Project.registerTaskCreateSourceDirectory() : TaskProvider<CreateSourceDirectory>{
    val mvvmConfigurationExtension = project.extensions.create(
        MvvmPluginConstant.EXTENSION_NAME,
        MvvmConfigurationExtension::class.java
    )
    return  tasks.register(MvvmPluginConstant.TASK_CREATE_DIRECTORY,CreateSourceDirectory::class.java){
        //this task needs project's package name and other stuffs to generate the code
        dependsOn(MvvmPluginConstant.TASK_GET_PROJECT_PACKAGE)

        group = MvvmPluginConstant.PLUGIN_GROUP
        description = MvvmPluginConstant.TASK_CREATE_DIRECTORY_DESCRIPTION
        mvvmConfigurationExtension.model{
            name.convention("model")
            insideDirectory.convention("")
        }
        mvvmConfigurationExtension.viewModel {
            name.convention("viewModel")
            insideDirectory.convention("")
        }
        mvvmConfigurationExtension.view {
            name.convention("view")
            insideDirectory.convention("")
        }
    }
}