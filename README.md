# PlayerDoll
Simple Standalone Fake Player Plugin for Spigot, Paper, Folia 1.20 [Java 17+]

[Modrinth](https://modrinth.com/plugin/playerdoll)

Release avaliable on Modrinth.



Detailed Usage has been migrated to [Wiki](https://github.com/sjavi4/PlayerDoll/wiki)


## Disclaimer

<b>This plugin is highly dependent on NMS, slightly changes made by Mojang / Server Jar provider might cause this plugin no longer working.<br>
Please use it with cautious</b>


<i>Features provided by this plugin are not GUARANTEE to be fully working, it is not encouraged to treat this plugin as a pay feature to other players.</i>

### Cautions

If you are running a Folia type server, please **Avoid** Doll entering regions containing Handheld/Hanging Maps of specific Region. Server will very likely to crash due the map is tracking the Doll.

Temoprary Solution: Goto `paper-world-defaults.yml` and change the value of `item-frame-cursor-update-interval` to 0

For BungeeCord connections, check [here](https://modrinth.com/plugin/playerdoll/version/lbTw2Mzy)

For Velocity connections, a basic test was done and no specific support is needed. Doll can join directly without any setup

For Proxy-Protocol connections, is not supported, and not being able to support in short time

Things like database, multiverse might went wrong (not tested or coded to handle)

Welcome to report any issues (performance, bugs, suggestions, feedback, compatibility tests)

Doll now will connect to server by Emulating a client.
- This Should provide a better support in the future.

## Known Issues
The issues stated here are not likely to be fixed due to complicated behaviour

<h6>It is welcome to contribute for this plugin</h6>

- Doll cannot update certain vehicles while mounting
- Doll cannot be push by piston with Slime block correctly
- Doll causes Crash in Folia servers when Map is tracking Regions containing Doll (Temporary Solution above)


## How to use

### Upgrade from old version (v1.28 above)
1. Backup and delete the old configs, (except doll configs).
   * Then start up server to regenerate new configs (with comments and usages)
2. Copy settings from old config to the new one, and set for new config keys according their usage

### In game (v1.28 above)
1. player without permission will not display the corrisponding command (or arguments)
   * `doll` or `playerdoll:doll` are the main commands
   * `dollmanage` or `playerdoll:dollmanage` are the same from above, but they bypass some optional checking. OP is reqiured by default
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
