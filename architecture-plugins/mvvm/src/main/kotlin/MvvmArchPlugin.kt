import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.CreateMvvmSourceCodeFiles.Companion.registerCreateMvvmSourceFiles
import tasks.GetProjectPackage.Companion.registerTaskGetProjectPackage
import tasks.mvvm.model.CreateModels.Companion.registerTaskCreateModels
import tasks.mvvm.model.database.GenerateDaoSourceFile.Companion.registerTaskGenerateDao
import tasks.mvvm.model.database.GenerateEntityModelSourceFile.Companion.registerTaskGenerateEntityModels
import tasks.mvvm.model.domain.GenerateDomainModelSourceFile.Companion.registerTaskGenerateDomainModels
import tasks.mvvm.model.network.GenerateNetworkModelSourceFile.Companion.registerTaskGenerateNetworkModels
import tasks.mvvm.model.network.GenerateRestApiSourceFile.Companion.registerTaskGenerateRestApi
import java.io.File

/**
 * @author Vivek Gupta
 */
class MvvmArchPlugin : Plugin<Project> {
    companion object {
        var useKotlin = true
        var projectPath: String = ""
        var mvvmSubPath: String = ""
        var packageName: String? = ""
        var projectDir: File? = null
    }

    override fun apply(project: Project) {
        // no cache
            // val task =
            with(project) {
                registerTaskGetProjectPackage()
                registerCreateMvvmSourceFiles()
                registerTaskCreateModels()
                registerTaskGenerateDomainModels()
                registerTaskGenerateNetworkModels()
                registerTaskGenerateEntityModels()
                registerTaskGenerateRestApi()
                registerTaskGenerateDao()
            }
    }
}
