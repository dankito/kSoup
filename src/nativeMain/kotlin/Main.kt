import net.codinux.ksoup.kSoup
import platform.posix.exit

fun main(args: Array<String>) {
  if (args.size < 2) {
    println("Please enter a CSS query and a HTML string, e.g.:\n./kSoup.kexe \"head > title\" \"<html><head><title>Simple example</title></head></html>\"")
    exit(0)
  }

  val kSoup = kSoup()

  val elements = kSoup.select(args[1], args[0])

  elements.elements.forEachIndexed { index, element ->
    println("[$index] ${element.outerHtml}")
  }
}