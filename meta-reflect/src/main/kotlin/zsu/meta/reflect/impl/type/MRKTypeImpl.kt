package zsu.meta.reflect.impl.type

import kotlin.metadata.KmClassifier
import kotlin.metadata.KmType
import kotlin.metadata.isNullable
import zsu.meta.reflect.JClassName
import zsu.meta.reflect.asJClass
import zsu.meta.reflect.impl.asKVariance
import zsu.meta.reflect.impl.k.MKTypeParameterContainer
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

internal class MRKTypeImpl(
    private val kmType: KmType,
    private val typeParameterContainer: MKTypeParameterContainer,
) : KType {
    // some implementation reference for that like `createAnnotationInstance` in kotlin reflection
    // todo: not support yet
    override val annotations: List<Annotation> = emptyList()

    override val arguments: List<KTypeProjection> by lazy {
        kmType.arguments.map {
            val (variance, type) = it
            if (variance == null || type == null) KTypeProjection.STAR
            else KTypeProjection(variance.asKVariance(), MRKTypeImpl(type, typeParameterContainer))
        }
    }

    override val classifier: KClassifier? by lazy {
        val className: JClassName = when (val classifier = kmType.classifier) {
            is KmClassifier.Class -> classifier.name.asJClass
            is KmClassifier.TypeAlias -> classifier.name.asJClass
            is KmClassifier.TypeParameter -> {
                val typeParameter = typeParameterContainer.getTypeParameter(classifier.id)
                return@lazy MRKTypeParameterImpl(typeParameter.asKm, typeParameterContainer)
            }
        }
        Class.forName(className).kotlin
    }

    override val isMarkedNullable: Boolean = kmType.isNullable
}