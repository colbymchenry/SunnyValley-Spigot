package sunnyvalley

import java.sql.ResultSet
import java.util.UUID

import org.bukkit._
import org.bukkit.block.{Block, BlockFace, Chest}
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class ChestShop(val world: World, val x: Int, val y: Int, val z: Int)  {

  def getInventory: Inventory = {
    val block = world.getBlockAt(x, y, z)
    val chest = block.getState.asInstanceOf[Chest]
    chest.getBlockInventory
  }

  def writeToDatabase(owner: Player): Boolean = {
    if (inDatabase) return false
    SunnyValley.sqlite.query("INSERT INTO chestshops (owner, world, x, y, z) VALUES ('" + owner.getUniqueId.toString + "', '" + world.getUID.toString + "', '" + x + "', '" + y + "', '" + z + "')")
    true
  }

  def delete(): Unit = {
    SunnyValley.sqlite.query("DELETE FROM chestshops WHERE world='" + world.getUID.toString + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'")
    world.getBlockAt(x, y, z).breakNaturally()
  }

  def inDatabase: Boolean = {
    val result: ResultSet = SunnyValley.sqlite.query("SELECT * FROM chestshops WHERE x='" + x + "' AND y='" + y + "' AND z='" + z + "' AND world='" + world.getUID.toString + "'")
    result.next()
  }

  def getOwner: OfflinePlayer = {
    val result: ResultSet = SunnyValley.sqlite.query("SELECT * FROM chestshops WHERE world='" + world.getUID.toString + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'")
    if (result.next()) return Bukkit.getOfflinePlayer(UUID.fromString(result.getString("owner")))
    null
  }


}

object ChestShop {

  def getAttachedSignForBlock(block: Block): Block = {
    val faces = Array(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH)
    for (face <- faces) {
      if (block.getRelative(face).getBlockData.getMaterial == Material.WALL_SIGN) {
        val sign: org.bukkit.block.Sign = block.getRelative(face).getState.asInstanceOf[org.bukkit.block.Sign]
        if (sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Shop]")) {
          if (new ShipmentBox(sign.getWorld, sign.getX, sign.getY, sign.getZ).inDatabase) return block.getRelative(face)
        }
      }
    }
    null
  }

}