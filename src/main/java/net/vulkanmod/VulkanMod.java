package net.vulkanmod;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.vulkanmod.config.Config;
import org.slf4j.Logger;

@Mod("vulkanmod")
public class VulkanMod {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Config CONFIG = new Config();

    /** РћР±С‰Р°СЏ (server/common) РёРЅРёС†РёР°Р»РёР·Р°С†РёСЏ вЂ” РµСЃР»Рё РїРѕРЅР°РґРѕР±РёС‚СЃСЏ. */
    public VulkanMod() {
        // РџСЂРёРјРµСЂ: NeoForge.EVENT_BUS.addListener(this::commonSetup);
    }

    /** Р’РµСЂСЃРёСЏ РјРѕРґР° РёР· РјРµС‚Р°РґР°РЅРЅС‹С… (fallback РЅР° "dev"). */
    public static String getVersion() {
        return ModList.get().getModContainerById("vulkanmod")
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("dev");
    }

    /** РљР»РёРµРЅС‚СЃРєРёРµ СЃРѕР±С‹С‚РёСЏ. */
    @EventBusSubscriber(modid = "vulkanmod", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            // РїРµСЂРµРЅРѕСЃ РІСЃРµР№ РєР»РёРµРЅС‚СЃРєРѕР№ РёРЅРёС†РёР°Р»РёР·Р°С†РёРё СЃСЋРґР°
            net.vulkanmod.Initializer.initClient();
        }
    }
}
