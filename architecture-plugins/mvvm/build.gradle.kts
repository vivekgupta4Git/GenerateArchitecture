import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins{
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
}
kotlin{
    jvmToolchain{
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
group = "io.github.vivekgupta4git"
version = "1.0.2"
dependencies{

    implementation("androidx.room:room-common:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("com.android.tools.build:gradle:8.5.2")

    //noinspection GradleDependency
    implementation("com.squareup:kotlinpoet:1.15.0"){
        exclude(module = "kotlin-reflect")
    }

}
gradlePlugin{
    website = "https://vivekgupta4git.github.io/"
    vcsUrl = "https://github.com/vivekgupta4Git/GenerateArchitecture.git"
    plugins.create("mvvmArch") {
        id = "io.github.vivekgupta4git.mvvm-arch"
        displayName = "Mvvm Architecture Plugin"
        implementationClass = "MvvmArchPlugin"
        tags = listOf("mvvm","android","architecture")
        description = "Often implementing mvvm architecture in the android, there are always " +
                "a boilerplate code. Such as Mapper for converting models from the network to the domain," +
                "creating data sources and data repositories etc. This plugin aims to reduce the boilerplate such codebase "
    }
}
