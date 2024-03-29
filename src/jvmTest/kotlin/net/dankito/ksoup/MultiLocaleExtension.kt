package net.dankito.ksoup

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.Locale
import java.util.stream.Stream

class MultiLocaleExtension : AfterEachCallback, ArgumentsProvider {

    private val defaultLocale = Locale.getDefault()

    override fun afterEach(context: ExtensionContext) {
        Locale.setDefault(defaultLocale)
    }

    override fun provideArguments(extensionContext: ExtensionContext): Stream<out Arguments> {
        return Stream.of(Arguments.of(Locale.ENGLISH), Arguments.arguments(Locale("tr")))
    }

}
