package com.github.xcykrix.dynamiclights.util;

import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.extendables.Stateful;
import com.github.xcykrix.plugincommon.extendables.implement.Shutdown;
import com.shaded._100.org.h2.mvstore.MVMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class LightManager extends Stateful implements Shutdown {
    public final LightSources lightSources;
    private final HashMap<String, Location> lastLightLocation = new HashMap<>();
    private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    // Local Database Map
    public final MVMap<String, Boolean> lightLockStatus;

    // Configuration
    private long refresh = 5L;
    private int distance = 64;

    public LightManager(PluginCommon pluginCommon, LightSources lightSources) {
        super(pluginCommon);
        this.lightLockStatus = this.pluginCommon.h2MVStoreAPI.getStore().openMap("lightLockStatus");
        this.lightSources = lightSources;

        this.refresh = this.pluginCommon.configurationAPI.get("config.yml").getLong("update-rate");
        this.distance = this.pluginCommon.configurationAPI.get("config.yml").getInt("light-culling-distance");
    }

    @Override
    public void shutdown() {
        for (UUID uuid : this.tasks.keySet()) {
            this.tasks.get(uuid).cancel();
            this.tasks.remove(uuid);
        }
    }

    public void addPlayer(Player player) {
        if (this.tasks.containsKey(player.getUniqueId())) return;
        this.tasks.put(player.getUniqueId(), this.pluginCommon.getServer().getScheduler().runTaskTimerAsynchronously(this.pluginCommon, () -> {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();

            // Check Light Source Validity
            boolean valid = this.valid(player, mainHand, offHand);
            int lightLevel = 0;
            if (valid) {
                lightLevel = lightSources.getLightLevel(offHand.getType(), mainHand.getType());
            }

            // Deploy Lighting
            for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
                // Pull Last Location
                String locationId = player.getUniqueId() + "/" + targetPlayer.getUniqueId();
                Location lastLocation = this.getLastLocation(locationId);

                // Test and Remove Old Lights
                if (!valid) {
                    if (lastLocation != null) {
                        this.removeLight(targetPlayer, lastLocation);
                        this.removeLastLocation(locationId);
                    }
                    continue;
                }

                // Get the Next Location
                Location nextLocation = player.getEyeLocation();

                // Add Light Sources
                if (lightLevel > 0 && differentLocations(lastLocation, nextLocation)) {
                    if (player.getWorld().getName().equals(targetPlayer.getWorld().getName())) {
                        if (player.getLocation().distance(targetPlayer.getLocation()) <= this.distance) {
                            this.addLight(targetPlayer, nextLocation, lightLevel);
                            this.setLastLocation(locationId, nextLocation);
                        }
                    }
                }

                // Remove Last Locations
                if (lastLocation != null && differentLocations(lastLocation, nextLocation)) {
                    this.removeLight(targetPlayer, lastLocation);
                }
            }
        }, 50L, refresh));
    }

    public void removePlayer(UUID uid) {
        synchronized (this.tasks) {
            if (this.tasks.containsKey(uid)) {
                this.tasks.get(uid).cancel();
                this.tasks.remove(uid);
            }
        }
    }

    public void addLight(Player player, Location location, int lightLevel) {
        if (lightLevel == 0) return;
        Light light = (Light) Material.LIGHT.createBlockData();
        if (location.getWorld() == null) location.setWorld(player.getWorld());
        World world = location.getWorld();
        switch (world.getBlockAt(location).getType()) {
            case AIR, CAVE_AIR -> {
                light.setWaterlogged(false);
                light.setLevel(lightLevel);
            }
            case WATER -> {
                light.setWaterlogged(true);
                light.setLevel(lightLevel - 2);
            }
        }
        player.sendBlockChange(location, light);
    }

    public void removeLight(Player player, Location location) {
        if (location.getWorld() == null) location.setWorld(player.getWorld());
        player.sendBlockChange(location, location.getWorld().getBlockAt(location).getBlockData());
    }

    public boolean valid(Player player, ItemStack mainHand, ItemStack offHand) {
        Material main = mainHand.getType();
        Material off = offHand.getType();
        boolean hasLightLevel = lightSources.hasLightLevel(off);
        if (!hasLightLevel) hasLightLevel = lightSources.hasLightLevel(main);
        if (!hasLightLevel) return false;

        Block currentLocation = player.getEyeLocation().getBlock();
        if (currentLocation.getType() == Material.AIR || currentLocation.getType() == Material.CAVE_AIR) return true;
        if (currentLocation instanceof Waterlogged && ((Waterlogged) currentLocation).isWaterlogged()) {
            return false;
        }
        if (currentLocation.getType() == Material.WATER) {
            return lightSources.isSubmersible(off, main);
        }
        return false;
    }

    public Location getLastLocation(String uuid) {
        return lastLightLocation.getOrDefault(uuid, null);
    }

    public void setLastLocation(String uuid, Location location) {
        lastLightLocation.put(uuid, location);
    }

    public void removeLastLocation(String uuid) {
        lastLightLocation.remove(uuid);
    }

    private boolean differentLocations(Location l1, Location l2) {
        if (l1 == null || l2 == null) return true;
        if (l1.getWorld() == null || l2.getWorld() == null) return true;
        if (!l1.getWorld().getName().equals(l2.getWorld().getName())) return true;
        return l1.getBlockX() != l2.getBlockX() || l1.getBlockY() != l2.getBlockY() || l1.getBlockZ() != l2.getBlockZ();
    }
}
