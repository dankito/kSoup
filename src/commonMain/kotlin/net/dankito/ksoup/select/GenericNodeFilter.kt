package net.dankito.ksoup.select

import net.dankito.ksoup.nodes.Node
import kotlin.jvm.JvmStatic

class GenericNodeFilter(private val filter: (Node) -> NodeFilter.FilterResult) : NodeFilter {

    companion object {

        @JvmStatic
        fun jvmNodeFilter(function: (Node) -> NodeFilter.FilterResult) = GenericNodeFilter { node ->
            function(node)
        }

    }

    override fun head(node: Node, depth: Int): NodeFilter.FilterResult =
        filter(node)

}