package net.dankito.ksoup.jvm

import net.dankito.ksoup.nodes.Element
import net.dankito.ksoup.nodes.Node
import java.util.function.Consumer


fun Node.forEachNode(action: Consumer<Node>) =
    this.forEachNode(action::accept)

fun Element.forEach(action: Consumer<in Element>) =
    this.forEach(action::accept)