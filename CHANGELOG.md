1.0.1.1
- added backwards compatibility for legacy (pre 1.7.10) savefiles

1.0.1.0
- update for MC 1.7.10
- no requires Forge 10.13.0.1177
- removed support for legacy save format and moved saving/loading to new forge events

1.0.0.16
- switch to SimpleNetworkWrapper
- now requires Forge 10.12.1.1110

1.0.0.15
- hide slot background when drawing slots with items in baubles inventory

1.0.0.14
- prevent loss of non-baubles items on death

1.0.0.13
- properly clear player baubles inventory before he enters the world and reset file pointers
- only attempt to load or save baubles server-side

1.0.0.12
- fix for baubles being synced to the wrong player on the client

1.0.0.11
- baubles are now synced to all clients on a server, allowing modders to add visual components or other client-side stuff for baubles.

1.0.0.10
- moved baubles data from player data to custom savefiles (*.baub)
- a backup file is also saved each time in case the main one becomes corrupt.
- automatically revert to backup file if main one cannot be loaded. If both cannot be loaded revert to old player data format. This will also insure that data from previous baubles versions will be loaded the first time.

1.0.0.9
- undoing the "baubles hotkey closes all inventories" change. It was messing with anything that allows text input

1.0.0.8
- sync baubles itemstack data between client and server if changed outside the baubles GUI

1.0.0.7
- the baubles hotkey can now be used to close any gui the player has open
- check for "keepInventory" game rule to prevent baubles from dropping on death

1.0.0.6
- additional checks to prevent the wrong items getting into the wrong slots
- minor changes to te example ring so it doesn't get used when you right-click to equip in creative mode

1.0.0.5
- API: added a few new methods. I know there is nothing more annoying than constantly changing api's, but hopefully these kinds of changes will be few and far between.

1.0.0.4
- allow other mods to force a save by marking the inventory as dirty

1.0.0.3
- example ring can now be rightclicked like armor to equip it

1.0.0.2
- baubles inventory now opens when it's hotkey is pressed in creative 

1.0.0.1
- properly trigger unequip event when shift clicking out of bauble slot