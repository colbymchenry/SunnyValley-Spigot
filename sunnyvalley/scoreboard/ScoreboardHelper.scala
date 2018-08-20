package sunnyvalley.scoreboard

import java.util.UUID

import org.bukkit.Bukkit
import org.bukkit.event.player.{PlayerLoginEvent, PlayerQuitEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.ScoreboardManager
import sunnyvalley.Main

import scala.collection.mutable.ListBuffer

object ScoreboardHelper extends Listener {

  val manager:ScoreboardManager = Bukkit.getScoreboardManager
  val stats:ListBuffer[PlayerStats] = new ListBuffer[PlayerStats]

  @EventHandler
  def onPlayerLogin(e: PlayerLoginEvent): Unit = {
    new BukkitRunnable {
      override def run(): Unit = {
        val playerStats = new PlayerStats(e.getPlayer)
        e.getPlayer.setScoreboard(playerStats.board)
        stats += playerStats
      }
    }.runTaskLater(Main.instance, 20)
  }

  @EventHandler
  def onPlayerLogout(e: PlayerQuitEvent): Unit = {
    e.getPlayer.setScoreboard(manager.getNewScoreboard)
    stats -= getStats(e.getPlayer.getUniqueId)
  }

  def getStats(id: UUID): PlayerStats = stats.toStream.find(_.getPlayer.getUniqueId.equals(id)).orNull

}
