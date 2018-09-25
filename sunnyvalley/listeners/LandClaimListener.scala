package sunnyvalley.listeners

import org.bukkit.entity.{EntityType, Player, Projectile, SplashPotion}
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.{ChatColor, Chunk, Material, World}
import sunnyvalley.landcontrol.{ClaimPotion, LandClaim}

object LandClaimListener extends Listener {

  @EventHandler
  def onPotionSplash(e: ProjectileHitEvent): Unit = {
    val projectile: Projectile = e.getEntity
    if (projectile.getType != EntityType.SPLASH_POTION) return

    val stack: ItemStack = projectile.asInstanceOf[SplashPotion].getItem
    val meta: ItemMeta = stack.getItemMeta

    if (!meta.getDisplayName.equalsIgnoreCase(ChatColor.GREEN.toString + ChatColor.BOLD.toString + "Chunk Claimer")) return
    if (!e.getEntity.getShooter.isInstanceOf[Player]) return

    val player: Player = e.getEntity.getShooter.asInstanceOf[Player]
    val world: World = e.getEntity.getWorld
    val chunk: Chunk = world.getChunkAt(e.getHitBlock)
    val chunkBlockX = chunk.getX * 16
    val chunkBlockZ = chunk.getZ * 16
    val chunkBlockY = e.getHitBlock.getY + 1

    val landClaim: LandClaim = new LandClaim(world, chunk.getX, chunk.getZ)

    if (landClaim.inDatabase) {
      player.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.WHITE + "That chunk is already claimed.")
      player.getInventory.addItem(ClaimPotion.getClaimPotion)
      return
    }

    // do left side
    for (x: Int <- chunkBlockX until chunkBlockX + 16) {
      world.getBlockAt(x, chunkBlockY, chunkBlockZ).setType(Material.ACACIA_FENCE)
      world.getBlockAt(x, chunkBlockY, chunkBlockZ + 15).setType(Material.ACACIA_FENCE)
    }
    // do right side
    for (z: Int <- chunkBlockZ until chunkBlockZ + 16) {
      world.getBlockAt(chunkBlockX, chunkBlockY, z).setType(Material.ACACIA_FENCE)
      world.getBlockAt(chunkBlockX + 15, chunkBlockY, z).setType(Material.ACACIA_FENCE)
    }

    landClaim.writeToDatabase(player)
    player.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.WHITE + "Chunk claimed!")
  }

}
