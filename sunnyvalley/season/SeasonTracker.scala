package sunnyvalley.season

import com.coloredcarrot.api.sidebar.{Sidebar, SidebarString}
import org.apache.commons.lang3.text.WordUtils
import org.bukkit.entity.{EntityType, Item}
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.{Bukkit, ChatColor, Material, World}
import org.bukkit.scheduler.BukkitRunnable
import sunnyvalley.{Main, PlayerStats, ShipmentBox, WorldListener}

import scala.concurrent.JavaConversions._
import scala.collection.JavaConverters._

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
              ShipmentBox.handleNewDay(world)
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

          println("CALLED")
          // handle replanting saplings
          for (elem <- world.getEntities.asScala) {
            if(elem.isInstanceOf[Item]) {
              println("BRBR")
            }
            if(elem.getType == EntityType.DROPPED_ITEM) {
              val item: Item = elem.asInstanceOf[Item]
              println(item.getItemStack.getType.name())
              if(item.getItemStack.getType == Material.ACACIA_SAPLING || item.getItemStack.getType == Material.BIRCH_SAPLING
              || item.getItemStack.getType == Material.DARK_OAK_SAPLING || item.getItemStack.getType == Material.JUNGLE_SAPLING
              || item.getItemStack.getType == Material.OAK_SAPLING) {
                println((world.getBlockAt(item.getLocation()).getType == Material.AIR) + "," + (world.getBlockAt(item.getLocation.subtract(0, 1, 0)).getType.name()) + "," + (world.getBlockAt(item.getLocation.subtract(0, 1, 0)).getType == Material.GRASS_BLOCK))

                if(world.getBlockAt(item.getLocation()).getType == Material.AIR && (world.getBlockAt(item.getLocation.subtract(0, 1, 0)).getType == Material.GRASS_BLOCK)) {
                  println("DO IT!")
                  world.getBlockAt(item.getLocation()).setType(item.getItemStack.getType)
                  item.remove()
                }
              }
            }
          }
        })
      }
    }.runTaskTimer(Main.instance, 0L, 20L)
  }

}
