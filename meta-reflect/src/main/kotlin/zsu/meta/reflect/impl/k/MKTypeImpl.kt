package zsu.meta.reflect.impl.k

import zsu.cacheable.Cacheable
import kotlin.metadata.KmClassifier
import kotlin.metadata.KmType
import kotlin.metadata.KmTypeProjection
import kotlin.metadata.KmVariance
import zsu.meta.reflect.*
import zsu.meta.reflect.impl.type.MRKTypeImpl
import kotlin.reflect.KType

internal class MKTypeImpl(
    override val asKm: KmType,
    private val parameterContainer: MKTypeParameterContainer,
) : MKType {
    override val classifier: MClassifier = when (val classifier = asKm.classifier) {
        is KmClassifier.Class, is KmClassifier.TypeAlias -> MKClassClassifier(classifier)
        is KmClassifier.TypeParameter -> MKTypeParameterClassifier(classifier)
    }

    override val arguments: List<MTypeProjection>
        @Cacheable get() =
            asKm.arguments.map {
                when {
                    it.type == null || it.variance == null -> MStarType
                    it.variance == KmVariance.INVARIANT -> MTypeNoVariance(it.mkType())
                    else -> MTypeWithVariance(it.mVariance(), it.mkType())
                }
            }

    private fun KmTypeProjection.mVariance() = when (val origin = variance!!) {
        KmVariance.IN -> MVariance.IN
        KmVariance.OUT -> MVariance.OUT
        else -> error("Must have variance! origin KmTypeProjection: $origin")
    }

    private fun KmTypeProjection.mkType() = MKTypeImpl(type!!, parameterContainer)

    override val asKr: KType
        @Cacheable get() = MRKTypeImpl(asKm, parameterContainer)
}
