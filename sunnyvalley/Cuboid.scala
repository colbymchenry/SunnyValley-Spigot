package sunnyvalley

import org.bukkit.{Location, World}

class Cuboid(var world: World, var x1: Int, var y1: Int, var z1: Int, var x2: Int, var y2: Int, var z2: Int) {

  def getMaxLoc: Location = new Location(world, Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))

  def getMinLoc: Location = new Location(world, Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2))

  def contains(x: Int, y: Int, z: Int): Unit = {
    val min = getMinLoc
    val max = getMaxLoc
    x >= min.getBlockX && x <= max.getBlockX && y >= min.getBlockY && y <= max.getBlockY && z >= min.getBlockZ && z <= max.getBlockZ
  }

}
