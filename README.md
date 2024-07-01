# PlayerDoll
Simple Standalone Fake Player Plugin for Spigot, Paper, Folia 1.20 [Java 17+]

[Modrinth](https://modrinth.com/plugin/playerdoll)

Release avaliable on Modrinth.

Updated Readme for v1.28

#### Currently supported Languages
<
<table>
  <tr>
    <th>Language</th>
    <th>Author</th>
    <th>Location</th>
  </tr>
  <tr>
    <td>default (English)</td>
    <td></td>
    <td>built-in</td>
  </tr>
  <tr>
    <td>Tranditional Chinese</td>
    <td>anonymous</td>
    <td><a href="https://github.com/sjavi4/PlayerDoll/blob/main/customlanguages/zh.yml">zh.yml</a></td>
  </tr>
</table>

Download links available from above

## Notice

<b>The plugin is finished re-construction and start Testing publicly.

If problems found on newer version, please write an issue. </b>

## Disclaimer

<b>This plugin is highly dependent on NMS, slightly changes made by Mojang / Server Jar provider might cause this plugin no longer working.<br>
Please use it with cautious</b>


<i>Features provided by this plugin are not GUARANTEE to be fully working, it is not encouraged to treat this plugin as a pay feature to other players.</i>

## Versions
Other versions and Server Mods are not guaranteed to 100% work<br>
Pre-1.20.4 versions are temporary un-supported.
<table>
  <tr>
    <th>version</th>
    <th>Spigot</th>
    <th>Paper/Purpur/...</th>
    <th>Folia</th>
  </tr>
  <tr>
    <td>1.20.3-1.20.4</td>
    <td>✓</td>
    <td>✓</td>
    <td>✓</td>
  </tr>
  <tr>
    <td>1.20.5-1.20.6</td>
    <td>✓</td>
    <td>✓</td>
    <td>✓</td>
  </tr>
  <tr>
    <td>1.21</td>
    <td>✓</td>
    <td>✓ (Exp Build #9)</td>
    <td>Not Release</td>
  </tr>
</table>

### Cautions

For BungeeCord connections, check [here](https://modrinth.com/plugin/playerdoll/version/lbTw2Mzy)

Things like database, multiverse, bungeecord might went wrong (not tested or coded to handle)

Welcome to report any issues (performance, bugs, suggestions, feedback, compatibility tests)

Doll now will connect to server by Emulating a client.
- This Should provide a better support in the future.

## How to use

### Upgrade from old version (v1.28 above)
1. Backup and delete the old configs, (except doll configs).
   * Then start up server to regenerate new configs (with comments and usages)
2. Copy settings from old config to the new one, and set for new config keys according their usage

### In game (v1.28 above)
1. player without permission will not display the corrisponding command (or arguments)
   * `doll` or `playerdoll:doll` are the main commands
   * `dollmanage` or `playerdoll:dollmanage` are the same from above, but they bypass some optional checking. OP are reqiured by default
   * To create doll, enter `/doll create <name> [skin]` and wait for server to setup the config.
   * After creating doll, enter `/doll spawn <name>` to let the doll connect to the server.
2. Most of the Doll data modification are require Doll to be online.
   * Except `Remove`
   * `/doll set` command is Doll specific settings (Doll behavior)
   * `/doll gset` command is settings for <b>all players</b> (command permission)
   * `/doll pset` command is settings for <b>specific players</b> (Override Gset if set)

3. Doll Shortcut
   * Open Doll menu GUI using `sneak` and `right-click` with <b>empty hand</b> (same as /doll menu)

4. Doll inventories
   * An alternative way to manipulate Doll inventory (`/doll inv`). It is not a direct modification. Detailed usages belows
   * <b>Actual Doll inventory</b> can be aceessed from the above GUI, by clicking the top right item. Items can be put and take directly
   * Doll's ender chest (`/doll echest`). Items can be put and take directly

Put items to Doll : Drop item and let Doll pick up

Take items from Doll : Command Drop or perform actions through Doll inventory GUI


Similar to vanilla player backpack shortcuts
```
Left Click slots in hotbar -> Set handheld slot to target slot
Right Click slots in hotbar -> Set handheld slot and perform <use> command (not placing block)
Q (Shift + Q) -> Perform Drop (Drop stack) command on target slot
F -> Swap between target slot and offhand slot
Shift Click slots -> Move hotbar/backpack slot to merge or put to the last empty backpack/hotbar slot (if avaliable)
Number keys -> Move / Swap target slot to the corresponding number key slot in hotbar
```


## Properties
1. All doll have "-" prefix as identifier to distinguish Real players and Doll
2. Because of the prefix, Doll name has only 15 of name length (obey real player name rule)
3. Doll are not gaining crafing recipes to reduce storage.
4. Doll are not count into sleeping percentage
5. Doll are default to be surivial mode
6. Doll quit the game immediate if died (not dropping loot and exp)
7. Doll being Removed by command will execute die process<br>(loot and exp are determined by gamerule `keepinventory`)<br>All related data will be deleted

## Convert Player

Convert Player is a feature can be set from config `convert-player`.
- Player will be convert to modified entity when Connect to server
- Converted Player has the ability to perform doll commands to self
  * attack, use, swapHands, drop, dropStack, lookat
- When enabled, some commands will display `_` as self indication

### Notes

If enabled, Server Reload command is not recommanded.

Converted players will be kicked from the server.

(Once reloaded, the plugin has no access to player entity anymore)



## Commands & Permissions

Some commands are not usable when Doll is on <b>Spawn Protection Area</b>

<b>Commands are now registered in vanilla brigadier</b>

Please report when any problems found on the command system

Command are followed by this format
```/doll <subcommand> <target> <arguments>```

<table>
  <tr>
    <th>Command</th>
    <th>Permission</th>
    <th>Description</th>
    <th>Note</th>
  </tr>
  <tr>
    <td>attack</td>
    <td>playerdoll.command.attack</td>
    <td>Doll attacks(left click) towards it's line of sight</td>
    <td></td>
  </tr>
  <tr>
    <td>create name [skinName]</td>
    <td>playerdoll.command.create<br>playerdoll.argument.create.skin</td>
    <td>Register Doll to server</td>
    <td>skinName: Any authenticated player name<br>Offline server uses default skins</td>
  </tr>
  <tr>
    <td>despawn</td>
    <td>playerdoll.command.despawn</td>
    <td>Offline Doll and save Data</td>
    <td></td>
  </tr>
  <tr>
    <td>drop</td>
    <td>playerdoll.command.drop</td>
    <td>Drop item</td>
    <td></td>
  </tr>
  <tr>
    <td>dropStack</td>
    <td>playerdoll.command.drop</td>
    <td>Drop item in stack</td>
    <td></td>
  </tr>
  <tr>
    <td>dismount</td>
    <td>playerdoll.command.dismount</td>
    <td>Let Doll gets off from current vehicle/entity</td>
    <td></td>
  </tr>
  <tr>
    <td>echest</td>
    <td>playerdoll.command.echest</td>
    <td>Open Doll's ender chest</td>
    <td></td>
  </tr>
  <tr>
    <td>exp [all/level]</td>
    <td>playerdoll.command.exp</td>
    <td>Get Exp from Doll</td>
    <td>Default:1 level</td>
  </tr>
  <tr>
    <td>gset</td>
    <td>playerdoll.command.gset</td>
    <td>Set Doll settings for All players</td>
    <td>This will be overridden by pset</td>
  </tr>
  <tr>
    <td>info</td>
    <td>playerdoll.command.info</td>
    <td>Show Doll data</td>
    <td>Not implemented</td>
  </tr>
  <tr>
    <td>inv</td>
    <td>playerdoll.command.inv</td>
    <td>Open Doll's inventory (snapshot)</td>
    <td>access actual inventory inside GUI</td>
  </tr>
  <tr>
    <td>jump</td>
    <td>playerdoll.command.jump</td>
    <td>Make Doll jumps</td>
    <td>Not support covert-player</td>
  </tr>
  <tr>
    <td>look</td>
    <td>playerdoll.command.look</td>
    <td>set Doll head to certain direction</td>
    <td></td>
  </tr>
  <tr>
    <td>lookat</td>
    <td>playerdoll.command.lookat</td>
    <td>Set Doll's head pointing to Entity</td>
    <td></td>
  </tr>
  <tr>
    <td>menu</td>
    <td>playerdoll.command.menu</td>
    <td>Open Doll's information panel</td>
    <td></td>
  </tr>
  <tr>
    <td>mount</td>
    <td>playerdoll.command.mount</td>
    <td>Assign Doll to ride on nearby vehicle/entity</td>
    <td></td>
  </tr>
  <tr>
    <td>move</td>
    <td>playerdoll.command.move</td>
    <td>Assign Doll to move in 4 directions</td>
    <td></td>
  </tr>
  <tr>
    <td>pset doll player</td>
    <td>playerdoll.command.pset</td>
    <td>Set Doll settings for Specific player</td>
    <td>This will override gset</td>
  </tr>
  <tr>
    <td>remove</td>
    <td>playerdoll.command.remove</td>
    <td>Remove Doll and its Data immediately</td>
    <td>No recovery</td>
  </tr>
  <tr>
    <td>set</td>
    <td>playerdoll.command.set</td>
    <td>Open Doll settings</td>
    <td></td>
  </tr>
  <tr>
    <td>slot</td>
    <td>playerdoll.command.slot</td>
    <td>Set Doll's Handheld Slot</td>
    <td></td>
  </tr>
  <tr>
    <td>sneak</td>
    <td>playerdoll.command.sneak</td>
    <td>Set sneak</td>
    <td></td>
  </tr>
  <tr>
    <td>spawn</td>
    <td>playerdoll.command.spawn</td>
    <td>Spawn Doll at player</td>
    <td></td>
  </tr>
  <tr>
    <td>sprint</td>
    <td>playerdoll.command.sprint</td>
    <td>Set sprint</td>
    <td></td>
  </tr>
  <tr>
    <td>stop</td>
    <td>playerdoll.command.stop</td>
    <td>Stop All action</td>
    <td></td>
  </tr>
  <tr>
    <td>swapHands</td>
    <td>playerdoll.command.swap</td>
    <td>Swap item between main hand and off hand</td>
    <td></td>
  </tr>
  <tr>
    <td>tp</td>
    <td>playerdoll.command.tp</td>
    <td>Teleport Doll to player</td>
    <td></td>
  </tr>
  <tr>
    <td>turn</td>
    <td>playerdoll.command.turn</td>
    <td>Rotate Doll's head by yaw(horizontal) and pitch(vertical)</td>
    <td></td>
  </tr>
  <tr>
    <td>unsneak</td>
    <td>playerdoll.command.sneak</td>
    <td>Set unsneak</td>
    <td></td>
  </tr>
  <tr>
    <td>unsprint</td>
    <td>playerdoll.command.sprint</td>
    <td>Set unsprint</td>
    <td></td>
  </tr>
  <tr>
    <td>use</td>
    <td>playerdoll.command.use</td>
    <td>Doll uses/interacts(right click) towards it's line of sight</td>
    <td></td>
  </tr>
</table>

## Permissions
If there is permission plugin installed, permissions can be set to normal players.<br>
Otherwise, only OP will be able to use the related action.
- Although some per-player setting can be set to true for specific players, they still need permission to perform those action.
### Other Permission
- Some misc permissions
<table>
  <tr>
    <th>Permission</th>
    <th>Description</th>
    <th>Default</th>
  </tr>
  <tr>
    <td>playerdoll.doll</td>
    <td>Ability to use /doll subCommands</td>
    <td>true (everyone)</td>
  </tr>
</table>

### Grouped Permission
- The followings are some presets
<table>
  <tr>
    <th>Group</th>
    <th>Description</th>
    <th>Children</th>
  </tr>
  <tr>
    <td>playerdoll.group.basic</td>
    <td>Very Basic permissions</td>
    <td>
      <ul>
        <li>playerdoll.command.create</li>
        <li>playerdoll.command.despawn</li>
        <li>playerdoll.command.spawn</li>
        <li>playerdoll.command.remove</li>
        <li>playerdoll.command.menu</li>
        <li>playerdoll.command.set</li>
        <li>playerdoll.command.gset</li>
        <li>playerdoll.command.pset</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>playerdoll.group.container</td>
    <td>Doll container related permissions</td>
    <td>
      <ul>
        <li>playerdoll.command.echest</li>
        <li>playerdoll.command.exp</li>
        <li>playerdoll.command.drop</li>
        <li>playerdoll.command.inv</li>
        <li>playerdoll.command.menu</li>
        <li>playerdoll.command.slot</li>
        <li>playerdoll.command.swap</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>playerdoll.group.movement</td>
    <td>A set of permission about movement</td>
    <td>
      <ul>
        <li>playerdoll.command.sprint</li>
        <li>playerdoll.command.stop</li>
        <li>playerdoll.command.move</li>
        <li>playerdoll.command.jump</li>
        <li>playerdoll.command.tp</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>playerdoll.group.action</td>
    <td>A set of permission for Action commands</td>
    <td>
      <ul>
        <li>playerdoll.command.attack</li>
        <li>playerdoll.command.dismount</li>
        <li>playerdoll.command.drop</li>
        <li>playerdoll.command.jump</li>
        <li>playerdoll.command.look</li>
        <li>playerdoll.command.lookat</li>
        <li>playerdoll.command.mount</li>
        <li>playerdoll.command.move</li>
        <li>playerdoll.command.slot</li>
        <li>playerdoll.command.sneak</li>
        <li>playerdoll.command.sprint</li>
        <li>playerdoll.command.stop</li>
        <li>playerdoll.command.strafe</li>
        <li>playerdoll.command.swap</li>
        <li>playerdoll.command.turn</li>
        <li>playerdoll.command.use</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>playerdoll.group.datacontrol</td>
    <td>A set of permission for modifying Doll data</td>
    <td>
      <ul>
        <li>playerdoll.command.set</li>
        <li>playerdoll.command.pset</li>
        <li>playerdoll.command.gset</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td>playerdoll.group.manage</td>
    <td>A set of permission for managing Doll</td>
    <td>
      <ul>
        <li>playerdoll.command.give</li>
      </ul>
    </td>
  </tr>
</table>

### GUI (Doll Setting) Permission
- These permissions will affect what Doll Setting can be modify by a player.

<table>
  <tr>
    <th>Permission</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>playerdoll.globalflag.glow</td>
    <td>Set Doll to Glow</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.gravity</td>
    <td>Set Doll to obey Gravity</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.hostility</td>
    <td>Set Doll to be targeted by mobs</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.invulnerable</td>
    <td>Set Doll to ignore damage</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.join_at_start</td>
    <td>Set Doll to Join automatically when Server Starts</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.large_step_size</td>
    <td>Set Doll to step up to 1 or 0.6 Block high</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.phantom</td>
    <td>Set Phantom Spawn when Long Awake</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.pickable</td>
    <td>Set Doll to Pick up nearby items or Exp</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.pushable</td>
    <td>Set Doll to be Pushable</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.real_player_tick_update</td>
    <td>Set Doll to tick update at Real Player timing</td>
  </tr>
  <tr>
    <td>playerdoll.globalflag.real_player_tick_action</td>
    <td>Set Doll to tick action at Real Player timing</td>
  </tr>
  <tr>
    <td> </td>
    <td> </td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.admin</td>
    <td>Grant All Access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.attack</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.despawn</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.dismount</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.drop</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.echest</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.exp</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.gset</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.info</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.inv</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.jump</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.look</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.lookat</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.menu</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.mount</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.move</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.pset</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.set</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.slot</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.sneak</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.spawn</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.sprint</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.stop</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.strafe</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.swap</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.tp</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.turn</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.unsneak</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.unsprint</td>
    <td>Grant command access for Player</td>
  </tr>
  <tr>
    <td>playerdoll.personalflag.use</td>
    <td>Grant command access for Player</td>
  </tr>
</table>
