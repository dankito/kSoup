package org.jsoup.select

import org.jsoup.nodes.Node
import java.util.function.Function

class GenericNodeFilter(private val filter: (Node) -> NodeFilter.FilterResult) : NodeFilter {

    companion object {

        @JvmStatic
        fun jvmNodeFilter(function: Function<Node, NodeFilter.FilterResult>) = GenericNodeFilter { node ->
            function.apply(node)
        }

    }

    override fun head(node: Node, depth: Int): NodeFilter.FilterResult =
        filter(node)

}