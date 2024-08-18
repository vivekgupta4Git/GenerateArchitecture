package tasks.mvvm.viewmodel

import MvvmPluginConstant
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import kotlinx.coroutines.flow.StateFlow
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import service.ProjectPathService
import tasks.OptionTask
import utils.TaskUtil.getExtension
import utils.TaskUtil.lowerFirstChar
import utils.TaskUtil.modifyPackageName
import java.io.File

abstract class CreateViewModel : OptionTask() {

    @TaskAction
    override fun action(){
        super.action()
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
        // get mvvm Extension
        val extension = getExtension(project)

        // model extension
        val modelExtension = extension.model
        val viewModelExtension = extension.viewModel

        // modify package based on the model extension -inside directory
        val modifiedPackage = modelExtension.insideDirectory
                .get()
                .modifyPackageName(
                    packageName,
                    modelExtension.name.get(),
                )

        val viewModelModifiedPackage = viewModelExtension.insideDirectory
            .get()
            .modifyPackageName(packageName,viewModelExtension.name.get())

        val explicitPath = projectPathService.get()
            .parameters.explicitPath.get()

        val domainModelsPackageName = explicitPath.ifBlank {  "$modifiedPackage.domainModels" }
        val domainModelClassName = "${domainName}Model"
        val repositoryPackageName = explicitPath.ifBlank {  "$modifiedPackage.repository" }
        val repositoryClassName = "${domainName}Repository"

        val viewModelPackageName = explicitPath.ifBlank { viewModelModifiedPackage }
        val viewModelClassName = "${domainName}ViewModel"

        projectDir.writeViewModelClass(
            repositoryInterfaceName = repositoryClassName,
            repositoryPackageName = repositoryPackageName,
            domainModelClassName = domainModelClassName,
            domainModelsPackageName = domainModelsPackageName,
            packageName = viewModelPackageName,
            className = viewModelClassName,
            domainName = domainName
        )
    }
    private fun File.writeViewModelClass(
        repositoryInterfaceName: String,
        repositoryPackageName: String,
        domainModelClassName: String,
        domainModelsPackageName: String,
        packageName: String,
        className: String,
        domainName : String,
        ){
        FileSpec.builder(packageName, className)
            .addImport("kotlinx.coroutines.flow","stateIn")
            .addImport("kotlinx.coroutines.flow","SharingStarted")
            .addImport("androidx.lifecycle","viewModelScope")
            .addImport("kotlinx.coroutines","launch")
            .addImport("kotlinx.coroutines.flow", "MutableStateFlow")
            .addImport("kotlinx.coroutines.flow", "StateFlow")
            .addImport("kotlinx.coroutines.flow", "asStateFlow")
            .addImport("kotlinx.coroutines.flow", "update")
            .addType(
                TypeSpec.classBuilder(className)
                    .superclass(ClassName("androidx.lifecycle","ViewModel"))
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter(
                                ParameterSpec.builder(repositoryInterfaceName.lowerFirstChar(),
                                    ClassName(repositoryPackageName,repositoryInterfaceName))
                                    .build()
                            )
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder(repositoryInterfaceName.lowerFirstChar(),
                            ClassName(repositoryPackageName,repositoryInterfaceName))
                            .addModifiers(KModifier.PRIVATE)
                            .initializer(repositoryInterfaceName.lowerFirstChar())
                            .build()
                    )
                    .addProperty(
                        mutableStateFlowModel(
                            domainModelClassName = domainModelClassName,
                            domainModelsPackageName = domainModelsPackageName,
                            domainName = domainName
                        )
                    )
                    .addProperty(asStateFlowModel(domainName, domainModelClassName, domainModelsPackageName))
                    .addProperty(mutableStateFlowException(domainName = domainName))
                    .addProperty(asStateFlowException(domainName))
                    .addProperty(getAllProperty(
                        repositoryInterfaceName = repositoryInterfaceName,
                        domainModelClassName = domainModelClassName,
                        domainModelsPackageName = domainModelsPackageName,
                        domainName = domainName
                    ))
                    .addFunction(insert(
                        repositoryInterfaceName = repositoryInterfaceName,
                        domainModelClassName = domainModelClassName,
                        domainModelsPackageName = domainModelsPackageName,
                        domainName = domainName
                    ))
                    .addFunction(
                        delete(
                            repositoryInterfaceName = repositoryInterfaceName,
                            domainModelClassName = domainModelClassName,
                            domainModelsPackageName = domainModelsPackageName,
                            domainName = domainName)
                    )
                    .addFunction(
                        update(
                            repositoryInterfaceName = repositoryInterfaceName,
                            domainModelClassName = domainModelClassName,
                            domainModelsPackageName = domainModelsPackageName,
                            domainName = domainName
                        )
                    )
                    .addFunction(
                        getFeatureById(
                            repositoryInterfaceName = repositoryInterfaceName,
                            domainName = domainName
                        )
                    )

                    .build()
            )
            .build()
            .writeTo(this)

    }

    /**
     * private val _feature = MutableStateFlow<FeatureModel?>(null)
     */
    private fun mutableStateFlowModel(
        domainModelClassName: String,
        domainModelsPackageName: String,
        domainName : String,
    ): PropertySpec {
        return PropertySpec
                    .builder(
                    "_${domainName.lowerFirstChar()}",
                    ClassName("kotlinx.coroutines.flow", "MutableStateFlow").parameterizedBy(
                    ClassName(domainModelsPackageName,domainModelClassName).copy(nullable = true))
                ).addModifiers(KModifier.PRIVATE)
                    .initializer("MutableStateFlow(null)")
            .build()
    }

    /**
     *     val feature = _feature.asStateFlow()
     */
    private fun asStateFlowModel(
        domainName : String,
        domainModelClassName: String,
        domainModelsPackageName: String,
    ) : PropertySpec{
            return  PropertySpec
                .builder(
                    domainName.lowerFirstChar(),
                    ClassName("kotlinx.coroutines.flow", "StateFlow").parameterizedBy(
                        ClassName(domainModelsPackageName,domainModelClassName).copy(nullable = true)
                    )
                )
                .initializer("_${domainName.lowerFirstChar()}.asStateFlow()")
                .build()

    }

    /**
     *     val featureException = _featureException.asStateFlow()
     */
    private fun asStateFlowException(
        domainName: String
    ) : PropertySpec{
        return PropertySpec.builder( "${domainName.lowerFirstChar()}Exception",
                    ClassName("kotlinx.coroutines.flow", "StateFlow").parameterizedBy(
                        ClassName("kotlin", "Throwable").copy(nullable = true)
                    )
            )
            .initializer("_${domainName.lowerFirstChar()}Exception.asStateFlow()")
            .build()

    }

    /**
     *
     * private val _featureException = MutableStateFlow<Throwable?>(null)
     *
     */
    private fun mutableStateFlowException(
        domainName : String,
    ): PropertySpec {
        return PropertySpec
            .builder(
                "_${domainName.lowerFirstChar()}Exception",
                ClassName("kotlinx.coroutines.flow", "MutableStateFlow").parameterizedBy(
                    ClassName("kotlin", "Throwable").copy(nullable = true)
                )
            ).addModifiers(KModifier.PRIVATE)
            .initializer("MutableStateFlow(null)")
            .build()
    }


    /**
     * This method generates following code :
     *
     *     fun getFeatureById(id: String) {
     *         viewModelScope.launch {
     *             featureRepository.getFeatureById(id).onSuccess {
     *                 _featureById.update { it }
     *             }.onFailure {
     *                 _featureException.update { it }
     *             }
     *         }
     *     }
     */
    private fun getFeatureById(
        repositoryInterfaceName: String,
        domainName: String,
    ) : FunSpec {
        return FunSpec.builder("get${domainName}ById")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(
                ParameterSpec.builder("id", String::class)
                    .build()
            )
            .addCode(
                """
                    viewModelScope.launch {
                        ${repositoryInterfaceName.lowerFirstChar()}.get${domainName}ById(id)
                        .onSuccess {
                            _${domainName.lowerFirstChar()}.update { it }
                        }.onFailure {
                            _${domainName.lowerFirstChar()}Exception.update { it }
                        }
                    }
                """
                    .trimIndent()
            )

            .build()
    }


    /**
     * This method generates following code : 
     *   public val allFeature: StateFlow<Result<List<FeatureModel>>> = featureRepository.getAllFeature()
     *       .stateIn(viewModelScope,
     *                SharingStarted.WhileSubscribed(5000),
     *                Result.success(emptyList()))
     */
    private fun getAllProperty(
        repositoryInterfaceName: String,
        domainModelClassName: String,
        domainModelsPackageName: String,
        domainName: String,
    ): PropertySpec {
        val propertyType =
            StateFlow::class.asClassName().parameterizedBy(
                Result::class.asClassName().parameterizedBy(
                    List::class.asClassName().parameterizedBy(
                        ClassName(domainModelsPackageName, domainModelClassName)
                    )
                )
            )
      return  PropertySpec.builder("all$domainName",propertyType)
          .initializer("""${repositoryInterfaceName.lowerFirstChar()}.getAll$domainName()
              |.stateIn(viewModelScope,
              |         SharingStarted.WhileSubscribed(5000),
              |         Result.success(emptyList()))
          """.trimMargin())
          .build()
    }

    /**
     * This method generates following code :
     *   fun insertFeature(feature: FeatureModel) {
     *             viewModelScope.launch {
     *                 featureRepository.insertFeature(feature)
     *                     .onSuccess {
     *
     *                     }
     *             }
     *     }
     */
    private fun insert(
        repositoryInterfaceName: String,
        domainModelClassName: String,
        domainModelsPackageName: String,
        domainName: String,
    ): FunSpec{
      return  FunSpec.builder("insert$domainName")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(
                ParameterSpec.builder(domainName.lowerFirstChar(),
                    ClassName(domainModelsPackageName,domainModelClassName))
                    .build())
            .addCode(
                """ viewModelScope.launch {
                    |${repositoryInterfaceName.lowerFirstChar()}.insert$domainName(${domainName.lowerFirstChar()})
                    |    .onSuccess {
                    |       //show success in the ui
                    |    }
                    |    .onFailure {
                    |       //show error in the ui
                    |    }
                    |}
            """
                    .trimMargin())
            .build()
        
    }
    /**
     * This method generates following code :
     *   fun deleteFeature(feature: FeatureModel) {
     *             viewModelScope.launch {
     *                 featureRepository.deleteFeature(feature)
     *                     .onSuccess {
     *
     *                     }
     *             }
     *     }
     */
    private fun delete(
        repositoryInterfaceName: String,
        domainModelClassName: String,
        domainModelsPackageName: String,
        domainName: String,
    ): FunSpec{
        return  FunSpec.builder("delete$domainName")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(
                ParameterSpec.builder(domainName.lowerFirstChar(),
                    ClassName(domainModelsPackageName,domainModelClassName))
                    .build())
            .addCode(
                """ viewModelScope.launch {
                    |${repositoryInterfaceName.lowerFirstChar()}.delete$domainName(${domainName.lowerFirstChar()})
                    |    .onSuccess {
                    |       //show success in the ui
                    |    }
                    |    .onFailure {
                    |       //show error in the ui
                    |    }
                    |}
            """
                    .trimMargin())
            .build()

    }
    /**
     * This method generates following code :
     *   fun updateFeature(feature: FeatureModel) {
     *             viewModelScope.launch {
     *                 featureRepository.updateFeature(feature)
     *                     .onSuccess {
     *
     *                     }
     *             }
     *     }
     */
    private fun update(
        repositoryInterfaceName: String,
        domainModelClassName: String,
        domainModelsPackageName: String,
        domainName: String,
    ): FunSpec{
        return  FunSpec.builder("update$domainName")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(
                ParameterSpec.builder(domainName.lowerFirstChar(),
                    ClassName(domainModelsPackageName,domainModelClassName))
                    .build())
            .addCode(
                """ viewModelScope.launch {
                    |${repositoryInterfaceName.lowerFirstChar()}.update$domainName(${domainName.lowerFirstChar()})
                    |    .onSuccess {
                    |       //show success in the ui
                    |    }
                    |    .onFailure {
                    |       //show error in the ui
                    |    }
                    |}
            """
                    .trimMargin())
            .build()

    }

    
    
    companion object {
        fun Project.registerTaskCreateViewModel(serviceProvider: Provider<ProjectPathService>): TaskProvider<CreateViewModel> =
            this.tasks.register(MvvmPluginConstant.TASK_CREATE_VIEW_MODEL, CreateViewModel::class.java) {
                // this task needs project's repository to generate the code
                dependsOn(MvvmPluginConstant.TASK_GENERATE_REPOSITORY)
                group = MvvmPluginConstant.PLUGIN_GROUP
                description = MvvmPluginConstant.TASK_CREATE_VIEW_MODEL_DESCRIPTION

                projectPathService.set(serviceProvider)
                usesService(serviceProvider)
            }
    }
}