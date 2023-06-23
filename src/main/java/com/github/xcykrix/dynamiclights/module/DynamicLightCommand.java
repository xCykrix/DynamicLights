package com.github.xcykrix.dynamiclights.module;


import com.github.xcykrix.dynamiclights.module.events.PlayerHandlerEvent;
import com.github.xcykrix.dynamiclights.util.LightManager;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.extendables.LateLoad;
import com.shaded._100.aikar.commands.BaseCommand;
import com.shaded._100.aikar.commands.annotation.CommandAlias;
import com.shaded._100.aikar.commands.annotation.CommandPermission;
import com.shaded._100.aikar.commands.annotation.Description;
import com.shaded._100.aikar.commands.annotation.Subcommand;
import com.shaded._100.net.kyori.adventure.text.Component;
import com.shaded._100.net.kyori.adventure.text.format.TextColor;
import com.shaded._100.net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("dynamiclights|dynamiclight|dl")
public class DynamicLightCommand extends BaseCommand implements LateLoad {
    private final PluginCommon pluginCommon;
    private final LightManager lightManager;

    public DynamicLightCommand(PluginCommon pluginCommon, LightManager lightManager) {
        this.pluginCommon = pluginCommon;
        this.lightManager = lightManager;
    }

    /**
     * LazyLoadable. Called during onEnable() instead of initialize().
     */
    @Override
    public void lateLoad() {
        // Register PlayerHandler Events
        pluginCommon.getServer().getPluginManager().registerEvents(new PlayerHandlerEvent(pluginCommon, this.lightManager), pluginCommon);
    }

    @Subcommand("lock")
    @CommandPermission("dynamiclights.lock")
    @Description("Prevent placing light sources from your off hand.")
    public void lock(Player player) {
        boolean status = this.lightManager.lightLockStatus.getOrDefault(player.getUniqueId().toString(), true);
        if (!status) {
            pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(Component.text("Enabled DynamicLights Lock.").color(TextColor.fromHexString("#AAAAAA")).decoration(TextDecoration.ITALIC, true));
            this.lightManager.lightLockStatus.put(player.getUniqueId().toString(), true);
        } else {
            pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(Component.text("Disabled DynamicLights Lock.").color(TextColor.fromHexString("#AAAAAA")).decoration(TextDecoration.ITALIC, true));
            this.lightManager.lightLockStatus.put(player.getUniqueId().toString(), false);
        }
    }

    @Subcommand("reload")
    @CommandPermission("dynamiclights.reload")
    @Description("Reload the plugin configuration where possible.")
    public void reload(CommandSender sender) {
        pluginCommon.adventureAPI.getAudiences().sender(sender).sendMessage(Component.text("Reloaded DynamicLights.").color(TextColor.fromHexString("#AAAAAA")).decoration(TextDecoration.ITALIC, true));
        this.lightManager.reload();
    }
}
