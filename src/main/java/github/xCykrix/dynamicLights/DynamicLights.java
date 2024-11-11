package github.xCykrix.dynamicLights;

import github.xCykrix.DevkitPlugin;
import github.xCykrix.dynamicLights.command.BaseCommand;
import github.xCykrix.dynamicLights.event.PlayerHandler;
import github.xCykrix.dynamicLights.util.LightManager;
import github.xCykrix.dynamicLights.util.LightSource;
import github.xCykrix.helper.LanguageFile;
import github.xCykrix.plugin.AdventurePlugin;
import github.xCykrix.plugin.CommandPlugin;
import github.xCykrix.plugin.ConfigurationPlugin;
import github.xCykrix.plugin.H2MVStorePlugin;
import github.xCykrix.plugin.ProtocolLibPlugin;
import github.xCykrix.records.Resource;

public final class DynamicLights extends DevkitPlugin {
  // Core APIs.
  public static ConfigurationPlugin configuration;
  public static AdventurePlugin adventure;
  public static CommandPlugin command;

  // Third Party APIs.
  public static ProtocolLibPlugin protocol;
  public static H2MVStorePlugin h2;

  // Internal APIs.
  public static LanguageFile language;
  public static LightSource source;
  public static LightManager manager;

  @Override
  protected void pre() {
    configuration = this.register(new ConfigurationPlugin(this));
    adventure = this.register(new AdventurePlugin(this));
    command = this.register(new CommandPlugin(this));

    protocol = this.register(new ProtocolLibPlugin(this));
    h2 = this.register(new H2MVStorePlugin(this));
  }

  @Override
  public void initialize() {
    // Register Configurations
    configuration
        .register(new Resource("config.yml", null, this.getResource("config.yml")))
        .register(new Resource("lights.yml", null, this.getResource("lights.yml")))
        .registerLanguageFile(this.getResource("language.yml"));
    language = configuration.getLanguageFile();

    // Register Internal APIs.
    source = new LightSource(this);
    source.initialize();
    manager = new LightManager(this);
    manager.initialize();

    // Register Events
    this.getServer().getPluginManager().registerEvents(new PlayerHandler(this), this);

    // Register Commands
    BaseCommand.getOrCreate(this).generate(command);
  }

  @Override
  public void shutdown() {
    manager.shutdown();
    source.shutdown();
  }
}
