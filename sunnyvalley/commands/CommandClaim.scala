package sunnyvalley.commands

import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.{ChatColor, Chunk}
import sunnyvalley.landcontrol.{ClaimPotion, LandClaim}

object CommandClaim extends CommandExecutor {

  override def onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array[String]): Boolean = {
    if (!commandSender.isInstanceOf[Player]) return false

    if(strings.isEmpty) return false

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
    } else if(strings(0).equalsIgnoreCase("info")) {

    } else if (strings(0).equalsIgnoreCase("potion")) {
      player.getInventory.addItem(ClaimPotion.getClaimPotion)
    } else return false

    true
  }
}
