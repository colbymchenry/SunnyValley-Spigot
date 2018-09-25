package sunnyvalley.listeners

import java.util.logging.Level

import org.bukkit.entity.EntityType
import org.bukkit.event.block.{BlockBreakEvent, SignChangeEvent}
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.{Bukkit, ChatColor, Material}
import sunnyvalley.{Main, ShipmentBox}

object ShipmentBoxListener extends Listener {

  @EventHandler
  def onEntitySpawn(e: CreatureSpawnEvent): Unit = {
    if (e.getEntityType != EntityType.PLAYER && e.getSpawnReason == SpawnReason.NATURAL
      && !Main.instance.getConfig.getBoolean("EnableAnimalNaturalSpawning", false))
      e.setCancelled(true)
  }

  /**
    * Event for creating shipment boxs
    */
  @EventHandler
  def onSignChanged(e: SignChangeEvent): Unit = {
    val signMaterial: org.bukkit.material.Sign = e.getBlock.getState.getData.asInstanceOf[org.bukkit.material.Sign]
    val sign: org.bukkit.block.Sign = e.getBlock.getState.asInstanceOf[org.bukkit.block.Sign]

    if (!e.getLine(0).equalsIgnoreCase("[ShipmentBox]")) return

    if (e.isCancelled) return

    if (e.getBlock.getRelative(signMaterial.getAttachedFace).getBlockData.getMaterial != Material.CHEST) {
      e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "The sign must be placed against a chest.")
      return
    }

    val attachedSign = ShipmentBox.getAttachedSignForBlock(e.getBlock.getRelative(signMaterial.getAttachedFace))

    if(attachedSign != null) {
      e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "There is already a shipment box.")
      e.setCancelled(true)
      e.getBlock.breakNaturally()
      return
    }

    try {
      // if there is somehow a shipment box still there within the database make sure to notify an admin to delete it
      if (!new ShipmentBox(sign.getWorld, sign.getX, sign.getY, sign.getZ).writeToDatabase(e.getPlayer)) {
        e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "Somehow there is still a shipment box here... Please notify the admins.")
        e.setCancelled(true)
        e.getBlock.breakNaturally()
      }
    } catch {
      case ex: Exception =>
        e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "A serious error occurred. Please notify the admins.")
        e.setCancelled(true)
        Main.instance.getLogger.log(Level.SEVERE, ex.getMessage, ex)
        ex.printStackTrace()
        e.getBlock.breakNaturally()
        return
    }

    e.setLine(0, ChatColor.DARK_BLUE.toString + "[ShipmentBox]")
    e.setLine(1, "")
    e.setLine(2, ChatColor.UNDERLINE.toString + "Owner")
    e.setLine(3, e.getPlayer.getName)
    e.getPlayer.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "Shipment box created! Shipments will be picked up at 12:00am every morning.")
  }

  @EventHandler
  def onBlockBreak(e: BlockBreakEvent): Unit = {
    if (e.getBlock.getBlockData.getMaterial != Material.WALL_SIGN) {
      if(e.getBlock.getBlockData.getMaterial == Material.CHEST) {
        val attachedSign = ShipmentBox.getAttachedSignForBlock(e.getBlock)
        if(attachedSign != null) {
          Bukkit.getServer.getPluginManager.callEvent(new BlockBreakEvent(attachedSign, e.getPlayer))
        }
      }
      return
    }

    val signMaterial: org.bukkit.material.Sign = e.getBlock.getState.getData.asInstanceOf[org.bukkit.material.Sign]
    val sign: org.bukkit.block.Sign = e.getBlock.getState.asInstanceOf[org.bukkit.block.Sign]

    if (e.isCancelled) return

    if (!sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE.toString + "[ShipmentBox]")) return

    val shipmentBox: ShipmentBox = new ShipmentBox(sign.getWorld, sign.getX, sign.getY, sign.getZ)
    if (shipmentBox.inDatabase) {
      shipmentBox.delete()
      e.getPlayer.sendMessage("Shipment box deleted.")
    }
  }

  // TODO: Animal prices for whool, milk, and meats
  // TODO: pricing for new aquatic update content

}
