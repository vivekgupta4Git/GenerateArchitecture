package architecture

import org.gradle.api.provider.Property

abstract class ArchitectureProperties {
    open var name : String = ""
    open var insideDirectory  = ""
}