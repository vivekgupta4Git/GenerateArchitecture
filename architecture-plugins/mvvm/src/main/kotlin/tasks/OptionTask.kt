package tasks

import MvvmArchPlugin.Companion.mvvmSubPath
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.options.Option

abstract class OptionTask : DefaultTask() {
    @Option(
        option = "sub-path",
        description = "generate source files under sub-path",
    )
    fun setSubPath(subPath: String) {
        mvvmSubPath = subPath
    }
}
