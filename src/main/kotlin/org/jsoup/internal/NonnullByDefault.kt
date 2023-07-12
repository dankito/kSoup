package org.jsoup.internal

import java.lang.annotation.ElementType
import javax.annotation.Nonnull
import javax.annotation.meta.TypeQualifierDefault

@MustBeDocumented
@Nonnull
@TypeQualifierDefault(ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD)
@Retention(
    AnnotationRetention.BINARY
)
/**
 * Indicates that all components (methods, returns, fields) are not nullable, unless otherwise specified by @Nullable.
 * @see javax.annotation.ParametersAreNonnullByDefault
 */
annotation class NonnullByDefault()
