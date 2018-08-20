package sunnyvalley

import lib.PatPeter.SQLibrary.{Database, SQLite}
import org.bukkit.plugin.java.JavaPlugin
import sunnyvalley.season.SeasonTracker

object SunnyValley {

  var sqlite:Database = _

  def onEnable(plugin: JavaPlugin): Unit = {
    sqlite = new SQLite(plugin.getLogger, plugin.getName, plugin.getDataFolder.getAbsolutePath, "storage")
    sqlite.open()
    createTables()
    plugin.saveDefaultConfig()
    plugin.getServer.getPluginManager.registerEvents(WorldListener, plugin)
    SeasonTracker.startSeasonTask
  }

  def createTables(): Unit = {
    sqlite.query("CREATE TABLE IF NOT EXISTS gold (player VARCHAR(36) NOT NULL, amount LONG DEFAULT 0)")
  }

}
