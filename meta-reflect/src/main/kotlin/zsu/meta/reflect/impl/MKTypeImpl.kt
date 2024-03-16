package zsu.meta.reflect.impl

import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.isNullable
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

internal class MKTypeImpl(
    private val kmType: KmType
) : KType {
    // todo: not support yet
    override val annotations: List<Annotation> = emptyList()

    override val arguments: List<KTypeProjection> by lazy {
        kmType.arguments.map {
            val (variance, type) = it
            if (variance == null || type == null) KTypeProjection.STAR
            else KTypeProjection(variance.asKVariance(), MKTypeImpl(type))
        }
    }

    override val classifier: KClassifier? by lazy {
        when (val classifier = kmType.classifier) {
            is KmClassifier.Class -> TODO()
            is KmClassifier.TypeAlias -> TODO()
            is KmClassifier.TypeParameter -> TODO()
        }

        Class.forName("").kotlin
    }

    override val isMarkedNullable: Boolean = kmType.isNullable
}