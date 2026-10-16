package eu.withoutaname.mods.draconicautomation;

import com.mojang.logging.LogUtils;
import eu.withoutaname.mods.draconicautomation.compat.de.DraconicIntegration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(DraconicAutomation.MODID)
public class DraconicAutomation {
  public static final String MODID = "draconicautomation";
  public static final Logger LOGGER = LogUtils.getLogger();

  public DraconicAutomation(IEventBus modBus, ModContainer modContainer) {
    DraconicIntegration.init(modBus);
  }
}
