package sunnyvalley.season

import com.coloredcarrot.api.sidebar.{Sidebar, SidebarString}
import org.apache.commons.lang3.text.WordUtils
import org.bukkit.{Bukkit, ChatColor, World}
import org.bukkit.scheduler.BukkitRunnable
import sunnyvalley.{Main, PlayerStats, WorldListener}

// TODO: Come up with a new name for this
object SeasonTracker {

  var worldDays:Map[World, Integer] = Map()

  def startSeasonTask: Unit = {
    new BukkitRunnable {
      override def run(): Unit = {
        Bukkit.getWorlds.forEach(world => {
          // handle tracking days so we can do something on a new day
          if(!worldDays.contains(world)) worldDays += (world -> CalendarHelper.getElapsedDays(world.getFullTime))
          else {
            if(worldDays(world).intValue() != CalendarHelper.getElapsedDays(world.getFullTime)) {
              WorldListener.onNewDay(world)
              worldDays -= world
              worldDays += (world -> CalendarHelper.getElapsedDays(world.getFullTime))
            }
          }

          // go through all the players in the world and update their sidebar information
          world.getPlayers.forEach(player => {
            val playerStats = new PlayerStats(player)
            if (playerStats != null) {
              val goldLine = new SidebarString(ChatColor.GOLD + "Gold: " + playerStats.getGold())
              val dayLine = new SidebarString("Day: " + CalendarHelper.getDayOfMonth(world.getFullTime))
              val seasonLine = new SidebarString(CalendarHelper.getSeason(world.getFullTime).chatColor + WordUtils.capitalizeFully(CalendarHelper.getSeason(world.getFullTime).name()))
              val weekdayLine = new SidebarString(WordUtils.capitalizeFully(CalendarHelper.getWeekday(world.getFullTime).name()))

              val timeLine = new SidebarString(CalendarHelper.formatTime(world.getTime.toInt))
              val blankLine = new SidebarString("")

              val sidebar = new Sidebar("SunnyValley", Main.instance, 60, weekdayLine, timeLine, seasonLine, dayLine, blankLine, goldLine)
              sidebar.showTo(player)
            }
          })
        })
      }
    }.runTaskTimer(Main.instance, 0L, 20L)
  }

}
