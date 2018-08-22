package sunnyvalley

import lib.PatPeter.SQLibrary.{Database, SQLite}
import sunnyvalley.season.SeasonTracker

object SunnyValley {

  val sqlite:Database = new SQLite(Main.instance.getLogger, Main.instance.getName, Main.instance.getDataFolder.getAbsolutePath, "storage")

  def onEnable(): Unit = {
    sqlite.open()
    createTables()
    Main.instance.saveDefaultConfig()
    Main.instance.getServer.getPluginManager.registerEvents(WorldListener, Main.instance)
    SeasonTracker.startSeasonTask
  }

  def onDisable: Unit = {
    sqlite.close()
  }

  def createTables(): Unit = {
    sqlite.query("CREATE TABLE IF NOT EXISTS gold (player VARCHAR(36) NOT NULL, amount LONG DEFAULT 0)")
    sqlite.query("CREATE TABLE IF NOT EXISTS shipmentbox (owner VARCHAR(36) NOT NULL, world VARCHAR(36) NOT NULL, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL)")
  }

}
