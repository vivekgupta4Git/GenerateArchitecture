plugins{
    `kotlin-dsl`
}
group = "com.ruviapps"
version = "1.0.0"
dependencies{
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("com.android.tools.build:gradle:8.0.0")
    implementation("com.squareup:kotlinpoet:1.18.1")

}
gradlePlugin{
    plugins.create("mvvmArch") {
        id = "mvvm-arch"
        implementationClass = "MvvmArchPlugin"
    }
}