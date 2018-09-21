package sunnyvalley

import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.{EventHandler, Listener}

object PlayerLoginListener extends Listener {

  @EventHandler
  def onLogin(e: PlayerLoginEvent): Unit = {
    var result = SunnyValley.sqlite.query("SELECT * FROM friends WHERE player='" + e.getPlayer.getUniqueId.toString + "'")
    if(!result.next()) SunnyValley.sqlite.query("INSERT INTO FRIENDS (player) VALUES ('" + e.getPlayer.getUniqueId.toString + "')")
  }

}
