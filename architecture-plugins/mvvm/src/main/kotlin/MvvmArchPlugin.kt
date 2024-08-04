import extension.MvvmConfigurationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import tasks.CreateSourceDirectory
/**
 * @author Vivek Gupta
 */
class MvvmArchPlugin : Plugin<Project>{
   companion object{
       var projectPath : String = ""
   }
    override fun apply(project: Project) {
     // val task =
          project.registerTaskCreateSourceDirectory()
          project.tasks.register("testTask"){
              dependsOn(MvvmPluginConstant.TASK_CREATE_DIRECTORY)
              println(projectPath)
          }
    }
}

fun Project.registerTaskCreateSourceDirectory() : TaskProvider<CreateSourceDirectory>{
    val mvvmConfigurationExtension = project.extensions.create(
        MvvmPluginConstant.EXTENSION_NAME,
        MvvmConfigurationExtension::class.java
    )
    return  tasks.register(MvvmPluginConstant.TASK_CREATE_DIRECTORY,CreateSourceDirectory::class.java){
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