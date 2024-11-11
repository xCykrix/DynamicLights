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
        this.plugin.getLogger()
            .warning("Unable to register submersible for '" + material + "'. " + exception.getMessage());
      }
    }
    this.plugin.getLogger()
        .info("Registered " + this.submersibleLights.size() + " items for Dynamic Submersible Lights.");

    // Register Lockable Lights
    this.lockedLights.clear();
    List<String> lockables = lights.getStringList("lockables");
    for (String material : lockables) {
      try {
        this.lockedLights.add(Material.valueOf(material));
      } catch (Exception exception) {
        this.plugin.getLogger()
            .warning("Unable to register lockable for '" + material + "'. " + exception.getMessage());
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

  public Integer getLightLevel(Material mainHand, Material offHand, Material helmet, Material chestplate,
      Material legging, Material boot) {
    int level = 0;
    level = levelOfLights.getOrDefault(boot, level);
    level = levelOfLights.getOrDefault(legging, level);
    level = levelOfLights.getOrDefault(chestplate, level);
    level = levelOfLights.getOrDefault(helmet, level);
    level = levelOfLights.getOrDefault(offHand, level);
    level = levelOfLights.getOrDefault(mainHand, level);
    return level;
  }

  public boolean isSubmersible(Material mainHand, Material offHand, Material helmet, Material chestplate,
      Material legging, Material boot) {
    boolean submersible = false;
    submersible = submersibleLights.contains(boot) ? true : submersible;
    submersible = submersibleLights.contains(legging) ? true : submersible;
    submersible = submersibleLights.contains(chestplate) ? true : submersible;
    submersible = submersibleLights.contains(helmet) ? true : submersible;
    submersible = submersibleLights.contains(offHand) ? true : submersible;
    submersible = submersibleLights.contains(mainHand) ? true : submersible;
    return submersible;
  }

  public boolean isProtectedLight(Material offHand) {
    return lockedLights.contains(offHand);
  }
}
