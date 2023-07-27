package net.dankito.ksoup.select

import net.dankito.ksoup.nodes.Node
import java.util.function.Consumer

fun GenericNodeVisitor.Companion.jvmNodeVisitor(consumer: Consumer<Node>) = GenericNodeVisitor { node ->
    consumer.accept(node)
}