package net.teamfruit.savetools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("savetools")
public class SaveTools {

	public static final Logger LOGGER = LogManager.getLogger();

	public SaveTools() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.INSTANCE.config);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> Config.INSTANCE.buildClothConfig(parent));

		MinecraftForge.EVENT_BUS.register(TickHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(InputHandler.INSTANCE);
		ClientRegistry.registerKeyBinding(InputHandler.KEY_TOGGLE);
		ClientRegistry.registerKeyBinding(InputHandler.KEY_CONFIG);

	}

}
