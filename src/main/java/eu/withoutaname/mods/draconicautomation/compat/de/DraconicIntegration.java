package eu.withoutaname.mods.draconicautomation.compat.de;


import com.brandon3055.draconicevolution.init.DEContent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class DraconicIntegration {
  private DraconicIntegration() {}

  public static void init(IEventBus modBus) {
    modBus.addListener(DraconicIntegration::registerCapabilities);
  }

  private static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
        DEContent.TILE_REACTOR_STABILIZER.get(),
        (be, side) -> new ReactorItemHandler(be::tryGetCore));
  }
}
