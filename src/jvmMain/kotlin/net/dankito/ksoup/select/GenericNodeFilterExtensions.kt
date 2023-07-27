package net.dankito.ksoup.select

import net.dankito.ksoup.nodes.Node
import java.util.function.Function

fun GenericNodeFilter.Companion.jvmNodeFilter(function: Function<Node, NodeFilter.FilterResult>) = GenericNodeFilter { node ->
    function.apply(node)
}