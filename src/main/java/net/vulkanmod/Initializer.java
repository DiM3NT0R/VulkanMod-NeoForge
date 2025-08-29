package net.vulkanmod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Клиентская инициализация VulkanMod под NeoForge.
 * Вызывается из {@link VulkanMod.ClientEvents#onClientSetup}.
 */
public final class Initializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    private Initializer() {
        // utility-class
    }

    /** Точка входа клиентской инициализации. */
    public static void initClient() {
        LOGGER.info("VulkanMod client init (NeoForge), version={}", VulkanMod.getVersion());

        // TODO: зарегистрируй всё клиентское под NeoForge:
        //  - GUI/экраны, рендер-хэндлеры, keybindings, модельные загрузчики и т.п.
        //  Важно: не использовать Fabric API классы.
    }
}
