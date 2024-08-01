package com.github.xcykrix.dynamiclights.module;

import com.github.xcykrix.dynamiclights.module.events.PlayerHandlerEvent;
import com.github.xcykrix.dynamiclights.util.LightManager;
import com.github.xcykrix.dynamiclights.util.LightSources;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.api.helper.configuration.LanguageFile;
import com.github.xcykrix.plugincommon.extendables.implement.Initialize;
import com.shaded._100.aikar.commands.BaseCommand;
import com.shaded._100.aikar.commands.CommandHelp;
import com.shaded._100.aikar.commands.annotation.CommandAlias;
import com.shaded._100.aikar.commands.annotation.CommandPermission;
import com.shaded._100.aikar.commands.annotation.Description;
import com.shaded._100.aikar.commands.annotation.HelpCommand;
import com.shaded._100.aikar.commands.annotation.Subcommand;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("dynamiclights|dynamiclight|dl")
public class DynamicLightCommand extends BaseCommand implements Initialize {

    private final PluginCommon pluginCommon;
    private final LightManager lightManager;
    private final LanguageFile languageFile;

    public DynamicLightCommand(PluginCommon pluginCommon, LightManager lightManager) {
        this.pluginCommon = pluginCommon;
        this.lightManager = lightManager;
        this.languageFile = this.pluginCommon.configurationAPI.getLanguageFile();
    }

    @Override
    public void initialize() {
        pluginCommon.getServer().getPluginManager()
            .registerEvents(new PlayerHandlerEvent(pluginCommon, this.lightManager), pluginCommon);
    }

    @Subcommand("toggle")
    @CommandPermission("dynamiclights.toggle")
    @Description("Toggle rendering light sources for your client.")
    public void toggle(Player player) {
        boolean status = this.lightManager.lightToggleStatus.getOrDefault(
            player.getUniqueId().toString(),
            this.lightManager.toggle);
        if (!status) {
            this.pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("toggle-on", true));
            this.lightManager.lightToggleStatus.put(player.getUniqueId().toString(), true);
        } else {
            this.pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("toggle-off", true));
            this.lightManager.lightToggleStatus.put(player.getUniqueId().toString(), false);
        }
    }

    @Subcommand("lock")
    @CommandPermission("dynamiclights.lock")
    @Description("Toggle placing light sources from your Off Hand.")
    public void lock(Player player) {
        boolean status = this.lightManager.lightLockStatus.getOrDefault(
            player.getUniqueId().toString(), true);
        if (!status) {
            this.pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("enable-lock", true));
            this.lightManager.lightLockStatus.put(player.getUniqueId().toString(), true);
        } else {
            this.pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("disable-lock", true));
            this.lightManager.lightLockStatus.put(player.getUniqueId().toString(), false);
        }
    }

    @Subcommand("reload")
    @CommandPermission("dynamiclights.reload")
    @Description("Reload the light level configuration file. Changes to config.yml require a Server Reboot.")
    public void reload(Player player) {
        try {
            this.pluginCommon.configurationAPI.get("lights.yml").reload();
            this.lightManager.updateLightSources(new LightSources(this.pluginCommon));
            this.pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("reload", true));
        } catch (IOException e) {
            this.pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("reload-error", true));
            this.pluginCommon.getLogger().severe("Failed to reload lights.yml configuration.");
            this.pluginCommon.getLogger().severe(ExceptionUtils.getStackTrace(e));
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp helpCommand) {
        helpCommand.showHelp();
    }
}
