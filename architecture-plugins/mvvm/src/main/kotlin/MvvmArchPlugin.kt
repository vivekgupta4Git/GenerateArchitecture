import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.registerIfAbsent
import service.ProjectPathService
import tasks.CreateMvvmSourceCodeFiles.Companion.registerCreateMvvmSourceFiles
import tasks.GetProjectPackage.Companion.registerTaskGetProjectPackage
import tasks.mvvm.model.CreateModels.Companion.registerTaskCreateModels
import tasks.mvvm.model.database.GenerateDaoSourceFile.Companion.registerTaskGenerateDao
import tasks.mvvm.model.database.GenerateEntityModelSourceFile.Companion.registerTaskGenerateEntityModels
import tasks.mvvm.model.database.GenerateLocalDataSource.Companion.registerTaskGenerateLocalDataSource
import tasks.mvvm.model.domain.GenerateDomainModelSourceFile.Companion.registerTaskGenerateDomainModels
import tasks.mvvm.model.network.GenerateNetworkModelSourceFile.Companion.registerTaskGenerateNetworkModels
import tasks.mvvm.model.network.GenerateRemoteDataSource.Companion.registerTaskGenerateRemoteDataSource
import tasks.mvvm.model.network.GenerateRestApiSourceFile.Companion.registerTaskGenerateRestApi

/**
 * @author Vivek Gupta
 */
class MvvmArchPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val serviceProvider =
            project.gradle.sharedServices.registerIfAbsent("projectPathService", ProjectPathService::class) {
                with(parameters) {
                    projectPath.set("")
                    packageName.set("")
                    useKotlin.set(true)
                    mvvmSubPath.set("")
                }
            }

        with(project) {
            registerTaskGetProjectPackage(serviceProvider)
            registerCreateMvvmSourceFiles(serviceProvider)
            registerTaskCreateModels(serviceProvider)
            registerTaskGenerateDomainModels(serviceProvider)
            registerTaskGenerateNetworkModels(serviceProvider)
            registerTaskGenerateEntityModels(serviceProvider)
            registerTaskGenerateRestApi(serviceProvider)
            registerTaskGenerateDao(serviceProvider)
            registerTaskGenerateRemoteDataSource(serviceProvider)
            registerTaskGenerateLocalDataSource(serviceProvider)
        }
    }
}
