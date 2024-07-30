package architecture

import org.gradle.api.provider.Property

abstract class ArchitectureBase {
    abstract var name : Property<String>
    abstract var subPath : Property<String>
}