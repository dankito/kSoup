package net.codinux.ksoup

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen


class CommandExecutor {

    /**
     * Kudos for this code go to gildor, copied from https://stackoverflow.com/a/64311170
     */
    fun executeCommand(command: String, trim: Boolean = true, redirectStderr: Boolean = true): String {
        val commandToExecute = if (redirectStderr) "$command 2>&1" else command
        val fp = popen(commandToExecute, "r") ?: error("Failed to run command: $command")

        val stdout = buildString {
            val buffer = ByteArray(4096)
            while (true) {
                val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
                append(input.toKString())
            }
        }

        val status = pclose(fp)
        if (status != 0) {
            error("Command `$command` failed with status $status${if (redirectStderr) ": $stdout" else ""}")
        }

        return if (trim) stdout.trim() else stdout
    }

    fun executeCommandGetLines(command: String, trim: Boolean = true, redirectStderr: Boolean = true): List<String> {
        return executeCommand(command, trim, redirectStderr).split("\n")
    }

}