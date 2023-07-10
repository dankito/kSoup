package org.jsoup.select

import org.jsoup.nodes.Node

class GenericNodeVisitor(private val action: (Node) -> Unit) : NodeVisitor {
    override fun head(node: Node, depth: Int) {
        action(node)
    }
}