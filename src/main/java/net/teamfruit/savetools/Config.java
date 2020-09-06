package net.teamfruit.savetools;

import java.util.Arrays;
import java.util.List;

import me.shedaniel.forge.clothconfig2.api.ConfigBuilder;
import me.shedaniel.forge.clothconfig2.api.ConfigCategory;
import me.shedaniel.forge.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.forge.clothconfig2.api.Modifier;
import me.shedaniel.forge.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Config {

	public static final Config INSTANCE = new Config();

	public final ForgeConfigSpec config;
	public final General general;
	public final Material material;
	public final Advanced advanced;

	private Config() {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		this.general = new General(builder);
		this.advanced = new Advanced(builder);
		this.material = new Material(builder);
		this.config = builder.build();
	}

	public static class General {
		public final IntValue interval;
		public final BooleanValue attack;
		public final BooleanValue use;
		public final BooleanValue onlyEnchanted;
		public final BooleanValue useAbsolute;
		public final ConfigValue<Integer> absoluteThreshold;
		public final BooleanValue usePercentage;
		public final ConfigValue<Integer> percentageThreshold;

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
			this.useAbsolute = builder.comment("Use absolute value threshold")
					.define("useabsolute", true);
			this.absoluteThreshold = builder.comment("Absolute threshold")
					.define("absolutethreshold", 2);
			this.usePercentage = builder.comment("Use percentage value threshold")
					.define("usepercentage", true);
			this.percentageThreshold = builder.comment("Percentage threshold")
					.define("percentagethreshold", 1);

			builder.pop();
		}
	}

	public static class Material {
		public final BooleanValue wood;
		public final BooleanValue stone;
		public final BooleanValue iron;
		public final BooleanValue gold;
		public final BooleanValue diamond;

		public Material(final ForgeConfigSpec.Builder builder) {
			builder.push("Material");

			this.wood = builder.comment("Wooden tools")
					.define("wood", true);
			this.stone = builder.comment("Stone tools")
					.define("stone", true);
			this.iron = builder.comment("Iron tools")
					.define("iron", true);
			this.gold = builder.comment("Golden tools")
					.define("gold", true);
			this.diamond = builder.comment("Diamond tools")
					.define("diamond", true);

			builder.pop();
		}
	}

	public static class Advanced {
		public static final List<String> DEFAULT_LIST = Arrays.asList(
				"minecraft:wooden_sword",
				"minecraft:wooden_shovel",
				"minecraft:wooden_pickaxe",
				"minecraft:wooden_axe",
				"minecraft:wooden_hoe",
				"minecraft:stone_sword",
				"minecraft:stone_shovel",
				"minecraft:stone_pickaxe",
				"minecraft:stone_axe",
				"minecraft:stone_hoe");

		public final BooleanValue enableListFiler;
		public final BooleanValue whitelistMode;
		public final ConfigValue<List<String>> list;

		public Advanced(final ForgeConfigSpec.Builder builder) {
			builder.push("Advanced");

			this.enableListFiler = builder.comment("Enable list filter")
					.define("enablelistfiler", false);
			this.whitelistMode = builder.comment("Whitelist mode")
					.define("whitelistmode", false);
			this.list = builder.comment("Item blacklist")
					.define("blacklist", DEFAULT_LIST);

			builder.pop();
		}
	}

	public Screen buildClothConfig(final Screen parent) {
		final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle("config.savetools.title");
		final ConfigEntryBuilder entry = builder.getEntryBuilder();

		final ConfigCategory general = builder.getOrCreateCategory("config.savetools.category.general");
		general.addEntry(entry.startModifierKeyCodeField("key.savetools.toggle", ModifierKeyCode.of(InputHandler.KEY_TOGGLE.getKey(), toClothModifier(InputHandler.KEY_TOGGLE.getKeyModifier())))
				.setDefaultValue(InputHandler.KEY_TOGGLE.getDefault())
				.setSaveConsumer(InputHandler.KEY_TOGGLE::bind)
				.setModifierSaveConsumer(mod -> InputHandler.KEY_TOGGLE.setKeyModifierAndCode(toForgeModifier(mod.getModifier()), mod.getKeyCode()))
				.build());
		general.addEntry(entry.startModifierKeyCodeField("key.savetools.config", ModifierKeyCode.of(InputHandler.KEY_CONFIG.getKey(), toClothModifier(InputHandler.KEY_CONFIG.getKeyModifier())))
				.setDefaultValue(InputHandler.KEY_CONFIG.getDefault())
				.setSaveConsumer(InputHandler.KEY_CONFIG::bind)
				.setModifierSaveConsumer(mod -> InputHandler.KEY_CONFIG.setKeyModifierAndCode(toForgeModifier(mod.getModifier()), mod.getKeyCode()))
				.build());
		general.addEntry(entry.startIntSlider("config.savetools.interval.title", this.general.interval.get(), 0, 20)
				.setDefaultValue(0)
				.setTextGetter((i) -> i+" tick")
				.setSaveConsumer(this.general.interval::set)
				.build());
		general.addEntry(entry.startBooleanToggle("config.savetools.attack.title", this.general.attack.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.general.attack::set)
				.build());
		general.addEntry(entry.startBooleanToggle("config.savetools.use.title", this.general.use.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.general.use::set)
				.build());
		general.addEntry(entry.startBooleanToggle("config.savetools.onlyenchanted.title", this.general.onlyEnchanted.get())
				.setDefaultValue(false)
				.setSaveConsumer(this.general.onlyEnchanted::set)
				.build());
		general.addEntry(entry.startBooleanToggle("config.savetools.useabsolute.title", this.general.useAbsolute.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.general.useAbsolute::set)
				.build());
		general.addEntry(entry.startIntField("config.savetools.absolutethreshold.title", this.general.absoluteThreshold.get())
				.setDefaultValue(2)
				.setSaveConsumer(this.general.absoluteThreshold::set)
				.build());
		general.addEntry(entry.startBooleanToggle("config.savetools.usepercentage.title", this.general.usePercentage.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.general.usePercentage::set)
				.build());
		general.addEntry(entry.startIntSlider("config.savetools.percentagethreshold.title", this.general.percentageThreshold.get(), 0, 100)
				.setDefaultValue(1)
				.setTextGetter((i) -> i+" %")
				.setSaveConsumer(this.general.percentageThreshold::set)
				.build());

		final ConfigCategory material = builder.getOrCreateCategory("config.savetools.category.material");
		material.addEntry(entry.startBooleanToggle("config.savetools.wood", this.material.wood.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.material.wood::set)
				.build());
		material.addEntry(entry.startBooleanToggle("config.savetools.stone", this.material.stone.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.material.stone::set)
				.build());
		material.addEntry(entry.startBooleanToggle("config.savetools.iron", this.material.iron.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.material.iron::set)
				.build());
		material.addEntry(entry.startBooleanToggle("config.savetools.gold", this.material.gold.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.material.gold::set)
				.build());
		material.addEntry(entry.startBooleanToggle("config.savetools.diamond", this.material.diamond.get())
				.setDefaultValue(true)
				.setSaveConsumer(this.material.diamond::set)
				.build());

		final ConfigCategory advanced = builder.getOrCreateCategory("config.savetools.category.advanced");
		advanced.addEntry(entry.startBooleanToggle("config.savetools.enablelistfilter.title", this.advanced.enableListFiler.get())
				.setDefaultValue(false)
				.setSaveConsumer(this.advanced.enableListFiler::set)
				.build());
		advanced.addEntry(entry.startTextDescription(I18n.format("config.savetools.advanced.description1")).build());
		advanced.addEntry(entry.startTextDescription(I18n.format("config.savetools.advanced.description2")).build());
		advanced.addEntry(entry.startBooleanToggle("config.savetools.whitelistmode.title", this.advanced.whitelistMode.get())
				.setDefaultValue(false)
				.setSaveConsumer(this.advanced.whitelistMode::set)
				.build());
		advanced.addEntry(entry.startStrList("config.savetools.blacklist.title", this.advanced.list.get())
				.setDefaultValue(Advanced.DEFAULT_LIST)
				.setSaveConsumer(this.advanced.list::set)
				.build());

		//			ForgeRegistries.ITEMS.getValues().stream().filter(Item::isDamageable).map(item -> {
		//		final ResourceLocation location = item.getRegistryName();
		//		if (location==null)
		//			return item.toString();
		//		return location.toString();
		//	}).collect(Collectors.toList());

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
