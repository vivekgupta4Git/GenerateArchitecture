package architecture

import org.gradle.api.provider.Property

abstract class ArchitectureProperties {
    abstract val name : Property<String>
    abstract val insideDirectory : Property<String>
}