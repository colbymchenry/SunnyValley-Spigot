package sunnyvalley.commands

import java.util.UUID

import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player
import org.bukkit.{Bukkit, ChatColor, OfflinePlayer, Sound}
import sunnyvalley.SunnyValley

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object CommandFriend extends CommandExecutor {

  var requests: Map[Player, ArrayBuffer[Player]] = Map()

  override def onCommand(sender: CommandSender, cmd: Command, s: String, strings: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) return false

    if (strings.isEmpty) return false


    if (strings(0).equalsIgnoreCase("add") || strings(0).equalsIgnoreCase("remove")
      || strings(0).equalsIgnoreCase("accept") || strings(0).equalsIgnoreCase("deny")) {
      // if the player just types accept or deny we accept or deny the last request received
      if(strings.length == 1) {
        if(requests(sender.asInstanceOf[Player]).nonEmpty) {
          if (strings(0).equalsIgnoreCase("accept")) acceptFriendRequest(sender.asInstanceOf[Player], requests(sender.asInstanceOf[Player])(0).getName)
          if (strings(0).equalsIgnoreCase("deny")) denyFriendRequest(sender.asInstanceOf[Player], requests(sender.asInstanceOf[Player])(0).getName)
          return true
        }
      }

      if (strings.length != 2) return false

      val receiver = Bukkit.getPlayer(strings(1))

      if (strings(0).equalsIgnoreCase("add")) sendFriendRequest(sender.asInstanceOf[Player], receiver)
      else if (strings(0).equalsIgnoreCase("remove")) destroyFriendship(sender.asInstanceOf[Player], receiver)
      else if (strings(0).equalsIgnoreCase("accept")) acceptFriendRequest(sender.asInstanceOf[Player], strings(1))
      else if (strings(0).equalsIgnoreCase("deny")) denyFriendRequest(sender.asInstanceOf[Player], strings(1))
      else return false

    } else if (strings(0).equalsIgnoreCase("requests")) sendFriendRequests(sender.asInstanceOf[Player])
    else if (strings(0).equalsIgnoreCase("list")) sendFriendsList(sender.asInstanceOf[Player])
    else return false

    true
  }

  def sendFriendRequest(sender: Player, receiver: Player): Unit = {
    if (receiver == null || !receiver.isOnline) {
      sender.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "We could not find a player by that name.")
      return
    }

    if(receiver.getUniqueId.equals(sender.getUniqueId)) {
      sender.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "You cannot send yourself a friend request.")
      return
    }

    // make sure the player's are not already friends
    for (elem <- getFriends(sender)) {
      if (elem.getUniqueId.equals(receiver.getUniqueId)) {
        sender.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "You're already friends with " + receiver.getName + ".")
        return
      }
    }

    // make sure they can't spam friend requests
    if (requests.contains(receiver) && requests(receiver).contains(sender)) {
      sender.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "You've already sent " + receiver.getName + " a friend request.")
      return
    }

    // make them friends if the sender has already received a friend request from the receiver
    if (requests.contains(sender) && requests(sender).contains(receiver)) {
      makeFriendship(sender, receiver)
      sender.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "You are now friends with " + receiver.getName + ".")
      receiver.sendMessage(ChatColor.YELLOW + "Notify! " + ChatColor.RESET + sender.getName + " accepted your friend request.")
      receiver.playSound(receiver.getLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1)
      // remove the old friend request
      requests(sender) -= receiver
      return
    }

    var buffer: ArrayBuffer[Player] = if (requests.contains(receiver)) requests(receiver) else ArrayBuffer[Player]()
    buffer += sender
    requests -= receiver
    requests += (receiver -> buffer)
    sender.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "Friend request sent!")
    receiver.sendMessage(ChatColor.YELLOW + "Alert! " + ChatColor.RESET + sender.getName + " wants to be your friend!")
    receiver.sendMessage(ChatColor.AQUA + "/f accept" + ChatColor.RESET + " to accept.")
    receiver.sendMessage(ChatColor.AQUA + "/f deny" + ChatColor.RESET + " to deny.")
    receiver.playSound(receiver.getLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1)
  }

  def acceptFriendRequest(player: Player, playerName: String): Unit = {
    if (!requests.contains(player) || requests(player).isEmpty) {
      player.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "You don't have any pending friend requests.")
      return
    }

    val receiver = requests(player).toStream.find(p => p.getName.equalsIgnoreCase(playerName)).get

    if (receiver == null) {
      sendFriendRequests(player)
      return
    }

    makeFriendship(player, receiver)
    player.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "You are now friends with " + receiver.getName + ".")
    receiver.sendMessage(ChatColor.YELLOW + "Notify! " + ChatColor.RESET + player.getName + " accepted your friend request.")
    receiver.playSound(receiver.getLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1)
    // remove the old friend request
    requests(player) -= receiver
  }

  def denyFriendRequest(player: Player, playerName: String): Unit = {
    if (!requests.contains(player) || requests(player).isEmpty) {
      player.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "You don't have any pending friend requests.")
      return
    }

    val receiver = requests(player).toStream.find(p => p.getName.equalsIgnoreCase(playerName)).get

    if (receiver == null) {
      sendFriendRequests(player)
      return
    }

    player.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "You denied " + receiver.getName + "'s friend request.")
    receiver.sendMessage(ChatColor.YELLOW + "Notify! " + ChatColor.RESET + player.getName + " denied your friend request.")
    receiver.playSound(receiver.getLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1)
    // remove the old friend request
    requests(player) -= receiver
  }

  def sendFriendRequests(player: Player): Unit = {
    if (!requests.contains(player)) {
      player.sendMessage("You have no pending friend requests.")
      return
    }

    player.sendMessage(ChatColor.YELLOW + "-------[" + ChatColor.GOLD + "FriendRequests" + ChatColor.YELLOW + "]-------")
    var i = 1
    for (elem <- requests(player)) {
      player.sendMessage(ChatColor.YELLOW + i.toString + ". " + ChatColor.GOLD + elem.getName)
      i += 1
    }
  }

  def makeFriendship(player1: Player, player2: Player): Unit = {
    val result1 = SunnyValley.sqlite.query("SELECT * FROM friends WHERE player='" + player1.getUniqueId.toString + "'")
    val result2 = SunnyValley.sqlite.query("SELECT * FROM friends WHERE player='" + player2.getUniqueId.toString + "'")

    if(!result1.next() || !result2.next()) return

    val friends1 = result1.getString("friends")
    val friends2 = result2.getString("friends")

    SunnyValley.sqlite.query("UPDATE friends SET friends='" + (player2.getUniqueId.toString + "," + friends1) + "' WHERE player='" + player1.getUniqueId.toString + "'")
    SunnyValley.sqlite.query("UPDATE friends SET friends='" + (player1.getUniqueId.toString + "," + friends2) + "' WHERE player='" + player2.getUniqueId.toString + "'")
  }

  def destroyFriendship(player1: Player, player2: Player): Unit = {
    if (player2 == null) {
      player1.sendMessage(ChatColor.RED + "Uh-Oh! " + ChatColor.RESET + "We could not find a player by that name.")
      return
    }

    val result1 = SunnyValley.sqlite.query("SELECT * FROM friends WHERE player='" + player1.getUniqueId.toString + "'")
    val result2 = SunnyValley.sqlite.query("SELECT * FROM friends WHERE player='" + player2.getUniqueId.toString + "'")

    if(!result1.next() || !result2.next()) return

    val friends1 = result1.getString("friends")
    val friends2 = result2.getString("friends")

    SunnyValley.sqlite.query("UPDATE friends SET friends='" + friends1.replace(player2.getUniqueId.toString + ",", "") + "' WHERE player='" + player1.getUniqueId.toString + "'")
    SunnyValley.sqlite.query("UPDATE friends SET friends='" + friends2.replace(player1.getUniqueId.toString + ",", "") + "' WHERE player='" + player2.getUniqueId.toString + "'")
    player1.sendMessage(ChatColor.GREEN + "Success! " + ChatColor.RESET + "You are no longer friends with " + player2.getName + ".")
  }

  def getFriends(player: Player): List[OfflinePlayer] = {
    val friends: ListBuffer[OfflinePlayer] = new ListBuffer[OfflinePlayer]
    val result = SunnyValley.sqlite.query("SELECT * FROM friends WHERE player='" + player.getUniqueId.toString + "'")
    if(!result.next()) return friends.toList
    if (!result.getString("friends").isEmpty) {
      result.getString("friends").split(",").foreach(playerUUID => {
        if (!playerUUID.isEmpty) friends += Bukkit.getOfflinePlayer(UUID.fromString(playerUUID))
      })
    }
    friends.toList
  }

  def sendFriendsList(player: Player): Unit = {
    for (elem <- getFriends(player)) if(elem.isOnline) player.sendMessage(ChatColor.GREEN + player.getName)
    for (elem <- getFriends(player)) if(!elem.isOnline) player.sendMessage(ChatColor.RED + player.getName)
  }


}
