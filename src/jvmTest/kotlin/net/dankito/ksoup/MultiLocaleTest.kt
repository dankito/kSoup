package net.dankito.ksoup

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@ArgumentsSource(MultiLocaleExtension::class)
@ExtendWith(MultiLocaleExtension::class)
@ParameterizedTest
annotation class MultiLocaleTest