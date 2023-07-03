
# DynamicLights

Emit Light from Held Items using the 1.17 Light Blocks in Spigot/Paper Servers.


## Usage/Examples

Commands
- /dynamiclights lock (dynamiclights.lock)
  - Prevents placing Lanterns and Torches from the offhand. Useful for held lights while exploring or eating.


## FAQ

#### How does this plugin work?

DynamicLights works by creating a thread for each player and rendering a [Light Block](https://minecraft.fandom.com/wiki/Light_Block) at their head location for all online players asyncrounously.

At the player's head location, DynamicLights will send a block change packet to all players within the culling range. DynamicLights does NOT modify the world.

#### Can I change which items glow and work underwater?

Yes! In the "lights.yml" file you can add any item from https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html exactly as it appears to set custom light levels. You can also make them work underwater!

#### Lights flickers in Blocks like Tall Grass, Sea Grass, and Kelp. Why?

DynamicLights utilizes the [Light Block](https://minecraft.fandom.com/wiki/Light_Block) to render the light in the world for all players. As this is an actual block, it requires there to be open air or open water to be used and cannot be placed on transparent blocks or blocks without collision.


## Deployment

This plugin is based on the PluginCommon API. Updates to PluginCommon will provide verioned and shaded assets associated with this plugin.

1. Update POM XML Plugins and Dependencies.
2. Update POM XML Version.
3. Build Plugin
4. Verify in 1.17 to 1.20.1
5. Update GitHub Source Code
6. Tag Version to Last Commit
7. Add Release
8. Update Spigot
