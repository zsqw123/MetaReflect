package zsu.meta.reflect.impl

import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.isNullable
import zsu.meta.reflect.JClassName
import zsu.meta.reflect.TypeParameterContainer
import zsu.meta.reflect.asJClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

internal class MKTypeImpl(
    private val kmType: KmType,
    private val typeParameterContainer: TypeParameterContainer,
) : KType {
    // todo: not support yet
    override val annotations: List<Annotation> = emptyList()

    override val arguments: List<KTypeProjection> by lazy {
        kmType.arguments.map {
            val (variance, type) = it
            if (variance == null || type == null) KTypeProjection.STAR
            else KTypeProjection(variance.asKVariance(), MKTypeImpl(type, typeParameterContainer))
        }
    }

    override val classifier: KClassifier? by lazy {
        val className: JClassName = when (val classifier = kmType.classifier) {
            is KmClassifier.Class -> classifier.name.asJClass
            is KmClassifier.TypeAlias -> classifier.name.asJClass
            is KmClassifier.TypeParameter -> {
                val typeParameter = typeParameterContainer.getTypeParameter(classifier.id)
                return@lazy MKTypeParameterImpl(typeParameter.asKm, typeParameterContainer)
            }
        }
        Class.forName(className).kotlin
    }

    override val isMarkedNullable: Boolean = kmType.isNullable
}