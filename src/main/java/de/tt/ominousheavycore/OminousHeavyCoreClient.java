package de.tt.ominousheavycore;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Client-Mod: Haelt der Spieler einen Ominous Trial Key und schaut auf einen Ominous Vault,
 * der gerade ein Item aus der Liste (config/ominous-heavy-core.json) anzeigt, wird automatisch
 * Rechtsklick ausgeloest. Geprueft wird jeden Tick, aber nur mit Key in der Haupthand.
 * Die Config wird alle 20 Ticks neu eingelesen, wenn sich die Datei geaendert hat.
 */
public class OminousHeavyCoreClient implements ClientModInitializer {
	private static final String DEFAULT_CONFIG = """
			{
			  "items": ["minecraft:heavy_core"]
			}
			""";

	private Path configFile;
	private Set<String> targets = Set.of("minecraft:heavy_core");
	private long lastModified = -1;
	private int reloadTimer = 0;

	@Override
	public void onInitializeClient() {
		configFile = FabricLoader.getInstance().getConfigDir().resolve("ominous-heavy-core.json");
		loadConfig();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void loadConfig() {
		try {
			if (!Files.exists(configFile)) {
				Files.writeString(configFile, DEFAULT_CONFIG);
			}
			long modified = Files.getLastModifiedTime(configFile).toMillis();
			if (modified == lastModified) {
				return;
			}
			lastModified = modified;

			JsonObject root = JsonParser.parseString(Files.readString(configFile)).getAsJsonObject();
			Set<String> set = new HashSet<>();
			for (JsonElement element : root.getAsJsonArray("items")) {
				String id = element.getAsString().trim().toLowerCase(Locale.ROOT);
				if (!id.contains(":")) {
					id = "minecraft:" + id;
				}
				set.add(id);
			}
			targets = set;
		} catch (Exception ignored) {
			// Bei Fehlern (z.B. kaputtes JSON) bleibt die letzte funktionierende Liste aktiv.
		}
	}

	private void tick(Minecraft client) {
		if (++reloadTimer >= 20) {
			reloadTimer = 0;
			loadConfig();
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
		if (!targets.contains(shownId)) {
			return;
		}

		client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hit);
	}
}
