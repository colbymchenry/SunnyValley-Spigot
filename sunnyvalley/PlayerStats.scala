package sunnyvalley

import java.sql.ResultSet

import org.bukkit.entity.Player

class PlayerStats(val player: Player) {

  insertIntoDb()

  def insertIntoDb() {
    var result:ResultSet = SunnyValley.sqlite.query("SELECT * FROM gold WHERE player='" + player.getUniqueId.toString + "'")

    if(!result.next()) {
      insertToDatabase()
      result = SunnyValley.sqlite.query("SELECT * FROM gold WHERE player='" + player.getUniqueId.toString + "'")
    }
  }

  def insertToDatabase(): Unit = {
    SunnyValley.sqlite.query("INSERT INTO gold (player) VALUES ('" + player.getUniqueId.toString + "')")
  }

  def setGold(x: Int): Unit = {
    SunnyValley.sqlite.query("UPDATE gold SET amount='" + x +"' WHERE player='" + player.getUniqueId.toString + "'")
  }

  def getGold(): Long = {
    val result = SunnyValley.sqlite.query("SELECT * FROM gold WHERE player='" + player.getUniqueId.toString + "'")
    if(result.next()) return result.getLong("amount")
    0L
  }

  def getPlayer:Player = player

}
