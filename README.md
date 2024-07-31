# PlayerDoll
Simple Standalone Fake Player Plugin for Spigot, Paper, Folia 1.20.2+ [Java 17+]

[Modrinth](https://modrinth.com/plugin/playerdoll)

Release avaliable on Modrinth.



Detailed Usage has been migrated to [Wiki](https://github.com/sjavi4/PlayerDoll/wiki)


## Disclaimer

<b>This plugin is highly dependent on NMS, slightly changes made by Mojang / Server Jar provider might cause this plugin no longer working.<br>
Please use it with cautious</b>


<i>Features provided by this plugin are not GUARANTEE to be fully working, it is not encouraged to treat this plugin as a pay feature to other players.</i>


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
