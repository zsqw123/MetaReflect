package zsu.meta.reflect.impl

import kotlinx.metadata.jvm.KotlinClassMetadata
import zsu.meta.reflect.*

internal class MReflectImpl(
    private val allMapping: Collection<MReflectGeneratedMapping>,
) : MReflect {
    override fun mClassFrom(jClass: Class<*>, fallbackRuntime: Boolean): MetadataContainer {
        val metadata = jClass.getDeclaredAnnotation(Metadata::class.java)
            ?: throw IllegalArgumentException("class: ${jClass.name} didn't contain kotlin metadata")
        val classMetadata = KotlinClassMetadata.readLenient(metadata)
        return when (classMetadata) {
            is KotlinClassMetadata.Class -> MClass(classMetadata.kmClass)
            is KotlinClassMetadata.FileFacade -> MFile(classMetadata.kmPackage)
            is KotlinClassMetadata.SyntheticClass -> classMetadata.kmLambda?.let { MLambda(it) }
            else -> null
        } ?: throw IllegalArgumentException(
            "unsupported kotlin metadata type: $classMetadata for class: ${jClass.name}"
        )
    }
}