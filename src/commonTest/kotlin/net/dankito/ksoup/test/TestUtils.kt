package net.dankito.ksoup.test

import net.codinux.kotlin.Platform
import kotlin.reflect.KClass

/**
 * JS throws a ClassCastExceptions, all other platforms a NullPointerException when a type expected to be non-null is null.
 */
val NullPointerExceptionOfPlatform: KClass<out Throwable> =
    if (Platform.type.isJavaScript) ClassCastException::class else NullPointerException::class