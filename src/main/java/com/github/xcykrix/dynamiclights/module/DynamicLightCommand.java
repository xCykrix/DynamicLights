package com.github.xcykrix.dynamiclights.module;


import com.github.xcykrix.dynamiclights.module.events.PlayerHandlerEvent;
import com.github.xcykrix.dynamiclights.util.LightManager;
import com.github.xcykrix.plugincommon.PluginCommon;
import com.github.xcykrix.plugincommon.api.helper.configuration.LanguageFile;
import com.github.xcykrix.plugincommon.extendables.implement.Initialize;
import com.github.xcykrix.plugincommon.extendables.implement.LateLoad;
import com.shaded._100.aikar.commands.BaseCommand;
import com.shaded._100.aikar.commands.CommandHelp;
import com.shaded._100.aikar.commands.annotation.*;
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
        pluginCommon.getServer().getPluginManager().registerEvents(new PlayerHandlerEvent(pluginCommon, this.lightManager), pluginCommon);
    }

    @Subcommand("lock")
    @CommandPermission("dynamiclights.lock")
    @Description("Toggle placing light sources from your Off Hand.")
    public void lock(Player player) {
        boolean status = this.lightManager.lightLockStatus.getOrDefault(player.getUniqueId().toString(), true);
        if (!status) {
            pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("enable-lock", true)
            );
            this.lightManager.lightLockStatus.put(player.getUniqueId().toString(), true);
        } else {
            pluginCommon.adventureAPI.getAudiences().player(player).sendMessage(
                this.languageFile.getComponentFromID("disable-lock", true)
            );
            this.lightManager.lightLockStatus.put(player.getUniqueId().toString(), false);
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp helpCommand) {
        helpCommand.showHelp();
    }
}
