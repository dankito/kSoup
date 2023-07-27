//package net.dankito.ksoup.nodes
//
//import net.dankito.ksoup.helper.Validate
//import net.dankito.ksoup.helper.W3CDom
//
//
///**
// * This impl works by compiling the input xpath expression, and then evaluating it against a W3C Document converted
// * from the original jsoup element. The original jsoup elements are then fetched from the w3c doc user data (where we
// * stashed them during conversion). This process could potentially be optimized by transpiling the compiled xpath
// * expression to a jsoup Evaluator when there's 1:1 support, thus saving the W3C document conversion stage.
// */
//internal fun <T : Node> NodeUtils.selectXpath(xpath: String, el: Element, nodeType: Class<T>): List<T> {
//    Validate.notEmpty(xpath)
//    Validate.notNull(el)
//    Validate.notNull(nodeType)
//
//    val w3c = W3CDom().namespaceAware(false)
//    val wDoc = w3c.fromJsoup(el)
//    val contextNode = w3c.contextNode(wDoc)
//    val nodeList = w3c.selectXpath(xpath, contextNode)
//    return w3c.sourceNodes(nodeList, nodeType)
//}