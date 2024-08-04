import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.CreateMvvmSourceCodeFiles.Companion.registerCreateMvvmSourceFiles
import tasks.GetProjectPackage.Companion.registerTaskGetProjectPackage
import tasks.mvvm.model.CreateModels.Companion.registerTaskCreateModels
import tasks.mvvm.model.database.GenerateEntityModelSourceFile.Companion.registerTaskGenerateEntityModels
import tasks.mvvm.model.domain.GenerateDomainModelSourceFile.Companion.registerTaskGenerateDomainModels
import tasks.mvvm.model.network.GenerateNetworkModelSourceFile.Companion.registerTaskGenerateNetworkModels
import java.io.File

/**
 * @author Vivek Gupta
 */
class MvvmArchPlugin : Plugin<Project> {
    companion object {
        var useKotlin = true
        var projectPath: String = ""
        var mvvmSubPath: String = "feature"
        var packageName: String? = ""
        var projectDir: File? = null
    }

    override fun apply(project: Project) {
        // val task =
        project.registerTaskGetProjectPackage()
        project.registerCreateMvvmSourceFiles()
        project.registerTaskCreateModels()
        project.registerTaskGenerateDomainModels()
        project.registerTaskGenerateNetworkModels()
        project.registerTaskGenerateEntityModels()
    }
}
