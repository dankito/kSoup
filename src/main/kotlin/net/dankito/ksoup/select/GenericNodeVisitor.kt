package net.dankito.ksoup.select

import net.dankito.ksoup.nodes.Node
import java.util.function.Consumer

class GenericNodeVisitor(private val action: (Node) -> Unit) : NodeVisitor {

    companion object {

        @JvmStatic
        fun jvmNodeVisitor(consumer: Consumer<Node>) = GenericNodeVisitor { node -> consumer.accept(node) }

    }

    override fun head(node: Node, depth: Int) {
        action(node)
    }
}