/**
 *@author Vivek Gupta
 */
object MvvmPluginConstant {
    const val TASK_CREATE_MODELS = "createModels"
    const val TASK_CREATE_MODELS_DESCRIPTION = "This task will generate all source files belongs to m of mvvm"
    const val TASK_GENERATE_DOMAIN_MODELS = "generateDomainModelSourceFile"
    const val TASK_GENERATE_DOMAIN_MODELS_DESCRIPTION = "This task will generate domain model source files"
    const val TASK_GENERATE_NETWORK_MODELS = "generateNetworkModelSourceFile"
    const val TASK_GENERATE_NETWORK_MODELS_DESCRIPTION = "This task will generate network model source files"
    const val TASK_GENERATE_ENTITY_MODELS = "generateEntityModelSourceFile"
    const val TASK_GENERATE_ENTITY_MODELS_DESCRIPTION = "This task will generate entity model source files"

    /*
             public static final String PLUGIN_GROUP = "Architecture Plugin"
            public static final String PLUGIN_NAME = "mvvm"
                public static final String EXTENSION_NAME = "mvvmConfig"

     */
    const val PLUGIN_GROUP = "Architecture Plugin"
    const val EXTENSION_NAME = "configureMvvm"
    const val TASK_CREATE_MVVM_SOURCE_CODES = "createMvvm"
    const val TASK_CREATE_MVVM_SOURCE_CODES_DESCRIPTION = "Generate directory structure according to Mvvm Architecture"
    const val TASK_GET_PROJECT_PACKAGE = "getProjectPackage"
    const val TASK_GET_PROJECT_PACKAGE_DESCRIPTION: String = "Get project package name"
}
