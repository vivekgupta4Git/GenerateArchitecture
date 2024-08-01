import extension.MvvmConfigurationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import tasks.CreateSourceDirectory

class MvvmArchPlugin : Plugin<Project>{
    override fun apply(project: Project) {
     // val task =
          project.registerTaskCreateSourceDirectory()

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