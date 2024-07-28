package tasks

import com.squareup.kotlinpoet.FileSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class GenerateMvvm extends DefaultTask {

    private boolean preferKotlin = true

    private String subPath

    @Option(option = "subPath", description = """Generates mvvm inside the subpath.
    Mvvm generates stuffs under main source directory + package name; 
    so if subPath is given then mainSourceDirectory/packageName/subPath""")
    void setSubPath(String subPath) {
        this.subPath = subPath
    }

    @Option(option = "preferKotlin",description = """ This plugin generates code assuming you have kotlin sourceSets
    but if you have java sourceSets an bhnygv`d you want to generate structure in the java sourceSets you can set this flag to false by
    using option :mvvm  --no-preferKotlin""")
    void setPreferKotlin(boolean prefer){
        preferKotlin = prefer
    }

    @TaskAction
    void action() {
        def projectPath
        def mainSourceSet = project.layout.projectDirectory.dir('src/main')
        def kotlinSourceDir = mainSourceSet.dir("kotlin").asFile
        def javaSourceDir = mainSourceSet.dir("java").asFile

        if (kotlinSourceDir.exists() && preferKotlin)
            projectPath = kotlinSourceDir.path
        else
            projectPath = javaSourceDir.path

        def androidExtension = project.extensions.findByName("android")
        //either use findByName or use map getter method
        def namespaceCollection = androidExtension.properties.get("namespace").split('\\.')
        def namespace = ""
        namespaceCollection.each { part ->
            namespace = namespace + "/$part"
        }
        def dirPath
        if (!subPath.isEmpty()) {
            dirPath = projectPath + namespace + "/${subPath}"
        } else
            dirPath = projectPath + namespace

        def dir = new File(dirPath)
        if (!dir.exists())
            dir.mkdirs()
        createMvvmStructure(dir,subPath)

    }
    private void createMvvmStructure(File dir,String mvvmSubPath){
        def mName = getValueForExtensionPropertyNameUsingField(PluginConstant.M_FOR_MODEL_FIELD)
        def vmName = getValueForExtensionPropertyNameUsingField(PluginConstant.VM_FOR_VIEWMODEL_FIELD)
        def vName = getValueForExtensionPropertyNameUsingField(PluginConstant.V_FOR_VIEW_FIELD)
        checkForEmpty(mName)
        checkForEmpty(vmName)
        checkForEmpty(vName)
        def mSubPath = getValueForExtensionPropertySubPathUsingField(PluginConstant.M_FOR_MODEL_FIELD)
        def vSubPath = getValueForExtensionPropertySubPathUsingField(PluginConstant.V_FOR_VIEW_FIELD)
        def vmSubPath = getValueForExtensionPropertySubPathUsingField(PluginConstant.VM_FOR_VIEWMODEL_FIELD)

        def mFile = createSubPathIfAvailable(mSubPath,dir,mName)
        createSourceFile(mFile,mName,mSubPath,mvvmSubPath)
        def vFile = createSubPathIfAvailable(vSubPath,dir,vName)
        createSourceFile(vFile,vName,vSubPath,mvvmSubPath)
        def vmFile = createSubPathIfAvailable(vmSubPath,dir,vmName)
        createSourceFile(vmFile,vmName,vmSubPath,mvvmSubPath)


    }

    private String getValueForExtensionPropertyNameUsingField(String fieldName){

        return project.extensions[PluginConstant.EXTENSION_NAME]
                                                     //extension property name
                .properties[fieldName][PluginConstant.EXTENSION_PROPERTY_NAME].get()
                            //field can be view,model,viewModel
    }
    private String getValueForExtensionPropertySubPathUsingField(String fieldName){
        return project.extensions[PluginConstant.EXTENSION_NAME]
                                                    //extension property subPath
                .properties[fieldName][PluginConstant.EXTENSION_PROPERTY_SUB_PATH].get()
                        //field can be view,model,viewModel
    }
    private static checkForEmpty(String value){
        if(value.isEmpty())
            throw new Throwable("Extension property is not configured properly, Don't pass empty String")
    }
    private static File createSubPathIfAvailable(String path, File dir, String field){
        def newMSubDirPath
        if(path.isEmpty())
            newMSubDirPath = dir.path + "/${field}"
        else
            newMSubDirPath = dir.path + "/${path}" + "/${field}"

        File newMSubDir = new File(newMSubDirPath.toLowerCase())
        if(!newMSubDir.exists())
            newMSubDir.mkdirs()

       return newMSubDir
    }
    private createSourceFile(File dir,String extensionName, String subPath,String mvvmSubPath){
        def androidExtension = project.extensions.findByName("android")
        def androidPackage = androidExtension.properties.get("namespace").toString()
        def packageName
        if(mvvmSubPath.isBlank())
        {
            packageName = androidPackage
        }else
        {
            String separatedMvvmSubPath = ""
            def collection = mvvmSubPath.split('/')
            println(collection)
            if(collection.size() == 0){
                separatedMvvmSubPath = mvvmSubPath
            }else
            {
                collection.each {
                    separatedMvvmSubPath = separatedMvvmSubPath + "." + it
                }
            }
            packageName = androidPackage + "${separatedMvvmSubPath.trim()}"
        }
        println(packageName)
        String modifiedPackageName
        if(subPath.isBlank())
            modifiedPackageName = packageName + ".${extensionName}"
        else
            {
                String separatedSubPath = ""
                def collection = subPath.split('/')
                if(collection.size() == 0)
                    modifiedPackageName = packageName + ".${subPath}" + ".${extensionName}"
                else
                    {
                        collection.each {
                            separatedSubPath = separatedSubPath + "." + it

                        }
                        modifiedPackageName = packageName + "${separatedSubPath.trim()}"+  ".${extensionName}"
                    }
            }
        def fileSpec = FileSpec.builder(modifiedPackageName.toLowerCase(), "GradleLovesKotlinPoet").build()
        def kotlinFile = new File(dir, "GradleLovesKotlinPoet.kt")
        kotlinFile.withWriter('UTF-8') {
            writer ->
                fileSpec.writeTo(writer)
        }
    }



}