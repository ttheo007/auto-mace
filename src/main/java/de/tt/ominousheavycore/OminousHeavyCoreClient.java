package de.tt.ominousheavycore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Client-Mod: Haelt der Spieler einen Ominous Trial Key und schaut auf einen Ominous Vault,
 * der gerade einen Heavy Core anzeigt, wird automatisch Rechtsklick ausgeloest.
 */
public class OminousHeavyCoreClient implements ClientModInitializer {
	private static final int COOLDOWN_TICKS = 10;
	private int cooldown = 0;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(Minecraft client) {
		if (cooldown > 0) {
			cooldown--;
			return;
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
		if (!vault.getSharedData().getDisplayItem().is(Items.HEAVY_CORE)) {
			return;
		}

		client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hit);
		cooldown = COOLDOWN_TICKS;
	}
}
