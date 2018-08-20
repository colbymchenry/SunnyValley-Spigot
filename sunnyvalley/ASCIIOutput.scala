package sunnyvalley

object ASCIIOutput {

  val ANSI_RESET = "\u001B[0m"
  val ANSI_BLACK = "\u001B[30m"
  val ANSI_RED = "\u001B[31m"
  val ANSI_GREEN = "\u001B[32m"
  val ANSI_YELLOW = "\u001B[33m"
  val ANSI_BLUE = "\u001B[34m"
  val ANSI_PURPLE = "\u001B[35m"
  val ANSI_CYAN = "\u001B[36m"
  val ANSI_WHITE = "\u001B[37m"

  def printTitle: Unit = {
    println(ANSI_YELLOW + "   _____                      __      __   _ _            " + ANSI_RESET)
    println(ANSI_YELLOW + "  / ____|                     \\ \\    / /  | | |           " + ANSI_RESET)
    println(ANSI_YELLOW + " | (___  _   _ _ __  _ __  _   \\ \\  / /_ _| | | ___ _   _ " + ANSI_RESET)
    println(ANSI_YELLOW + "  \\___ \\| | | | '_ \\| '_ \\| | | \\ \\/ / _` | | |/ _ \\ | | |" + ANSI_RESET)
    println(ANSI_YELLOW + "  ____) | |_| | | | | | | | |_| |\\  / (_| | | |  __/ |_| |" + ANSI_RESET)
    println(ANSI_YELLOW + " |_____/ \\__,_|_| |_|_| |_|\\__, | \\/ \\__,_|_|_|\\___|\\__, |" + ANSI_RESET)
    println(ANSI_YELLOW + "                            __/ |                    __/ |" + ANSI_RESET)
    println(ANSI_YELLOW + "                           |___/                    |___/ " + ANSI_RESET)
    println(" ")
    println(" ")
    println(ANSI_GREEN + "Release: v" + Main.version + ANSI_RESET)
    println(" ")
  }

}
