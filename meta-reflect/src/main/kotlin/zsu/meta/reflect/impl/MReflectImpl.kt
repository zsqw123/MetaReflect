package zsu.meta.reflect.impl

import kotlinx.metadata.jvm.KotlinClassMetadata
import zsu.meta.reflect.*

internal class MReflectImpl(
    allMapping: Collection<MReflectGeneratedMapping>,
) : MReflect {
    private val mapping: Map<String, MReflectGeneratedMapping> = allMapping.flatMap {
        it.names.map { jClassName: JClassName -> jClassName to it }
    }.toMap()

    private val cache: LinkedHashMap<Class<*>, MetadataContainer> = linkedMapOf()

    override fun mClassFrom(jClass: Class<*>): MetadataContainer {
        val cached = cache[jClass]
        if (cached != null) return cached
        val className = jClass.name
        val metadataFromGeneratedMapping = mapping.takeIf { it.isNotEmpty() }
            ?.get(className)?.getMetadataByName(className)
        val metadata = metadataFromGeneratedMapping
            ?: jClass.getDeclaredAnnotation(Metadata::class.java)
            ?: throw IllegalArgumentException("class: ${jClass.name} didn't contain kotlin metadata")
        val classMetadata = KotlinClassMetadata.readLenient(metadata)
        val metadataContainer = when (classMetadata) {
            is KotlinClassMetadata.Class -> MClassImpl(this, jClass, classMetadata.kmClass)
            is KotlinClassMetadata.FileFacade -> MFileImpl(jClass, classMetadata.kmPackage)
            is KotlinClassMetadata.SyntheticClass -> classMetadata.kmLambda?.let { MLambda(it) }
            else -> null
        } ?: throw IllegalArgumentException(
            "unsupported kotlin metadata type: $classMetadata for class: ${jClass.name}"
        )
        synchronized(this) {
            cache[jClass] = metadataContainer
        }
        return metadataContainer
    }
}
