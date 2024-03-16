package zsu.meta.reflect.impl

import kotlinx.metadata.KmVariance
import kotlin.reflect.KVariance

internal fun KmVariance.asKVariance(): KVariance = when (this) {
    KmVariance.INVARIANT -> KVariance.INVARIANT
    KmVariance.IN -> KVariance.IN
    KmVariance.OUT -> KVariance.OUT
}
