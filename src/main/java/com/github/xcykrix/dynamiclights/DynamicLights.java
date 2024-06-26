package com.github.xcykrix.dynamiclights;

import org.bukkit.entity.Player;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.github.xcykrix.dynamiclights.module.DynamicLightCommand;
import com.github.xcykrix.dynamiclights.util.LightManager;
import com.github.xcykrix.dynamiclights.util.LightSources;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.api.records.Resource;

public final class DynamicLights extends PluginCommon {
    private LightManager lightManager;

    @Override
    public void initialize() {
        // Set Earliest Supported Version
        this.setEarliestSupportedVersion(MinecraftVersion.CAVES_CLIFFS_2);

        // Register Configurations
        this.configurationAPI
                .register(new Resource("config.yml", null, this.getResource("config.yml")))
                .register(new Resource("lights.yml", null, this.getResource("lights.yml")))
                .registerLanguageFile(this.getResource("language.yml"));

        // Initialize Light Manager
        this.lightManager = new LightManager(this, new LightSources(this));

        // Register Commands
        this.commandAPI.register(new DynamicLightCommand(this, lightManager));

        // Register Current Players for Tracking.
        for (Player player : this.getServer().getOnlinePlayers()) {
            lightManager.addPlayer(player);
        }
    }

    @Override
    public void shutdown() {
        if (this.lightManager != null) {
            this.lightManager.shutdown();
        }
    }
}
