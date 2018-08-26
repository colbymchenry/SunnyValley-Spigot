package sunnyvalley.landcontrol

import org.bukkit.{ChatColor, Chunk}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

object CommandClaim extends CommandExecutor {

  override def onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array[String]): Boolean = {
    if (!commandSender.isInstanceOf[Player]) return false

    val player: Player = commandSender.asInstanceOf[Player]
    val chunk: Chunk = player.getWorld.getChunkAt(player.getLocation)
    val claim: LandClaim = new LandClaim(player.getWorld, chunk.getX, chunk.getZ)

    if (strings(0).equalsIgnoreCase("delete")) {
      if(!claim.inDatabase) {
        player.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "This chunk is not claimed.")
      } else {
        if(!claim.getOwner().getUniqueId.equals(player.getUniqueId) && !player.hasPermission("claim.override")) {
          player.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "You are not allowed to delete this claim.")
        } else {
          claim.delete()
          player.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "Land claim deleted!")
        }
      }
    }

    true
  }
}

