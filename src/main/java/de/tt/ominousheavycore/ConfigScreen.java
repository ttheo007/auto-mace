package de.tt.ominousheavycore;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Einstellungs-Bildschirm: Items per Klick an- und ausschalten. */
public class ConfigScreen extends Screen {
	/** Moegliche Beute aus Ominous Vaults. Eigene IDs gehen weiter ueber die JSON-Datei. */
	private static final String[] ITEMS = {
			"minecraft:heavy_core",
			"minecraft:enchanted_golden_apple",
			"minecraft:diamond",
			"minecraft:emerald",
			"minecraft:diamond_block",
			"minecraft:emerald_block",
			"minecraft:enchanted_book",
			"minecraft:ominous_bottle",
			"minecraft:crossbow",
			"minecraft:diamond_axe",
			"minecraft:diamond_chestplate",
			"minecraft:iron_axe",
			"minecraft:flow_banner_pattern",
			"minecraft:guster_banner_pattern",
			"minecraft:flow_armor_trim_smithing_template",
			"minecraft:bolt_armor_trim_smithing_template",
			"minecraft:music_disc_creator",
			"minecraft:music_disc_creator_music_box"
	};

	private static final int COLUMNS = 3;
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	private static final int GAP = 4;

	public ConfigScreen() {
		super(Component.literal("Ominous Heavy Core"));
	}

	@Override
	protected void init() {
		int totalWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * GAP;
		int startX = this.width / 2 - totalWidth / 2;
		int startY = 40;

		for (int i = 0; i < ITEMS.length; i++) {
			String id = ITEMS[i];
			int x = startX + (i % COLUMNS) * (BUTTON_WIDTH + GAP);
			int y = startY + (i / COLUMNS) * (BUTTON_HEIGHT + GAP);
			Button button = Button.builder(label(id), b -> {
				ModConfig.toggle(id);
				b.setMessage(label(id));
			}).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();
			this.addRenderableWidget(button);
		}

		int rows = (ITEMS.length + COLUMNS - 1) / COLUMNS;
		int doneY = Math.min(startY + rows * (BUTTON_HEIGHT + GAP) + 8, this.height - BUTTON_HEIGHT - 6);
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
				.bounds(this.width / 2 - 100, doneY, 200, BUTTON_HEIGHT).build());
	}

	private static Component label(String id) {
		String name = id.substring(id.indexOf(':') + 1).replace('_', ' ');
		return Component.literal((ModConfig.isEnabled(id) ? "[X] " : "[ ] ") + name);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		String title = "Auto-Klick bei diesen Items im Ominous Vault:";
		graphics.text(this.font, title, this.width / 2 - this.font.width(title) / 2, 18, 0xFFFFFFFF, true);
	}
}
