package github.xCykrix.dynamicLights.util;

import dist.xCykrix.shade.dev.jorel.commandapi.CommandAPIBukkit;
import dist.xCykrix.shade.org.h2.mvstore.MVStore;
import github.xCykrix.DevkitPlugin;
import github.xCykrix.dynamicLights.DynamicLights;
import github.xCykrix.extendable.DevkitFullState;
import dist.xCykrix.shade.dev.dejvokep.boostedyaml.YamlDocument;
import dist.xCykrix.shade.org.h2.mvstore.MVMap;
import java.util.HashMap;
import java.util.UUID;
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

public class LightManager extends DevkitFullState {
  private final LightSource source;
  private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();
  private final HashMap<String, Location> lastLightLocation = new HashMap<>();

  public final MVMap<String, Boolean> toggles;
  public final MVMap<String, Boolean> locks;
  private final long refresh;
  private final int distance;
  public final boolean toggle;

  public LightManager(DevkitPlugin plugin) {
    super(plugin);
    YamlDocument config = DynamicLights.configuration.getYAMLFile("config.yml");
    if (config == null) {
      throw new RuntimeException("config.yml is corrupted or contains invalid formatting. Failed to load plugin.");
    }

    this.source = DynamicLights.source;
    this.toggles = DynamicLights.h2.get().openMap("lightToggleStatus");
    this.locks = DynamicLights.h2.get().openMap("lightLockStatus");
    this.refresh = config.getLong("update-rate");
    this.distance = config.getInt("light-culling-distance");
    this.toggle = config.getBoolean("default-toggle-state");
  }

  @Override
  public void initialize() {
  }

  @Override
  public void shutdown() {
    synchronized (this.tasks) {
      for (UUID uuid : this.tasks.keySet()) {
        this.tasks.get(uuid).cancel();
      }
      this.tasks.clear();
    }
  }

  public void addPlayer(Player player) {
    synchronized (this.tasks) {
      if (this.tasks.containsKey(player.getUniqueId())) {
        return;
      }
      this.tasks.put(player.getUniqueId(), this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        boolean valid = this.valid(player, mainHand, offHand);
        int lightLevel = 0;
        if (valid) {
          lightLevel = source.getLightLevel(offHand.getType(), mainHand.getType());
        }
        for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
          String locationId = player.getUniqueId() + "/" + targetPlayer.getUniqueId();
          Location lastLocation = this.getLastLocation(locationId);
          if (!valid) {
            if (lastLocation != null) {
              this.removeLight(targetPlayer, lastLocation);
              this.removeLastLocation(locationId);
            }
            continue;
          }
          Location nextLocation = player.getEyeLocation();
          if (this.toggles.getOrDefault(targetPlayer.getUniqueId().toString(), this.toggle)) {
            if (lightLevel > 0 && differentLocations(lastLocation, nextLocation)) {
              if (player.getWorld().getName().equals(targetPlayer.getWorld().getName())) {
                if (player.getLocation().distance(targetPlayer.getLocation()) <= this.distance) {
                  this.addLight(targetPlayer, nextLocation, lightLevel);
                  this.setLastLocation(locationId, nextLocation);
                }
              }
            }
          }
          if (lastLocation != null && differentLocations(lastLocation, nextLocation)) {
            this.removeLight(targetPlayer, lastLocation);
          }
        }
      }, 50L, refresh));
    }
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
    if (lightLevel == 0) {
      return;
    }
    Light light = (Light) Material.LIGHT.createBlockData();
    if (location.getWorld() == null) {
      location.setWorld(player.getWorld());
    }
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
      default -> {
      }
    }
    player.sendBlockChange(location, light);
  }

  public void removeLight(Player player, Location location) {
    if (location.getWorld() == null) {
      location.setWorld(player.getWorld());
    }
    player.sendBlockChange(location, location.getWorld().getBlockAt(location).getBlockData());
  }

  public boolean valid(Player player, ItemStack mainHand, ItemStack offHand) {
    Material main = mainHand.getType();
    Material off = offHand.getType();
    boolean hasLightLevel = source.hasLightLevel(off);
    if (!hasLightLevel) {
      hasLightLevel = source.hasLightLevel(main);
    }
    if (!hasLightLevel) {
      return false;
    }
    Block currentLocation = player.getEyeLocation().getBlock();
    if (currentLocation.getType() == Material.AIR || currentLocation.getType() == Material.CAVE_AIR) {
      return true;
    }
    if (currentLocation instanceof Waterlogged && ((Waterlogged) currentLocation).isWaterlogged()) {
      return false;
    }
    if (currentLocation.getType() == Material.WATER) {
      return source.isSubmersible(off, main);
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
    if (l1 == null || l2 == null) {
      return true;
    }
    if (l1.getWorld() == null || l2.getWorld() == null) {
      return true;
    }
    if (!l1.getWorld().getName().equals(l2.getWorld().getName())) {
      return true;
    }
    return l1.getBlockX() != l2.getBlockX() || l1.getBlockY() != l2.getBlockY() || l1.getBlockZ() != l2.getBlockZ();
  }
}
