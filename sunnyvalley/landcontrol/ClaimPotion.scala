package sunnyvalley.landcontrol

import org.bukkit.{ChatColor, Material}
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import scala.collection.JavaConverters._


object ClaimPotion {

  def getClaimPotion: ItemStack = {
    val stack:ItemStack = new ItemStack(Material.SPLASH_POTION)
    val meta: ItemMeta = stack.getItemMeta
    val lore = List(ChatColor.UNDERLINE + "LandControl", " ", "Throwing this potion will", "claim a chunk of land (16x16)", "from bedrock to the sky.")
    meta.setLore(lore.asJava)
    meta.setDisplayName(ChatColor.GREEN.toString + ChatColor.BOLD + "Chunk Claimer")
    stack.setItemMeta(meta)
    stack
  }

}
