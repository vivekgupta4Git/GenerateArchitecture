/**
 *@author Vivek Gupta
 */
object MvvmPluginConstant {
    const val PLUGIN_GROUP = "Architecture Plugin"
    const val EXTENSION_NAME = "configureMvvm"

    const val TASK_CREATE_MODELS = "createModels"
    const val TASK_CREATE_MODELS_DESCRIPTION = "This task will generate all source files belongs to m of mvvm"

    const val TASK_GENERATE_DOMAIN_MODELS = "generateDomainModel"
    const val TASK_GENERATE_DOMAIN_MODELS_DESCRIPTION = "This task will generate domain model source files"

    const val TASK_GENERATE_NETWORK_MODELS = "generateNetworkModel"
    const val TASK_GENERATE_NETWORK_MODELS_DESCRIPTION = "This task will generate network model source files"

    const val TASK_GENERATE_ENTITY_MODELS = "generateEntityModel"
    const val TASK_GENERATE_ENTITY_MODELS_DESCRIPTION = "This task will generate entity model source files"

    const val TASK_GENERATE_REST_API = "generateRestApi"
    const val TASK_GENERATE_REST_API_DESCRIPTION = "This task will generate rest api source files"

    const val TASK_CREATE_MVVM_SOURCE_CODES = "createMvvm"
    const val TASK_CREATE_MVVM_SOURCE_CODES_DESCRIPTION = "Generate directory structure according to Mvvm Architecture"

    const val TASK_GET_PROJECT_PACKAGE = "getProjectPackage"
    const val TASK_GET_PROJECT_PACKAGE_DESCRIPTION: String = "This task when run on an android project will set the project's package name to the build service which other task can use"

    const val TASK_GENERATE_DAO = "generateDao"
    const val TASK_GENERATE_DAO_DESCRIPTION: String = "This task generates dao(Data Access Object) for the database"

    const val TASK_GENERATE_REMOTE_DATA_SOURCE = "generateRemoteDataSource"
    const val TASK_GENERATE_REMOTE_DATA_SOURCE_DESCRIPTION: String = "This task generates remote data source"

    const val TASK_GENERATE_LOCAL_DATA_SOURCE = "generateLocalDataSource"
    const val TASK_GENERATE_LOCAL_DATA_SOURCE_DESCRIPTION: String = "This task generates local data source"

    const val TASK_GENERATE_MAPPER = "generateMapper"
    const val TASK_GENERATE_MAPPER_DESCRIPTION : String = "This task generates mapper between domain,entity and network Model"

    const val TASK_GENERATE_REPOSITORY_INTERFACE = "generateRepositoryInterface"
    const val TASK_GENERATE_REPOSITORY_INTERFACE_DESCRIPTION: String = "This task generates repository interface"

    const val TASK_GENERATE_REPOSITORY = "generateRepository"
    const val TASK_GENERATE_REPOSITORY_DESCRIPTION: String = "This task generates repository implementation of the repository interface"
}
