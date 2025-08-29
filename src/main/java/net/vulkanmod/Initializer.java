package net.vulkanmod;

import net.vulkanmod.config.Config;
import org.slf4j.Logger;

/**
 * Fabric-совместимая шима: старые импорты net.vulkanmod.Initializer
 * продолжают работать, но делегируют в наш неофорджевый VulkanMod.
 */
public final class Initializer {
    public static final Logger LOGGER = VulkanMod.LOGGER;
    public static final Config CONFIG = VulkanMod.CONFIG;

    public static String getVersion() {
        return VulkanMod.getVersion();
    }

    private Initializer() {}
}
