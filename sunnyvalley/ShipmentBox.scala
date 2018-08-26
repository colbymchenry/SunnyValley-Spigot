package sunnyvalley

import java.sql.ResultSet
import java.text.DecimalFormat
import java.util.UUID

import org.apache.commons.lang3.text.WordUtils
import org.bukkit._
import org.bukkit.block.{Block, BlockFace, Chest, DoubleChest}
import org.bukkit.entity.Player
import org.bukkit.inventory.{Inventory, InventoryHolder}
import sunnyvalley.season.{CalendarHelper, SeasonTracker}

import scala.collection.JavaConverters._

class ShipmentBox(val world: World, val x: Int, val y: Int, val z: Int) {

  def getInventory: Inventory = {
    val block = world.getBlockAt(x, y, z)
    val chest = block.getState.asInstanceOf[Chest]
    chest.getBlockInventory
  }

  def writeToDatabase(owner: Player): Boolean = {
    if (inDatabase) return false
    SunnyValley.sqlite.query("INSERT INTO shipmentbox (owner, world, x, y, z) VALUES ('" + owner.getUniqueId.toString + "', '" + world.getUID.toString + "', '" + x + "', '" + y + "', '" + z + "')")
    true
  }

  def delete(): Unit = {
    SunnyValley.sqlite.query("DELETE FROM shipmentbox WHERE world='" + world.getUID.toString + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'")
    world.getBlockAt(x, y, z).breakNaturally()
  }

  def getOwner(): OfflinePlayer = {
    val result: ResultSet = SunnyValley.sqlite.query("SELECT * FROM shipmentbox WHERE world='" + world.getUID.toString + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'")
    if (result.next()) return Bukkit.getOfflinePlayer(UUID.fromString(result.getString("owner")))
    null
  }

  def inDatabase: Boolean = {
    val result: ResultSet = SunnyValley.sqlite.query("SELECT * FROM shipmentbox WHERE x='" + x + "' AND y='" + y + "' AND z='" + z + "' AND world='" + world.getUID.toString + "'")
    result.next()
  }

  def processInventory: Long = {
    if (!getOwner().isOnline) return 0

    if (world.getBlockAt(x, y, z).getBlockData.getMaterial != Material.WALL_SIGN) {
      delete()
      return 0
    }

    val signBlock = world.getBlockAt(x, y, z)
    val signMaterial: org.bukkit.material.Sign = signBlock.getState.getData.asInstanceOf[org.bukkit.material.Sign]
    val sign: org.bukkit.block.Sign = signBlock.getState.asInstanceOf[org.bukkit.block.Sign]

    if (!sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE.toString + "[ShipmentBox]")) {
      delete()
      return 0
    }

    val chestBlock = signBlock.getRelative(signMaterial.getAttachedFace)

    if (chestBlock.getBlockData.getMaterial != Material.CHEST) {
      delete()
      signBlock.breakNaturally()
      return 0
    }

    val chest: Chest = chestBlock.getState.asInstanceOf[Chest]
    val inventoryHolder: InventoryHolder = chest.getInventory.getHolder

    val saleValues: Map[Material, Long] = getSaleValues
    val playerStats: PlayerStats = new PlayerStats(getOwner().getPlayer)
    var totalGold = 0L
    for (itemStack <- inventoryHolder.getInventory.getContents) {
      if (itemStack != null && saleValues.contains(itemStack.getType)) {
        totalGold += saleValues.getOrElse(itemStack.getType, 0L) * itemStack.getAmount
        inventoryHolder.getInventory.remove(itemStack)
      }
    }

    playerStats.setGold(playerStats.getGold() + totalGold)
    totalGold
  }

  def getSaleValues: Map[Material, Long] = {
    val season = CalendarHelper.getSeason(world.getFullTime)
    var map: Map[Material, Long] = Map()

    for (pricing: String <- Main.instance.getConfig.getStringList("ShipmentBox.Crops." + WordUtils.capitalizeFully(season.name())).asScala) {
      if (pricing.contains(",")) {
        val mat = Material.valueOf(pricing.split(",")(0))
        val price = pricing.split(",")(1).toLong
        if (mat != null) map += (mat -> price)
      }
    }

    for (pricing: String <- Main.instance.getConfig.getStringList("ShipmentBox.Items").asScala) {
      if (pricing.contains(",")) {
        val mat = Material.valueOf(pricing.split(",")(0))
        val price = pricing.split(",")(1).toLong
        if (mat != null) map += (mat -> price)
      }
    }
    map
  }

}

/**
  * Static handlers
  */
object ShipmentBox {

  def handleNewDay(w: World): Unit = {
    val formatter = new DecimalFormat("#,###")
    val result: ResultSet = SunnyValley.sqlite.query("SELECT * FROM shipmentbox WHERE world='" + w.getUID.toString + "'")
    while (result.next()) {
      val shipmentBox: ShipmentBox = new ShipmentBox(w, result.getInt("x"), result.getInt("y"), result.getInt("z"))
      val amount = shipmentBox.processInventory
      if (shipmentBox.getOwner().isOnline && amount > 0) {
        shipmentBox.getOwner().getPlayer.sendMessage(ChatColor.BOLD + "Your shipments were picked up! You racked up " + ChatColor.GREEN.toString + ChatColor.BOLD.toString + "+$" + formatter.format(amount) + ChatColor.WHITE + ChatColor.BOLD.toString + "!")
      }
    }
  }

  def getAttachedSignForBlock(block: Block): Block = {
    val faces = Array(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH)
    for (face <- faces) {
      if (block.getRelative(face).getBlockData.getMaterial == Material.WALL_SIGN) {
        val sign: org.bukkit.block.Sign = block.getRelative(face).getState.asInstanceOf[org.bukkit.block.Sign]
        if (sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[ShipmentBox]")) {
          if (new ShipmentBox(sign.getWorld, sign.getX, sign.getY, sign.getZ).inDatabase) return block.getRelative(face)
        }
      }
    }
    null
  }
}
