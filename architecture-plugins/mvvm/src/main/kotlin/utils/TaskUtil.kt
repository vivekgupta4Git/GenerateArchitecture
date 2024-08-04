package utils

import architecture.AndroidExtension
import com.squareup.kotlinpoet.TypeSpec
import java.util.Locale

object TaskUtil {

    fun String.capitalizeFirstChar() = this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }

    fun String.lowerFirstChar() = this.replaceFirstChar { char -> char.lowercase() }

    fun String.modifyPackageName(pkg: String?, ext: String): String {
        val extName = ".${ext.replaceFirstChar { it.lowercase() }}"

        if (this.isEmpty())
            return if (pkg != null)
                pkg + extName
            else
                extName
        else {
            var separatedSubPath = ""
            val collection = this.split('/')
            collection.forEach {
                separatedSubPath = "$separatedSubPath.${it.lowerFirstChar()}"
            }
            return if (pkg != null)
                pkg + ".${separatedSubPath.trim()}" + extName
            else
                ".${separatedSubPath.trim()}" + extName
        }

    }

    fun String.makeGoodName() =
        this.replace('.', '/').split('/').last().capitalizeFirstChar()

    inline fun <reified T> TypeSpec.Builder.addSuperIfNullable(isNullable: Boolean) = if(isNullable) this.addSuperinterface(T::class) else this

    fun String.getPackageName(androidExtension: AndroidExtension): String? {
        return if (this.isEmpty())
            androidExtension.namespace
        else {
            var separatedMvvmSubPath = ""
            val collection = this.replace('.', '/').split('/')
            collection.forEach {
                separatedMvvmSubPath = "$separatedMvvmSubPath.${it.lowerFirstChar()}"
            }

            androidExtension.namespace + separatedMvvmSubPath.trim()
        }
    }
}