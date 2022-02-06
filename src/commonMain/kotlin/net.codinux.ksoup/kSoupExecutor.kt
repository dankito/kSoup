package net.codinux.ksoup


internal expect class kSoupExecutor() {

  fun select(html: String, cssQuery: String): Elements

}