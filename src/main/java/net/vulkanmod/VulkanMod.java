package net.vulkanmod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("vulkanmod")
public class VulkanMod {

    public VulkanMod() {
        // Общая инициализация, если понадобится.
    }

    /** Берём версию мода из metadata; если не нашли — "dev". */
    public static String getVersion() {
        return ModList.get()
                .getModContainerById("vulkanmod")
                .map(mc -> mc.getModInfo().getVersion().toString())
                .orElse("dev");
    }

    @EventBusSubscriber(modid = "vulkanmod", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            // Клиентская инициализация переносится сюда.
            Initializer.initClient();
        }
    }
}
