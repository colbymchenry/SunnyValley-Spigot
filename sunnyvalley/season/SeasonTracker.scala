package sunnyvalley.season

import org.bukkit.{Bukkit, World}
import org.bukkit.scheduler.BukkitRunnable
import sunnyvalley.Main
import sunnyvalley.scoreboard.ScoreboardHelper.manager
import sunnyvalley.scoreboard.{PlayerStats, ScoreboardHelper}

object SeasonTracker {

  def getDaysPassed(w: World):Int = Math.floor(w.getFullTime / 1000D / 24D).toInt

  def startSeasonTask: Unit = {
    new BukkitRunnable {
      override def run(): Unit = {
        Bukkit.getWorlds.forEach(world => {
          world.getPlayers.forEach(player => {
            var playerStats = ScoreboardHelper.getStats(player.getUniqueId)
            if(playerStats != null) {
              playerStats = new PlayerStats(player)
              player.setScoreboard(manager.getNewScoreboard)
              player.setScoreboard(playerStats.board)
              playerStats.scoreDays = playerStats.objective.getScore("Days: " + CalendarHelper.getElapsedDays(world.getFullTime))
              playerStats.scoreSeason = playerStats.objective.getScore(CalendarHelper.getSeason(world.getFullTime).chatColor + CalendarHelper.getSeason(world.getFullTime).name())
              playerStats.scoreWeekday = playerStats.objective.getScore(CalendarHelper.getWeekday(world.getFullTime).name())
              playerStats.scoreTime = playerStats.objective.getScore(CalendarHelper.formatTime(world.getTime.toInt))

              playerStats.scoreGold.setScore(5)
              playerStats.scoreSeason.setScore(4)
              playerStats.scoreDays.setScore(3)
              playerStats.scoreWeekday.setScore(2)
              playerStats.scoreTime.setScore(1)
            }
          })
        })
      }
    }.runTaskTimer(Main.instance, 0L, 20L)
  }

}
