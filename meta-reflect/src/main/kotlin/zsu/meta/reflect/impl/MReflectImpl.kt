package zsu.meta.reflect.impl

import kotlinx.metadata.jvm.KotlinClassMetadata
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.j.MJClassImpl
import zsu.meta.reflect.impl.k.MFileImpl
import zsu.meta.reflect.impl.k.MKClassImpl

internal class MReflectImpl(
    allMapping: Collection<MReflectGeneratedMapping>,
) : MReflect {
    private val mapping: Map<String, MReflectGeneratedMapping> = allMapping.flatMap {
        it.names.map { jClassName: JClassName -> jClassName to it }
    }.toMap()

    private val cache: LinkedHashMap<Class<*>, MetadataContainer> = linkedMapOf()

    override fun mClassFrom(jClass: Class<*>, fallbackJavaImpl: Boolean): MetadataContainer {
        val cached = cache[jClass]
        if (cached != null) return cached
        val className = jClass.name
        val metadataFromGeneratedMapping = mapping.takeIf { it.isNotEmpty() }
            ?.get(className)?.getMetadataByName(className)
        val metadata = metadataFromGeneratedMapping
            ?: jClass.getDeclaredAnnotation(Metadata::class.java)

        val metadataContainer: MetadataContainer = if (metadata == null) {
            if (fallbackJavaImpl) MJClassImpl(this, jClass)
            else throw IllegalArgumentException("class: $className didn't contains kotlin metadata")
        } else {
            val classMetadata = KotlinClassMetadata.readLenient(metadata)
            when (classMetadata) {
                is KotlinClassMetadata.Class -> MKClassImpl(this, jClass, classMetadata.kmClass)
                is KotlinClassMetadata.FileFacade -> MFileImpl(jClass, classMetadata.kmPackage)
                is KotlinClassMetadata.SyntheticClass -> classMetadata.kmLambda?.let { MLambda(it) }
                else -> null
            } ?: throw IllegalArgumentException(
                "unsupported kotlin metadata type: $classMetadata for class: $className"
            )
        }

        synchronized(this) {
            cache[jClass] = metadataContainer
        }
        return metadataContainer
    }
}
