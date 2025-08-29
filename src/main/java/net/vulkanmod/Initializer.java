package net.vulkanmod;

import net.vulkanmod.config.Config;
import org.slf4j.Logger;

/**
 * Fabric-СЃРѕРІРјРµСЃС‚РёРјР°СЏ В«РїСЂРѕРєР»Р°РґРєР°В»: СЃС‚Р°СЂС‹Рµ РёРјРїРѕСЂС‚С‹ net.vulkanmod.Initializer
 * РїСЂРѕРґРѕР»Р¶Р°СЋС‚ СЂР°Р±РѕС‚Р°С‚СЊ, Р° РІСЃРµ РґРµР»РµРіРёСЂСѓРµС‚СЃСЏ РІ РЅР°С€ РЅРµРѕС„РѕСЂРґР¶РµРІС‹Р№ VulkanMod.
 */
public final class Initializer {
    public static final Logger LOGGER = VulkanMod.LOGGER;
    public static final Config CONFIG = VulkanMod.CONFIG;

    public static String getVersion() {
        return VulkanMod.getVersion();
    }

    private Initializer() {}
}
