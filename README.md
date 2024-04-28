# PlayerDoll
Simple Standalone Fake Player Plugin for Spigot 1.20 [Java 17+]

[Modrinth](https://modrinth.com/plugin/playerdoll)

Release avaliable on Modrinth.

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
    <td>1.20.3</td>
    <td>✓</td>
    <td>✓</td>
    <td>✓</td>
  </tr>
  <tr>
    <td>1.20.4</td>
    <td>✓</td>
    <td>✓</td>
    <td>✓</td>
  </tr>
  <tr>
    <td>1.20.5</td>
    <td>✓</td>
    <td>✓ (experimental build #6)</td>
    <td>Not Release</td>
  </tr>
</table>

### Cautions
Things like database, multiverse, bungeecord might went wrong (not tested or coded to handle)

Welcome to report any issues (performance, bugs, suggestions, feedback, compatibility tests)

v1.25 : Doll now will connect to server by Emulating a client.<br>
- This Should provide a better support in the future.



## Functionalities
- Do most as player does
- Able to make changes to the world
- Support automatic actions

## Properties
1. Once Doll has spawned, playerdata will be created
2. Once Doll has created, config data will be created
3. All doll have "-" prefix as identifier to distinguish Real players and Doll
4. Doll data are expected to not to clash with Real players data (maybe there are players with "-" at the start on their name)
5. Based on the previous, Doll name has only 15 of name length (obey real player name rule)
6. Doll are not gaining crafing recipes to reduce storage.

## Commands
### Note
Some commands are not usable when Doll is on Spawn Protection Area

All all [action] are specify as this option.
```
once : Do the action once (default when not stated)
interval <intervals> <offset> : Do the action by intervals(ticks) and delayed by offset(ticks)
continuous : Continuously repeat the action
```
`[] represents optional; <> represents reqiured`

<table>
  <tr>
    <th>Command<br>(/doll &lt;target&gt; ?)</th>
    <th>Permission</th>
    <th>Description</th>
    <th>Note</th>
  </tr>
  <tr>
    <td>attack [action]</td>
    <td>playerdoll.command.attack</td>
    <td>Doll attacks(left click) towards it's line of sight</td>
    <td></td>
  </tr>
  <tr>
    <td>copy <Target></td>
    <td>playerdoll.command.copy</td>
    <td>Copy other Doll's action to Doll</td>
    <td></td>
  </tr>
  <tr>
    <td>create [skinName]</td>
    <td>playerdoll.command.create</td>
    <td>Register Doll to server</td>
    <td>skinName: Any authenticated player name<br>Default using creator's skin<br>Offline server uses default skins</td>
  </tr>
  <tr>
    <td>despawn</td>
    <td>playerdoll.command.despawn</td>
    <td>Offline Doll and save Data</td>
    <td></td>
  </tr>
  <tr>
    <td>drop [stack/single] [slots/action]</td>
    <td>playerdoll.command.drop</td>
    <td>Drop Doll's Item from Slot</td>
    <td>stack:Drop in maximum amount<br>Default:Drop 1 handheld item</td>
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
    <td></td>
  </tr>
  <tr>
    <td>jump [action]</td>
    <td>playerdoll.command.jump</td>
    <td>Make Doll jumps</td>
    <td></td>
  </tr>
  <tr>
    <td>look &lt;yaw&gt; &lt;pitch&gt;<br>look &lt;direction/player&gt;</td>
    <td>playerdoll.command.look</td>
    <td>Copy Doll's head to target's orientation</td>
    <td>[yaw pitch]In +/-ve decimal</td>
  </tr>
  <tr>
    <td>lookat &lt;X&gt; &lt;Y&gt; &lt;Z&gt;<br>lookat &lt;player&gt;<br>lookat target [action]</td>
    <td>playerdoll.command.lookat</td>
    <td>Set Doll's head pointing to player/coordinates/Entity</td>
    <td>[X Y Z]In +/-ve decimal</td>
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
    <td>move [forwards/backwards]</td>
    <td>playerdoll.command.move</td>
    <td>Assign Doll to move forward(W) or backward(S)</td>
    <td></td>
  </tr>
  <tr>
    <td>pset &lt;player&gt;</td>
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
    <td>slot [1-9]</td>
    <td>playerdoll.command.slot</td>
    <td>Set Doll's Handheld Slot</td>
    <td>Default:Slot 1</td>
  </tr>
  <tr>
    <td>sneak [true/false]</td>
    <td>playerdoll.command.sneak</td>
    <td>Toggle Doll to (un)sneak</td>
    <td>Default:Opposite from Current</td>
  </tr>
  <tr>
    <td>spawn [gridded]</td>
    <td>playerdoll.command.spawn</td>
    <td>Spawn Doll at player position and copy player's pitch and yaw</td>
    <td>gridded: Align at nearest line/quarter center</td>
  </tr>
  <tr>
    <td>sprint [true/false]</td>
    <td>playerdoll.command.sprint</td>
    <td>Toggle Doll to (un)sprint</td>
    <td>Default:Opposite from Current</td>
  </tr>
  <tr>
    <td>stop [all/movement]</td>
    <td>playerdoll.command.stop</td>
    <td>Stop Doll Action</td>
    <td>Default:All</td>
  </tr>
  <tr>
    <td>strafe [left/right]</td>
    <td>playerdoll.command.strafe</td>
    <td>Assign Doll to move left(A) or right(D)</td>
    <td></td>
  </tr>
  <tr>
    <td>swap [action]</td>
    <td>playerdoll.command.swap</td>
    <td>Swap item between Doll's main hand and off hand</td>
    <td></td>
  </tr>
  <tr>
    <td>tp [gridded]</td>
    <td>playerdoll.command.tp</td>
    <td>Teleport Doll to player and copy player's pitch and yaw</td>
    <td>gridded: Align at nearest line/quarter center</td>
  </tr>
  <tr>
    <td>turn &lt;yaw&gt; &lt;pitch&gt;</td>
    <td>playerdoll.command.turn</td>
    <td>Rotate Doll's head by yaw(horizontal) and pitch(vertical)</td>
    <td>[yaw pitch]In +/-ve decimal</td>
  </tr>
  <tr>
    <td>use [action]</td>
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
  <tr>
    <td>playerdoll.reload</td>
    <td>/dollReload command</td>
    <td>op</td>
  </tr>
  <tr>
    <td>playerdoll.help</td>
    <td>/dollHelp command</td>
    <td>true (everyone)</td>
  </tr>
  <tr>
    <td>playerdoll.list</td>
    <td>/dollList command</td>
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
        <li>playerdoll.command.strafe</li>
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
        <li>playerdoll.command.copy</li>
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
        <li>playerdoll.command.rename</li>
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
    <td>playerdoll.globalflag.echest</td>
    <td>Global Access of Doll Ender Chest</td>
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
    <td>playerdoll.globalflag.inv</td>
    <td>Global access of Doll Inventory</td>
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
    <td>playerdoll.personalflag.copy</td>
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
    <td>playerdoll.personalflag.use</td>
    <td>Grant command access for Player</td>
  </tr>
</table>


## Shortcut
- Player sneaks and right click Doll with bare hand can open Doll status GUI (same as Menu)

## Doll inventories
- Players with permission can access Doll's ender chest directly (put&take)
- Doll inventory are not supported to do so because of there is no access for armor/offhand slot in GUI view

Put items to Doll : Drop item and let Doll pick up

Take items from Doll : Command Drop or perform actions through Doll inventory navigation GUI

An indirect solution to interact with Doll inventory (upper inventory navigation action) is provided

Similar to vanilla player backpack shortcuts
```
Left Click slots in hotbar -> Set handheld slot to target slot
Right Click slots in hotbar -> Set handheld slot and perform <use> command (not placing block)
Q (Shift + Q) -> Perform Drop (Drop stack) command on target slot
F -> Swap between target slot and offhand slot
Shift Click slots -> Move hotbar/backpack slot to merge or put to the last empty backpack/hotbar slot (if avaliable)
Number keys -> Move / Swap target slot to the corresponding number key slot in hotbar
```
