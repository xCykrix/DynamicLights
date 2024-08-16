package github.xCykrix.dynamicLights.util;

import github.xCykrix.DevkitPlugin;
import github.xCykrix.dynamicLights.DynamicLights;
import github.xCykrix.extendable.DevkitFullState;
import dist.xCykrix.shade.dev.dejvokep.boostedyaml.YamlDocument;
import dist.xCykrix.shade.dev.dejvokep.boostedyaml.block.Block;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

public class LightSource extends DevkitFullState {
  private final HashMap<Material, Integer> levelOfLights = new HashMap<>();
  private final HashSet<Material> submersibleLights = new HashSet<>();
  private final HashSet<Material> lockedLights = new HashSet<>();

  public LightSource(DevkitPlugin plugin) {
    super(plugin);
  }

  @Override
  public void initialize() {
    YamlDocument lights = DynamicLights.configuration.getYAMLFile("lights.yml");
    if (lights == null) {
      throw new RuntimeException("lights.yml is corrupted or contains invalid formatting. Failed to load plugin.");
    }

    // Register Light Levels
    this.levelOfLights.clear();
    Map<Object, Block<?>> levels = lights.getSection("levels").getStoredValue();
    for (Object material : levels.keySet()) {
      try {
        int level = Integer.parseInt(levels.get(material).getStoredValue().toString());
        this.levelOfLights.put(Material.valueOf((String) material), level);
      } catch (Exception exception) {
        this.plugin.getLogger().warning("Unable to register level for '" + material + "'. " + exception.getMessage());
      }
    }
    this.plugin.getLogger().info("Registered " + this.levelOfLights.size() + " items for Dynamic Lights.");

    // Register Submersible Lights
    this.submersibleLights.clear();
    List<String> submersibles = lights.getStringList("submersibles");
    for (String material : submersibles) {
      try {
        this.submersibleLights.add(Material.valueOf(material));
      } catch (Exception exception) {
        this.plugin.getLogger().warning("Unable to register submersible for '" + material + "'. " + exception.getMessage());
      }
    }
    this.plugin.getLogger().info("Registered " + this.submersibleLights.size() + " items for Dynamic Submersible Lights.");

    // Register Lockable Lights
    this.lockedLights.clear();
    List<String> lockables = lights.getStringList("lockables");
    for (String material : lockables) {
      try {
        this.lockedLights.add(Material.valueOf(material));
      } catch (Exception exception) {
        this.plugin.getLogger().warning("Unable to register lockable for '" + material + "'. " + exception.getMessage());
      }
    }
    this.plugin.getLogger().info("Registered " + this.lockedLights.size() + " items for Dynamic Locked Lights.");
  }

  @Override
  public void shutdown() {
    this.lockedLights.clear();
    this.submersibleLights.clear();
    this.levelOfLights.clear();
  }

  public boolean hasLightLevel(Material material) {
    return levelOfLights.containsKey(material);
  }

  public Integer getLightLevel(Material material, Material fallback) {
    return levelOfLights.getOrDefault(material, levelOfLights.getOrDefault(fallback, 0));
  }

  public boolean isSubmersible(Material offHand, Material mainHand) {
    return submersibleLights.contains(offHand) || submersibleLights.contains(mainHand);
  }

  public boolean isProtectedLight(Material offHand) {
    return lockedLights.contains(offHand);
  }
}