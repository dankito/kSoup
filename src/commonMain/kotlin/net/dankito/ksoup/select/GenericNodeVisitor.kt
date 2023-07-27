package net.dankito.ksoup.select

import net.dankito.ksoup.nodes.Node
import kotlin.jvm.JvmStatic

class GenericNodeVisitor(private val action: (Node) -> Unit) : NodeVisitor {

    companion object {

        @JvmStatic
        fun jvmNodeVisitor(consumer: (Node) -> Unit) = GenericNodeVisitor { node ->
            consumer(node)
        }

    }

    override fun head(node: Node, depth: Int) {
        action(node)
    }

}