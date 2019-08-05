package net.teamfruit.savetools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod("savetools")
public class SaveTools {

	public static final Logger LOGGER = LogManager.getLogger();

	public SaveTools() {
		MinecraftForge.EVENT_BUS.register(TickHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(InputHandler.INSTANCE);
		ClientRegistry.registerKeyBinding(InputHandler.KEY_TOGGLE);
	}

}
