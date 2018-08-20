package sunnyvalley.scoreboard

import java.sql.ResultSet

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard._
import sunnyvalley.SunnyValley
import sunnyvalley.scoreboard.ScoreboardHelper.manager

class PlayerStats(player: Player) {

  val board:Scoreboard = manager.getNewScoreboard
  val team:Team = board.registerNewTeam("main")
  val objective:Objective = board.registerNewObjective("stats", "dummy")
  objective.setDisplaySlot(DisplaySlot.SIDEBAR)
  objective.setDisplayName("Stats")

  var scoreGold:Score = _
  var scoreDays:Score = _
  var scoreSeason:Score = _
  var scoreWeekday:Score = _
  var scoreTime:Score = _

  team.addPlayer(player)
  initialSetup()

  def initialSetup() {
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

  def getPlayer:Player = player

}
