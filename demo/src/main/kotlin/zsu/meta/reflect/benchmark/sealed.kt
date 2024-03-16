package zsu.meta.reflect.benchmark

sealed interface SealedI

class ISubA : SealedI
class ISubB : SealedI
class ISubC : SealedI
class ISubD : SealedI
class ISubE : SealedI
class ISubF : SealedI
class ISubG : SealedI
class ISubH : SealedI
class ISubI : SealedI

sealed class SealedC

class CSubA : SealedC()
class CSubB : SealedC()
class CSubC : SealedC()
class CSubD : SealedC()
class CSubE : SealedC()
class CSubF : SealedC()
class CSubG : SealedC()
class CSubH : SealedC()
class CSubI : SealedC()
