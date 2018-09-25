package sunnyvalley.listeners

import java.util.logging.Level

import org.bukkit.block.Chest
import org.bukkit.{ChatColor, Material}
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.{Merchant, MerchantInventory}
import sunnyvalley.{ChestShop, Main}

object ChestShopListener extends Listener {

  /**
    * Event for creating chest shops
    */
  @EventHandler
  def onSignChanged(e: SignChangeEvent): Unit = {
    val signMaterial: org.bukkit.material.Sign = e.getBlock.getState.getData.asInstanceOf[org.bukkit.material.Sign]
    val sign: org.bukkit.block.Sign = e.getBlock.getState.asInstanceOf[org.bukkit.block.Sign]

    if (!e.getLine(0).equalsIgnoreCase("[Shop]")) return

    if (e.isCancelled) return

    if (e.getBlock.getRelative(signMaterial.getAttachedFace).getBlockData.getMaterial != Material.CHEST) {
      e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "The sign must be placed against a chest.")
      return
    }

    val attachedSign = ChestShop.getAttachedSignForBlock(e.getBlock.getRelative(signMaterial.getAttachedFace))

    if(attachedSign != null) {
      e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "There is already a shop.")
      e.setCancelled(true)
      e.getBlock.breakNaturally()
      return
    }

    try {
      // if there is somehow a shop still there within the database make sure to notify an admin to delete it
      if (!new ChestShop(sign.getWorld, sign.getX, sign.getY, sign.getZ).writeToDatabase(e.getPlayer)) {
        e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "Somehow there is still a shop here... Please notify the admins.")
        e.setCancelled(true)
        e.getBlock.breakNaturally()
      }
    } catch {
      case ex: Exception =>
        e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "A serious error occurred. Please notify the admins.")
        e.setCancelled(true)
        Main.instance.getLogger.log(Level.SEVERE, ex.getMessage, ex)
        ex.printStackTrace()
        e.getBlock.breakNaturally()
        return
    }

    val chestBlock = sign.getBlock.getRelative(signMaterial.getAttachedFace)
    val chest: Chest = chestBlock.getState.asInstanceOf[Chest]

    if(chest.getBlockInventory.getContents.length != 1) {
      e.getPlayer.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "Only place one of the item you wish to buy/sell in the chest, nothing else.")
      e.setCancelled(true)
      e.getBlock.breakNaturally()
      return
    }

// TODO: Use merchant inventories to allow players to create shops using gold nuggets
    // TODO: Gold nuggets won't really work as the max price could only be 64... Need to think of something here..
//    // create merchant:
//    Merchant merchant = Bukkit.createMerchant();
//
//    // setup trading recipes:
//    List<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();
//    MerchantRecipe recipe = new MerchantRecipe(sellingItem, 10000); // no max-uses limit
//    recipe.setExperienceReward(false); // no experience rewards
//    recipe.addIngredient(buyItem1);
//    recipe.addIngredient(buyItem2);
//    merchantRecipes.add(recipe);
//
//    // apply recipes to merchant:
//    merchant.setRecipes(merchantRecipes);
//
//    // open trading window:
//    player.openMerchant(merchant, true);

    e.setLine(0, ChatColor.DARK_BLUE.toString + "[Shop]")
    e.setLine(1, "")
    e.setLine(2, ChatColor.UNDERLINE.toString + "Owner")
    e.setLine(3, e.getPlayer.getName)
    e.getPlayer.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "Shipment box created! Shipments will be picked up at 12:00am every morning.")
  }
}
