# PlayerDoll
Simple Standalone Fake Player Plugin for Spigot 1.20

[Modrinth](https://modrinth.com/plugin/playerdoll)

Release avaliable on Modrinth.

## Versions
Spigot, Paper, Purpur, Folia (maybe more)
- 1.20.1 - 1.20.4
### Cautions
Things like SQL, multiverse, bungeecord might went wrong (not tested or coded to handle)

Welcome to report any issues (performance, bugs, suggestions, feedback, compatibility tests)

## Functionalities
- Do most as player does
- Able to make changes to the world
- Support automatic actions

## Properties
1. Once Doll has spawned, playerdata will be created
2. Once Doll has created, config data will be created
3. All doll have "-" prefix as identifier to distinguish Real players and Doll
4. Doll data are expected to not to clash with Real players (maybe there are players with "-" on their name)
5. Based on the previous, Doll name has only 15 letters to choose (obey real player name rule)

## Commands
### Note
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
    <th>Description</th>
    <th>Note</th>
  </tr>
  <tr>
    <td>attack [action]</td>
    <td>Doll attacks(left click) towards it's line of sight</td>
    <td></td>
  </tr>
  <tr>
    <td>copy <Target></td>
    <td>Copy other Doll's action to Doll</td>
    <td></td>
  </tr>
  <tr>
    <td>create [skinName]</td>
    <td>Register Doll to server</td>
    <td>skinName: Any authenticated player name<br>Default using creator's skin<br>Offline server uses default skins</td>
  </tr>
  <tr>
    <td>despawn</td>
    <td>Offline Doll and save Data</td>
    <td></td>
  </tr>
  <tr>
    <td>drop [stack/single] [slots/action]</td>
    <td>Drop Doll's Item from Slot</td>
    <td>stack:Drop in maximum amount<br>Default:Drop 1 handheld item</td>
  </tr>
  <tr>
    <td>dismount</td>
    <td>Let Doll gets off from current vehicle/entity</td>
    <td></td>
  </tr>
  <tr>
    <td>echest</td>
    <td>Open Doll's ender chest</td>
    <td></td>
  </tr>
  <tr>
    <td>exp [all/level]</td>
    <td>Get Exp from Doll</td>
    <td>Default:1 level</td>
  </tr>
  <tr>
    <td>gset</td>
    <td>Set Doll settings for All players</td>
    <td>This will be overridden by pset</td>
  </tr>
  <tr>
    <td>info</td>
    <td>Show Doll data</td>
    <td>Not implemented</td>
  </tr>
  <tr>
    <td>inv</td>
    <td>Open Doll's inventory</td>
    <td></td>
  </tr>
  <tr>
    <td>jump [action]</td>
    <td>Make Doll jumps</td>
    <td></td>
  </tr>
  <tr>
    <td>look &lt;yaw&gt; &lt;pitch&gt;<br>look &lt;direction/player&gt;</td>
    <td>Copy Doll's head to target's orientation</td>
    <td>[yaw pitch]In +/-ve decimal</td>
  </tr>
  <tr>
    <td>lookat &lt;X&gt; &lt;Y&gt; &lt;Z&gt;<br>lookat &lt;player&gt;<br>lookat target [action]</td>
    <td>Set Doll's head pointing to player/coordinates/Entity</td>
    <td>[X Y Z]In +/-ve decimal</td>
  </tr>
  <tr>
    <td>menu</td>
    <td>Open Doll's information panel</td>
    <td></td>
  </tr>
  <tr>
    <td>mount</td>
    <td>Assign Doll to ride on nearby vehicle/entity</td>
    <td></td>
  </tr>
  <tr>
    <td>move [forwards/backwards]</td>
    <td>Assign Doll to move forward(W) or backward(S)</td>
    <td></td>
  </tr>
  <tr>
    <td>pset <player></td>
    <td>Set Doll settings for Specific player</td>
    <td>This will override gset</td>
  </tr>
  <tr>
    <td>remove</td>
    <td>Remove Doll and its Data immediately</td>
    <td>No recovery</td>
  </tr>
  <tr>
    <td>set</td>
    <td>Open Doll settings</td>
    <td></td>
  </tr>
  <tr>
    <td>slot [1-9]</td>
    <td>Set Doll's Handheld Slot</td>
    <td>Default:Slot 1</td>
  </tr>
  <tr>
    <td>sneak [true/false]</td>
    <td>Toggle Doll to (un)sneak</td>
    <td>Default:Opposite from Current</td>
  </tr>
  <tr>
    <td>spawn [gridded]</td>
    <td>Spawn Doll at player position and copy player's pitch and yaw</td>
    <td>gridded: Align at nearest line/quarter center</td>
  </tr>
  <tr>
    <td>sprint [true/false]</td>
    <td>Toggle Doll to (un)sprint</td>
    <td>Default:Opposite from Current</td>
  </tr>
  <tr>
    <td>stop [all/movement]</td>
    <td>Stop Doll Action</td>
    <td>Default:All</td>
  </tr>
  <tr>
    <td>strafe [left/right]</td>
    <td>Assign Doll to move left(A) or right(D)</td>
    <td></td>
  </tr>
  <tr>
    <td>swap [action]</td>
    <td>Swap item between Doll's main hand and off hand</td>
    <td></td>
  </tr>
  <tr>
    <td>tp [gridded]</td>
    <td>Teleport Doll to player and copy player's pitch and yaw</td>
    <td>gridded: Align at nearest line/quarter center</td>
  </tr>
  <tr>
    <td>turn &lt;yaw&gt; &lt;pitch&gt;</td>
    <td>Rotate Doll's head by yaw(horizontal) and pitch(vertical)</td>
    <td>[yaw pitch]In +/-ve decimal</td>
  </tr>
  <tr>
    <td>use [action]</td>
    <td>Doll uses/interacts(right click) towards it's line of sight</td>
    <td></td>
  </tr>
</table>

## Shortcut
- Player sneaks and right click Doll can open Doll status GUI (same as Menu)

## Doll inventories
- Players with permission can access Doll's ender chest directly (put&take)
- Doll inventory are not supported to do so base on some technological reasons (no direct api for open-up player inventory)

Put items to Doll : Drop item and let Doll pick up

Take items from Doll : Command Drop or perform actions through Doll inventory navigation GUI

There is an in-direct solution to let players to interact with Doll inventory (upper inventory navigation action)

Similar to vanilla player backpack shortcuts
```
Left Click slots in hotbar -> Set handheld slot to target slot
Right Click slots in hotbar -> Set handheld slot and perform <use> command (not placing block)
Q (Shift + Q) -> Perform Drop (Drop stack) command on target slot
F -> Swap between target slot and offhand slot
Shift Click slots -> Move hotbar/backpack slot to merge or put to the last empty backpack/hotbar slot (if avaliable)
Number keys -> Move / Swap target slot to the corresponding number key slot in hotbar
```
