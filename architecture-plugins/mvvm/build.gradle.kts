plugins{
    `kotlin-dsl`
}

group = "com.ruviapps"
version = "1.0.0"
dependencies{

    implementation("androidx.room:room-common:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("com.android.tools.build:gradle:8.0.2")
    //noinspection GradleDependency
    implementation("com.squareup:kotlinpoet:1.15.0")

}
gradlePlugin{
    plugins.create("mvvmArch") {
        id = "mvvm-arch"
        implementationClass = "MvvmArchPlugin"
    }
}