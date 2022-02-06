package net.codinux.ksoup

internal actual class kSoupExecutor {

  private val commandExecutor = CommandExecutor()


  actual fun select(html: String, cssQuery: String): Elements {
    // TODO: this is dangerous as an attacker could execute any command for 'html' and 'cssQuery' on the system
    val pupResult = commandExecutor.executeCommandGetLines("echo \"$html\" | pup \"$cssQuery\"")

    return Elements(pupResult.map { Element(it) })
  }

}