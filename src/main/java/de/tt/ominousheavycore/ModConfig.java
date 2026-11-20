package de.tt.ominousheavycore;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/** Liste der Item-IDs, bei denen geklickt wird. Gespeichert in config/ominous-heavy-core.json. */
final class ModConfig {
	static volatile Set<String> targets = new HashSet<>(Set.of("minecraft:heavy_core"));

	private static Path file;
	private static long lastModified = -1;

	private ModConfig() {
	}

	static void init() {
		file = FabricLoader.getInstance().getConfigDir().resolve("ominous-heavy-core.json");
		load();
	}

	/** Liest die Datei neu, falls sie sich geaendert hat (legt sie beim ersten Start an). */
	static void load() {
		try {
			if (!Files.exists(file)) {
				save();
				return;
			}
			long modified = Files.getLastModifiedTime(file).toMillis();
			if (modified == lastModified) {
				return;
			}
			lastModified = modified;

			JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
			Set<String> set = new HashSet<>();
			for (JsonElement element : root.getAsJsonArray("items")) {
				set.add(normalize(element.getAsString()));
			}
			targets = set;
		} catch (Exception ignored) {
			// Bei Fehlern (z.B. kaputtes JSON) bleibt die letzte funktionierende Liste aktiv.
		}
	}

	static void save() {
		try {
			JsonArray items = new JsonArray();
			for (String id : new TreeSet<>(targets)) {
				items.add(id);
			}
			JsonObject root = new JsonObject();
			root.add("items", items);
			Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(root));
			lastModified = Files.getLastModifiedTime(file).toMillis();
		} catch (Exception ignored) {
			// Speichern ist optional, die Liste bleibt im Speicher aktiv.
		}
	}

	static boolean isEnabled(String id) {
		return targets.contains(id);
	}

	static void toggle(String id) {
		Set<String> copy = new HashSet<>(targets);
		if (!copy.remove(id)) {
			copy.add(id);
		}
		targets = copy;
		save();
	}

	private static String normalize(String raw) {
		String id = raw.trim().toLowerCase(Locale.ROOT);
		return id.contains(":") ? id : "minecraft:" + id;
	}
}
