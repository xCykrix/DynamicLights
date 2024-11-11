package github.xCykrix.dynamicLights.event;

import github.xCykrix.DevkitPlugin;
import github.xCykrix.dynamicLights.DynamicLights;
import github.xCykrix.extendable.DevkitSimpleState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerHandler extends DevkitSimpleState implements Listener {
  public PlayerHandler(DevkitPlugin plugin) {
    super(plugin);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void playerBlockPlaceEvent(BlockPlaceEvent event) {
    if (event.getPlayer().isSneaking()) {
      return;
    }

    if (event.getHand() == EquipmentSlot.OFF_HAND) {
      if (!DynamicLights.source.isProtectedLight(event.getItemInHand().getType())) {
        return;
      }
      if (DynamicLights.manager.locks.getOrDefault(event.getPlayer().getUniqueId().toString(), DynamicLights.manager.toggle)) {
        DynamicLights.adventure.get().player(event.getPlayer()).sendMessage(DynamicLights.language.getComponentFromID("prevent-block-place", true));
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    DynamicLights.manager.addPlayer(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuitEvent(PlayerQuitEvent event) {
    DynamicLights.manager.removePlayer(event.getPlayer().getUniqueId());
  }
}
