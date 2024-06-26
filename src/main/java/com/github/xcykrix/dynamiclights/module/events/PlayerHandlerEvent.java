package com.github.xcykrix.dynamiclights.module.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.github.xcykrix.dynamiclights.util.LightManager;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.api.helper.configuration.LanguageFile;
import com.github.xcykrix.plugincommon.extendables.Stateful;

public class PlayerHandlerEvent extends Stateful implements Listener {
    private final LightManager lightManager;
    private final LanguageFile languageFile;
    private final boolean defaultState;

    public PlayerHandlerEvent(PluginCommon pluginCommon, LightManager lightManager) {
        super(pluginCommon);
        this.lightManager = lightManager;
        this.languageFile = this.pluginCommon.configurationAPI.getLanguageFile();
        this.defaultState = this.pluginCommon.configurationAPI.get("config.yml")
                .getOptionalBoolean("default-lock-state").orElse(false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void playerBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            if (!this.lightManager.lightSources.isProtectedLight(event.getItemInHand().getType()))
                return;
            if (this.lightManager.lightLockStatus.getOrDefault(event.getPlayer().getUniqueId().toString(),
                    defaultState)) {
                pluginCommon.adventureAPI.getAudiences().player(event.getPlayer()).sendMessage(
                        this.languageFile.getComponentFromID("prevent-block-place", true));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        this.lightManager.addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        this.lightManager.removePlayer(event.getPlayer().getUniqueId());
    }
}
