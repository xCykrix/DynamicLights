package github.xCykrix.dynamicLights;

import github.xCykrix.DevkitPlugin;
import github.xCykrix.plugin.AdventurePlugin;
import github.xCykrix.plugin.ConfigurationPlugin;
import github.xCykrix.plugin.H2MVStorePlugin;
import github.xCykrix.plugin.ProtocolLibPlugin;
import github.xCykrix.records.Resource;

@SuppressWarnings({"unused", "Called by Spigot API."})
public final class DynamicLights extends DevkitPlugin {
  public static ConfigurationPlugin configuration;
  public static AdventurePlugin adventure;
  public static ProtocolLibPlugin protocol;
  public static H2MVStorePlugin h2;

  @Override
  protected void pre() {
    configuration = this.register(new ConfigurationPlugin(this));
    adventure = this.register(new AdventurePlugin(this));
    protocol = this.register(new ProtocolLibPlugin(this));
    h2 = this.register(new H2MVStorePlugin(this));
  }

  @Override
  public void initialize() {
    configuration
        .register(new Resource("config.yml", null, this.getResource("config.yml")))
        .register(new Resource("lights.yml", null, this.getResource("lights.yml")))
        .registerLanguageFile(this.getResource("language.yml"));
  }

  @Override
  public void shutdown() {

  }
}
