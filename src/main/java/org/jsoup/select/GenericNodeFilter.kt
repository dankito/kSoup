package org.jsoup.select

import org.jsoup.nodes.Node

class GenericNodeFilter(private val filter: (Node) -> NodeFilter.FilterResult) : NodeFilter {

    override fun head(node: Node, depth: Int): NodeFilter.FilterResult =
        filter(node)

}