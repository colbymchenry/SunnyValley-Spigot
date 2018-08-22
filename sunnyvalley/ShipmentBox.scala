package sunnyvalley

import java.sql.ResultSet
import java.util.UUID

import org.bukkit.{Bukkit, OfflinePlayer, World}
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class ShipmentBox(val world: World, val x: Int, val y: Int, val z: Int) {

  def getInventory: Inventory = {
    val block = world.getBlockAt(x, y, z)
    val chest = block.getState.asInstanceOf[Chest]
    chest.getBlockInventory
  }

  def writeToDatabase(owner: Player): Boolean = {
    if(inDatabase) return false
    SunnyValley.sqlite.query("INSERT INTO shipmentbox (owner, world, x, y, z) VALUES ('" + owner.getUniqueId.toString + "', '" + world.getUID.toString + "', '" + x + "', '" + y + "', '" + z + "')")
    true
  }

  def delete(): Unit = {
    SunnyValley.sqlite.query("DELETE FROM shipmentbox WHERE world='" + world.getUID.toString + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'")
  }

  def getOwner():OfflinePlayer = {
    val result:ResultSet = SunnyValley.sqlite.query("SELECT * FROM shipmentbox WHERE world='" + world.getUID.toString + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'")
    if(result.next()) return Bukkit.getOfflinePlayer(UUID.fromString(result.getString("owner")))
    null
  }

  def inDatabase: Boolean = {
    val result:ResultSet = SunnyValley.sqlite.query("SELECT * FROM shipmentbox WHERE x='" + x + "' AND y='" + y + "' AND z='" + z + "' AND world='" + world.getUID.toString + "'")
    result.next()
  }

}
