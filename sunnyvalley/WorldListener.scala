package sunnyvalley

import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.{EventHandler, Listener}

object WorldListener extends Listener {

  @EventHandler
  def onEntitySpawn(e: CreatureSpawnEvent): Unit = {
    if(e.getEntityType != EntityType.PLAYER && !Main.instance.getConfig.getBoolean("EnableAnimalNaturalSpawning", false))
      e.setCancelled(true)
  }

  def onNewDay(w: World): Unit ={
    println("NEW DAY!")
  }

}
