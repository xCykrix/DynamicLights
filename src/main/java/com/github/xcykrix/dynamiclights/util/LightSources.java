package com.github.xcykrix.dynamiclights.util;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.extendables.Stateful;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;

public class LightSources extends Stateful {
    private final HashMap<Material, Integer> levelOfLights = new HashMap<>();
    private final HashSet<Material> submersibleLights = new HashSet<>();
    private final HashSet<Material> protectedLights = new HashSet<>();

    public LightSources(PluginCommon pluginCommon) {
        super(pluginCommon);

        // Add 1.19 Lights
        if (this.pluginCommon.protocolLibAPI.getProtocolManager().getMinecraftVersion().isAtLeast(MinecraftVersion.WILD_UPDATE)) {
            // Light Sources
            levelOfLights.put(Material.OCHRE_FROGLIGHT, 15);
            levelOfLights.put(Material.PEARLESCENT_FROGLIGHT, 15);
            levelOfLights.put(Material.VERDANT_FROGLIGHT, 15);

            // Underwater Allowed
            submersibleLights.add(Material.OCHRE_FROGLIGHT);
            submersibleLights.add(Material.PEARLESCENT_FROGLIGHT);
            submersibleLights.add(Material.VERDANT_FROGLIGHT);
        }

        // Intense Light Sources
        levelOfLights.put(Material.BEACON, 15);

        levelOfLights.put(Material.GLOWSTONE, 15);
        levelOfLights.put(Material.JACK_O_LANTERN, 15);
        levelOfLights.put(Material.LAVA_BUCKET, 15);
        levelOfLights.put(Material.SEA_LANTERN, 15);
        levelOfLights.put(Material.SHROOMLIGHT, 15);

        // Lanterns
        levelOfLights.put(Material.LANTERN, 13);
        levelOfLights.put(Material.SOUL_LANTERN, 13);

        // Torches
        levelOfLights.put(Material.TORCH, 11);
        levelOfLights.put(Material.SOUL_TORCH, 11);
        levelOfLights.put(Material.REDSTONE_TORCH, 7);

        // Underwater Allowed
        submersibleLights.add(Material.BEACON);
        submersibleLights.add(Material.GLOWSTONE);
        submersibleLights.add(Material.SEA_LANTERN);
        submersibleLights.add(Material.SHROOMLIGHT);

        // Protected Lights
        protectedLights.add(Material.LANTERN);
        protectedLights.add(Material.SOUL_LANTERN);
        protectedLights.add(Material.TORCH);
        protectedLights.add(Material.SOUL_TORCH);

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
        return protectedLights.contains(offHand);
    }
}
