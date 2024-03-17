# MetaReflect

Fast and lite Kotlin reflect tool built by
[kotlinx-metadata](https://github.com/JetBrains/kotlin/blob/master/libraries/kotlinx-metadata/jvm/ReadMe.md).

## Usages

Here is an example shows getting sealed subclasses through `MetaReflect`.

```kotlin
sealed interface SealedParent
class ChildA : SealedParent
class ChildB : SealedParent

fun main() {
    // preload is recommended, **NOT REQUIRED**
    // only needed call once in whole application lifecycle.
    MReflect.preload()
    
    val reflect = MReflect.get()
    reflect.mClass<SealedParent>().sealedSubclasses // [class ChildA, class ChildB]
}
```

## Dependencies

Latest version:
[![Maven Central](https://img.shields.io/maven-central/v/host.bytedance/meta-reflect)](https://central.sonatype.com/artifact/host.bytedance/meta-reflect)

```kotlin
dependencies {
    implementation("host.bytedance:meta-reflect:<latest>")
}
```

Recommended kotlin version as follows:

| Recommend | Possible Compatible Version Range | meta-reflect |
|-----------|-----------------------------------|--------------|
| 1.9.22    | <= 2.0.X                          | 0.0.1-beta   |

## Benchmark

Time unit: us (microsecond), lower is better.

| Benchmark Type              | Full Resolve |  Only Names |
|-----------------------------|-------------:|------------:|
| Kotlin Reflect (warmup)     |  182,641.966 | 175,242.409 |
| Kotlin Reflect (first time) |  115,888.439 | 110,907.128 |
| Meta Reflect (warmup)       |   79,314.122 |  79,842.884 |
| Meta Reflect (first time)   |   82,755.670 |  66,865.890 |

1. Full Benchmark
2. Only Resolve Name

## License

```
Copyright 2024 zsqw123

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
