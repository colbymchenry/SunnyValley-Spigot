package sunnyvalley.landcontrol

import java.sql.ResultSet
import java.util.UUID

import org.bukkit._
import org.bukkit.entity.Player
import sunnyvalley.{Cuboid, SunnyValley}

// TODO: New form of land control, we want to keep players inside a city limit, so they do command /claim to pay rent on each claim they are renting
class LandClaim(world: World, chunkX: Int, chunkZ: Int) {

  def writeToDatabase(owner: Player): Boolean = {
    if (inDatabase) return false
    SunnyValley.sqlite.query("INSERT INTO landclaim (owner, world, x, z) VALUES ('" + owner.getUniqueId.toString + "', '" + world.getUID.toString + "', '" + chunkX + "', '" + chunkZ + "')")
    true
  }

  def delete(): Unit = {
    SunnyValley.sqlite.query("DELETE FROM landclaim WHERE world='" + world.getUID.toString + "' AND x='" + chunkX + "' AND z='" + chunkZ + "'")
  }

  def getOwner(): OfflinePlayer = {
    val result: ResultSet = getResult
    if (result.next()) return Bukkit.getOfflinePlayer(UUID.fromString(result.getString("owner")))
    null
  }

  def inDatabase: Boolean = getResult.next()

  def getResult: ResultSet = SunnyValley.sqlite.query("SELECT * FROM landclaim WHERE x='" + chunkX + "' AND z='" + chunkZ + "' AND world='" + world.getUID.toString + "'")

  def getBounds: Cuboid = new Cuboid(world, chunkX * 16, 0, chunkZ * 16, chunkX * 16 + 16, world.getMaxHeight, chunkZ * 16 + 16)

}
