package zsu.meta.reflect.impl

import kotlinx.metadata.jvm.KotlinClassMetadata
import zsu.meta.reflect.*

internal class MReflectImpl(
    allMapping: Collection<MReflectGeneratedMapping>,
) : MReflect {
    private val mapping: Map<String, MReflectGeneratedMapping> = allMapping.flatMap {
        it.names.map { jClassName: JClassName -> jClassName to it }
    }.toMap()

    private val cache: LinkedHashMap<JClassName, MetadataContainer> = linkedMapOf()

    override fun mClassFrom(jClass: Class<*>, fallbackRuntime: Boolean): MetadataContainer {
        val className = jClass.name
        val cached = cache[className]
        if (cached != null) return cached
        val metadataFromGeneratedMapping = mapping.takeIf { it.isNotEmpty() }
            ?.get(className)?.getMetadataByName(className)
        val metadata = metadataFromGeneratedMapping
            ?: jClass.getDeclaredAnnotation(Metadata::class.java)
            ?: throw IllegalArgumentException("class: ${jClass.name} didn't contain kotlin metadata")
        val classMetadata = KotlinClassMetadata.readLenient(metadata)
        val metadataContainer = when (classMetadata) {
            is KotlinClassMetadata.Class -> MClassImpl(jClass, classMetadata.kmClass)
            is KotlinClassMetadata.FileFacade -> MFileImpl(jClass, classMetadata.kmPackage)
            is KotlinClassMetadata.SyntheticClass -> classMetadata.kmLambda?.let { MLambda(it) }
            else -> null
        } ?: throw IllegalArgumentException(
            "unsupported kotlin metadata type: $classMetadata for class: ${jClass.name}"
        )
        synchronized(this) {
            cache[className] = metadataContainer
        }
        return metadataContainer
    }
}
