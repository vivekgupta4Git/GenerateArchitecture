package tasks.mvvm.model.dto

import MvvmPluginConstant
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import service.ProjectPathService
import tasks.DependencyClass
import tasks.OptionTask
import utils.TaskUtil.getExtension
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class GenerateMapperSourceFile : OptionTask(){

    @TaskAction
    fun action(){
        val projectPath =
            projectPathService
                .get()
                .parameters.projectPath
                .get()
        val packageName =
            projectPathService
                .get()
                .parameters.packageName
                .get()
        val domainName =
            projectPathService
                .get()
                .parameters.domainName
                .get()
        val projectDir = File(projectPath)
        // model extension
        val modelExtension = getExtension(project).model
        val modifiedPackage =
            modelExtension
                .insideDirectory
                .get()
                .modifyPackageName(
                    packageName,
                    modelExtension.name.get(),
                )

        val domainModelsPackageName = "$modifiedPackage.domainModels"
        val domainModelClassName = "${domainName}Model"

        val entityPackageName = "$modifiedPackage.entities"
        val entityName = "${domainName}Entity"

        val dtoPackageName = "$modifiedPackage.mapper"

        val networkModelsPackageName = "$modifiedPackage.networkModels"
        val networkModelClassName = "${domainName}NetworkModel"

        projectDir.writeEntityToDomain(
            dtoPackageName = dtoPackageName,
            domainDependency = DependencyClass(domainModelsPackageName, domainModelClassName),
            entityDependency = DependencyClass(entityPackageName, entityName),
        )
        projectDir.writeDomainToEntity(
            dtoPackageName = dtoPackageName,
            domainDependency = DependencyClass(domainModelsPackageName, domainModelClassName),
            entityDependency = DependencyClass(entityPackageName, entityName),
        )
      projectDir.writeNetworkToEntity(
            dtoPackageName = dtoPackageName,
            networkDependency = DependencyClass(networkModelsPackageName, networkModelClassName),
            entityDependency = DependencyClass(entityPackageName, entityName),
        )
        projectDir.writeEntityToNetwork(
            dtoPackageName = dtoPackageName,
            networkDependency = DependencyClass(networkModelsPackageName, networkModelClassName),
            entityDependency = DependencyClass(entityPackageName, entityName),
        )

    }
    private fun File.writeEntityToDomain(
        dtoPackageName : String,
        domainDependency : DependencyClass,
        entityDependency : DependencyClass,
    ){
        val fileSpec = FileSpec
            .builder(dtoPackageName,"EntityToDomain")
            .addFunction(
                FunSpec.builder("to${domainDependency.className}")
                    .receiver(ClassName(entityDependency.packageName,entityDependency.className))
                    .addStatement( "return ${domainDependency.className}( id = this.id, name = this.name ?: \"\"  )")
                    .returns(ClassName(domainDependency.packageName,domainDependency.className))
                    .build()
            )
            .addFunction(
                FunSpec.builder("toList${domainDependency.className}")
                    .receiver(
                        List::class.asClassName().parameterizedBy(
                            ClassName(entityDependency.packageName,entityDependency.className)
                        )
                    )
                    .returns(
                        List::class.asClassName().parameterizedBy(
                            ClassName(domainDependency.packageName,domainDependency.className)
                        )
                    )
                    .addStatement("return map { it.to${domainDependency.className}() }.filter{ it.name.isNotBlank() }")
                    .build()
            )
            .build()
        fileSpec.writeTo(this)
    }

    private fun File.writeDomainToEntity(
        dtoPackageName : String,
        domainDependency : DependencyClass,
        entityDependency : DependencyClass,
    ){
        val fileSpec = FileSpec
            .builder(dtoPackageName,"DomainToEntity")
            .addFunction(
                FunSpec.builder("to${entityDependency.className}")
                    .receiver(ClassName(domainDependency.packageName,domainDependency.className))
                    .addStatement( "return ${entityDependency.className}( id = this.id, name = this.name)")
                    .returns(ClassName(entityDependency.packageName,entityDependency.className))
                    .build()
            )
            .addFunction(
                FunSpec.builder("toList${entityDependency.className}")
                    .receiver(
                        List::class.asClassName().parameterizedBy(
                            ClassName(domainDependency.packageName,domainDependency.className)
                        )
                    )
                    .returns(
                        List::class.asClassName().parameterizedBy(
                            ClassName(entityDependency.packageName,entityDependency.className)
                        )
                    )
                    .addStatement("return map { it.to${entityDependency.className}() }")
                    .build()
            )
            .build()

        fileSpec.writeTo(this)
    }
    private fun File.writeNetworkToEntity(
        dtoPackageName : String,
        networkDependency : DependencyClass,
        entityDependency : DependencyClass,
    ){
        val fileSpec = FileSpec
            .builder(dtoPackageName,"NetworkToEntity")
            .addFunction(
                FunSpec.builder("to${entityDependency.className}")
                    .receiver(ClassName(networkDependency.packageName,networkDependency.className))
                    .addStatement( "return ${entityDependency.className}( id = this.id ?: \"\", name = this.name)")
                    .returns(ClassName(entityDependency.packageName,entityDependency.className))
                    .build()
            )
            .addFunction(
                FunSpec.builder("toList${entityDependency.className}")
                    .receiver(
                        List::class.asClassName().parameterizedBy(
                            ClassName(networkDependency.packageName,networkDependency.className)
                        )
                    )
                    .returns(
                        List::class.asClassName().parameterizedBy(
                            ClassName(entityDependency.packageName,entityDependency.className)
                        )
                    )
                    .addStatement("return map { it.to${entityDependency.className}() }.filter{ it.id.isNotBlank() }")
                    .build()
            )
            .build()
       fileSpec.writeTo(this)
    }
    private fun File.writeEntityToNetwork(
        dtoPackageName : String,
        networkDependency : DependencyClass,
        entityDependency : DependencyClass,
    ){
        val fileSpec = FileSpec
            .builder(dtoPackageName,"EntityToNetwork")
            .addFunction(
                FunSpec.builder("to${networkDependency.className}")
                    .receiver(ClassName(entityDependency.packageName,entityDependency.className))
                    .addStatement( "return ${networkDependency.className}( id = this.id , name = this.name)")
                    .returns(ClassName(networkDependency.packageName,networkDependency.className))
                    .build()
            )
            .addFunction(
                FunSpec.builder("toList${networkDependency.className}")
                    .receiver(
                        List::class.asClassName().parameterizedBy(
                            ClassName(entityDependency.packageName,entityDependency.className)
                        )
                    )
                    .returns(
                        List::class.asClassName().parameterizedBy(
                            ClassName(networkDependency.packageName,networkDependency.className)
                        )
                    )
                    .addStatement("return map { it.to${networkDependency.className}() }")
                    .build()
            )
            .build()
        fileSpec.writeTo(this)
    }
    companion object {
        fun Project.registerTaskMapper(
            serviceProvider: Provider<ProjectPathService>,
        ): TaskProvider<GenerateMapperSourceFile> =
            this.tasks.register(MvvmPluginConstant.TASK_GENERATE_MAPPER,GenerateMapperSourceFile::class.java) {
                dependsOn(MvvmPluginConstant.TASK_GENERATE_DOMAIN_MODELS)
                dependsOn(MvvmPluginConstant.TASK_GENERATE_ENTITY_MODELS)
                dependsOn(MvvmPluginConstant.TASK_GENERATE_NETWORK_MODELS)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_GENERATE_MAPPER_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}
