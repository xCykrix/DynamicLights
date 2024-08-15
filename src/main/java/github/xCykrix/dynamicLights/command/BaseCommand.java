package github.xCykrix.dynamicLights.command;

import github.xCykrix.DevkitPlugin;
import github.xCykrix.dynamicLights.DynamicLights;
import github.xCykrix.extendable.DevkitSimpleState;
import github.xCykrix.plugin.CommandPlugin;
import github.xCykrix.shade.dev.jorel.commandapi.CommandAPICommand;
import github.xCykrix.shade.org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.IOException;
import java.util.Objects;

public class BaseCommand extends DevkitSimpleState {

  public static BaseCommand core;

  public static BaseCommand getOrCreate(DevkitPlugin plugin) {
    if (core == null) {
      core = new BaseCommand(plugin);
    }
    return core;
  }

  public BaseCommand(DevkitPlugin plugin) {
    super(plugin);
  }

  public void generate(CommandPlugin commandPlugin) {
    new CommandAPICommand("dynamiclights")
        .withAliases("dynamiclight", "dl")
        .withSubcommand(
            new CommandAPICommand("toggle")
                .withPermission("dynamiclights.toggle")
                .executesPlayer(info -> {
                  String uuid = info.sender().getUniqueId().toString();
                  boolean current = DynamicLights.manager.toggles.getOrDefault(uuid, DynamicLights.manager.toggle);
                  if (!current) {
                    DynamicLights.adventure.get().player(info.sender()).sendMessage(
                        DynamicLights.language.getComponentFromID("toggle-on", true)
                    );
                    DynamicLights.manager.toggles.put(uuid, true);
                  } else {
                    DynamicLights.adventure.get().player(info.sender()).sendMessage(
                        DynamicLights.language.getComponentFromID("toggle-off", true)
                    );
                    DynamicLights.manager.toggles.put(uuid, false);
                  }
                })
        )
        .withSubcommand(
            new CommandAPICommand("lock")
                .withPermission("dynamiclights.lock")
                .executesPlayer(info -> {
                  String uuid = info.sender().getUniqueId().toString();
                  boolean current = DynamicLights.manager.locks.getOrDefault(uuid, true);
                  if (!current) {
                    DynamicLights.adventure.get().player(info.sender()).sendMessage(
                        DynamicLights.language.getComponentFromID("enable-lock", true)
                    );
                    DynamicLights.manager.locks.put(uuid, true);
                  } else {
                    DynamicLights.adventure.get().player(info.sender()).sendMessage(
                        DynamicLights.language.getComponentFromID("disable-lock", true)
                    );
                    DynamicLights.manager.locks.put(uuid, false);
                  }
                })
        )
        .withSubcommand(
            new CommandAPICommand("reload")
                .withPermission("dynamiclights.reload")
                .executes(info -> {
                      try {
                        Objects.requireNonNull(DynamicLights.configuration.getYAMLFile("lights.yml")).reload();
                        DynamicLights.source.initialize();
                        DynamicLights.adventure.get().sender(info.sender()).sendMessage(
                            DynamicLights.language.getComponentFromID("reload", true)
                        );
                      } catch (IOException | NullPointerException ex) {
                        DynamicLights.adventure.get().sender(info.sender()).sendMessage(
                            DynamicLights.language.getComponentFromID("reload-error", true)
                        );
                        this.plugin.getLogger().severe("Failed to reload lights.yml.");
                        this.plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
                      }
                })
        ).register(this.plugin);
  }
}


//    commandPlugin.register(
//        commandPlugin.create("dynamiclights")
//            .withAliases("dynamiclight", "dl")
//            .withSubcommand(
//                commandPlugin.create("toggle")
//                    .withPermission("dynamiclights.toggle")
//                    .executesPlayer(info -> {
//                      String uuid = info.sender().getUniqueId().toString();
//                      boolean current = DynamicLights.manager.toggles.getOrDefault(uuid, DynamicLights.manager.toggle);
//                      if (!current) {
//                        DynamicLights.adventure.get().player(info.sender()).sendMessage(
//                            DynamicLights.language.getComponentFromID("toggle-on", true)
//                        );
//                        DynamicLights.manager.toggles.put(uuid, true);
//                      } else {
//                        DynamicLights.adventure.get().player(info.sender()).sendMessage(
//                            DynamicLights.language.getComponentFromID("toggle-off", true)
//                        );
//                        DynamicLights.manager.toggles.put(uuid, false);
//                      }
//                    })
//            )
//            .withSubcommand(
//                commandPlugin.create("lock")
//                    .withPermission("dynamiclights.lock")
//                    .executesPlayer(info -> {
//                      String uuid = info.sender().getUniqueId().toString();
//                      boolean current = DynamicLights.manager.locks.getOrDefault(uuid, true);
//                      if (!current) {
//                        DynamicLights.adventure.get().player(info.sender()).sendMessage(
//                            DynamicLights.language.getComponentFromID("enable-lock", true)
//                        );
//                        DynamicLights.manager.locks.put(uuid, true);
//                      } else {
//                        DynamicLights.adventure.get().player(info.sender()).sendMessage(
//                            DynamicLights.language.getComponentFromID("disable-lock", true)
//                        );
//                        DynamicLights.manager.locks.put(uuid, false);
//                      }
//                    })
//            )
//            .withSubcommand(
//                commandPlugin.create("update_lights")
//                    .withPermission("dynamiclights.reload")
//                    .executesPlayer(info -> {
//                      try {
//                        Objects.requireNonNull(DynamicLights.configuration.getYAMLFile("lights.yml")).reload();
//                        DynamicLights.source.initialize();
//                      } catch (IOException | NullPointerException ex) {
//                        DynamicLights.adventure.get().sender(info.sender()).sendMessage(
//                            DynamicLights.language.getComponentFromID("reload-error", true)
//                        );
//                        this.plugin.getLogger().severe("Failed to reload lights.yml.");
//                        this.plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
//                      }
//                    })
//            )
//    );