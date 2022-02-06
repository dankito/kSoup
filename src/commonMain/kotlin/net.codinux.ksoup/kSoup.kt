package net.codinux.ksoup

class kSoup {

  private val executor = kSoupExecutor()


  fun select(html: String, cssQuery: String): Elements {
    return executor.select(html, cssQuery)
  }

}