package utils

import architecture.AndroidExtension
import com.android.build.gradle.AppExtension
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import extension.MvvmConfigurationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import retrofit2.Response
import tasks.DependencyClass
import java.util.Locale

object TaskUtil {
    fun String.capitalizeFirstChar() =
        this.replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(
                    Locale.ROOT,
                )
            } else {
                it.toString()
            }
        }

    fun String.lowerFirstChar() = this.replaceFirstChar { char -> char.lowercase() }

    fun String.modifyPackageName(
        pkg: String?,
        ext: String,
    ): String {
        val extName = ".${ext.replaceFirstChar { it.lowercase() }}"

        if (this.isEmpty()) {
            return if (pkg != null) {
                pkg + extName
            } else {
                extName
            }
        } else {
            var separatedSubPath = ""
            val collection = this.split('/')
            collection.forEach {
                separatedSubPath = "$separatedSubPath.${it.lowerFirstChar()}"
            }
            return if (pkg != null) {
                pkg + ".${separatedSubPath.trim()}" + extName
            } else {
                ".${separatedSubPath.trim()}" + extName
            }
        }
    }

    fun String.makeGoodName() =
        this
            .replace('.', '/')
            .split('/')
            .last()
            .capitalizeFirstChar()

    inline fun <reified T> TypeSpec.Builder.addSuperIfNullable(isNullable: Boolean) =
        if (isNullable) this.addSuperinterface(T::class) else this

    fun String.getPackageName(androidExtension: AppExtension): String? =
        if (this.isEmpty()) {
            androidExtension.namespace
        } else {
            var separatedMvvmSubPath = ""
            val collection = this.replace('.', '/').split('/')
            collection.forEach {
                separatedMvvmSubPath = "$separatedMvvmSubPath.${it.lowerFirstChar()}"
            }

            androidExtension.namespace + separatedMvvmSubPath.trim()
        }

    fun getExtension(project: Project): MvvmConfigurationExtension {
        val extension = project.extensions.getByType<MvvmConfigurationExtension>()
        if (extension.model.name
                .get()
                .isBlank() ||
            extension.view.name
                .get()
                .isBlank() ||
            extension.viewModel.name
                .get()
                .isBlank()
        ) {
            throw Throwable("${MvvmPluginConstant.EXTENSION_NAME} is not properly configured; Please check for blank String")
        }

        return extension
    }

    fun DependencyClass.wrapInRetrofitResponse() =
        Response::class.asClassName().parameterizedBy(
            ClassName(this.packageName, this.className),
        )
}
