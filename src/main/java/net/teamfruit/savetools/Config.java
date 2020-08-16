package net.teamfruit.savetools;

import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.forge.clothconfig2.api.Modifier;
import me.shedaniel.forge.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Config {

	public static final Config INSTANCE = new Config();

	public final ForgeConfigSpec config;
	public final General general;

	private Config() {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		this.general = new General(builder);
		this.config = builder.build();
	}

	public static class General {
		public final ConfigValue<Integer> interval;
		public final ConfigValue<Boolean> attack;
		public final ConfigValue<Boolean> use;
		public final ConfigValue<Boolean> onlyEnchanted;

		public General(final ForgeConfigSpec.Builder builder) {
			builder.push("General");

			this.interval = builder.comment("Tick interval to monitor the durability of the tool",
					"Increasing the value reduces the load, but may not work as well when using tool at high speeds")
					.defineInRange("interval", 0, 0, 20);
			this.attack = builder.comment("Whether it works in an attack action")
					.define("attack", true);
			this.use = builder.comment("Whether it works in an attack action")
					.define("use", true);
			this.onlyEnchanted = builder.comment("Save only the enchanted tools")
					.define("onlyenchanted", false);

			builder.pop();
		}
	}

	public Screen buildClothConfig(final Screen parent) {
		final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("savetools.config.title");
		final ConfigEntryBuilder entry = builder.getEntryBuilder();

		final ConfigCategory general = builder.getOrCreateCategory("general");
		general.addEntry(entry.startModifierKeyCodeField("savetools.key.toggle", ModifierKeyCode.of(InputHandler.KEY_TOGGLE.getKey(), toClothModifier(InputHandler.KEY_TOGGLE.getKeyModifier())))
				.setDefaultValue(InputHandler.KEY_TOGGLE.getDefault())
				.setSaveConsumer(InputHandler.KEY_TOGGLE::bind)
				.setModifierSaveConsumer(mod -> InputHandler.KEY_TOGGLE.setKeyModifierAndCode(toForgeModifier(mod.getModifier()), mod.getKeyCode()))
				.build());
		general.addEntry(entry.startModifierKeyCodeField("savetools.key.config", ModifierKeyCode.of(InputHandler.KEY_CONFIG.getKey(), toClothModifier(InputHandler.KEY_CONFIG.getKeyModifier())))
				.setDefaultValue(InputHandler.KEY_CONFIG.getDefault())
				.setSaveConsumer(InputHandler.KEY_CONFIG::bind)
				.setModifierSaveConsumer(mod -> InputHandler.KEY_CONFIG.setKeyModifierAndCode(toForgeModifier(mod.getModifier()), mod.getKeyCode()))
				.build());
		general.addEntry(entry.startIntSlider("savetools.config.interval.title", this.general.interval.get(), 0, 20)
				.setDefaultValue(0)
				.setTextGetter((i) -> i+" tick")
				.setSaveConsumer(this.general.interval::set)
				.build());
		general.addEntry(entry.startBooleanToggle("savetools.config.attack.title", this.general.attack.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.general.attack::set)
				.build());
		general.addEntry(entry.startBooleanToggle("savetools.config.use.title", this.general.use.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.general.use::set)
				.build());
		general.addEntry(entry.startBooleanToggle("savetools.config.onlyenchanted.title", this.general.onlyEnchanted.get())
				.setDefaultValue(false)
				.setSaveConsumer(this.general.onlyEnchanted::set)
				.build());

		return builder.setSavingRunnable(this.config::save).build();
	}

	public static Modifier toClothModifier(final KeyModifier modifier) {
		return Modifier.of(modifier==KeyModifier.ALT, modifier==KeyModifier.CONTROL, modifier==KeyModifier.SHIFT);
	}

	public static KeyModifier toForgeModifier(final Modifier modifier) {
		if (modifier.hasAlt())
			return KeyModifier.ALT;
		if (modifier.hasControl())
			return KeyModifier.CONTROL;
		if (modifier.hasShift())
			return KeyModifier.SHIFT;
		return KeyModifier.NONE;
	}

}
