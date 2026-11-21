package de.tt.ominousheavycore;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Client-Mod: Haelt der Spieler einen Ominous Trial Key und schaut auf einen Ominous Vault,
 * der gerade ein ausgewaehltes Item anzeigt, wird automatisch Rechtsklick ausgeloest.
 * Geprueft wird jeden Tick, aber nur mit Key in der Haupthand. Auswahl: Taste V oder config-Datei.
 */
public class OminousHeavyCoreClient implements ClientModInitializer {
	private static final String MOD_ID = "ominous-heavy-core";

	private KeyMapping openConfigKey;
	private int reloadTimer = 0;

	@Override
	public void onInitializeClient() {
		ModConfig.init();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key." + MOD_ID + ".open_config",
				InputConstants.Type.KEYBOARD,
				InputConstants.KEY_V,
				category));

		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(Minecraft client) {
		if (++reloadTimer >= 20) {
			reloadTimer = 0;
			ModConfig.load();
		}

		while (openConfigKey.consumeClick()) {
			if (client.player != null) {
				client.gui.setScreen(new ConfigScreen());
			}
		}

		if (client.player == null || client.level == null || client.gameMode == null || !client.mouseHandler.isMouseGrabbed()) {
			return;
		}
		if (!client.player.getMainHandItem().is(Items.OMINOUS_TRIAL_KEY)) {
			return;
		}
		if (!(client.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}

		BlockPos pos = hit.getBlockPos();
		BlockState state = client.level.getBlockState(pos);
		if (!state.is(Blocks.VAULT) || !state.getValue(VaultBlock.OMINOUS)) {
			return;
		}
		if (!(client.level.getBlockEntity(pos) instanceof VaultBlockEntity vault)) {
			return;
		}

		ItemStack shown = vault.getSharedData().getDisplayItem();
		String shownId = BuiltInRegistries.ITEM.getKey(shown.getItem()).toString();
		if (!ModConfig.isEnabled(shownId)) {
			return;
		}

		client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hit);
	}
}
