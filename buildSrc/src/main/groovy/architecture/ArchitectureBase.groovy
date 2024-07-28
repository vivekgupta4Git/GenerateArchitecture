package architecture

import org.gradle.api.provider.Property

abstract class ArchitectureBase {
    abstract Property<String> getName()

    abstract Property<String> getSubPath()
}