package com.github.xcykrix.dynamiclights.module.events;

import com.github.xcykrix.dynamiclights.util.LightManager;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.extendables.Stateful;
import com.shaded._100.net.kyori.adventure.text.Component;
import com.shaded._100.net.kyori.adventure.text.format.TextColor;
import com.shaded._100.net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerHandlerEvent extends Stateful implements Listener {
    private final LightManager lightManager;

    public PlayerHandlerEvent(PluginCommon pluginCommon, LightManager lightManager) {
        super(pluginCommon);
        this.lightManager = lightManager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void playerBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            if (!this.lightManager.lightSources.isProtectedLight(event.getItemInHand().getType())) return;
            boolean status = this.lightManager.lightLockStatus.getOrDefault(event.getPlayer().getUniqueId().toString(), true);
            if (status) {
                pluginCommon.adventureAPI.getAudiences().player(event.getPlayer()).sendMessage(Component.text("You must sneak to place light sources while lock mode is enabled. Type \"/dl lock\" to toggle.").color(TextColor.fromHexString("#AAAAAA")).decoration(TextDecoration.ITALIC, true));
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
