# Ominous Heavy Core (Fabric 26.3, nur Client)

Haelt du einen **Ominous Trial Key** in der Hand und schaust auf einen **Ominous Vault**, der gerade
ein Item aus deiner Liste anzeigt, klickt die Mod automatisch (Rechtsklick, Pruefung jeden Tick).

## Einstellen, bei welchem Item geklickt wird
Im Spiel mit der Taste **V** (aenderbar unter Steuerung -> Ominous Heavy Core): Items per Klick an/aus.

Oder per Datei:

Datei `config/ominous-heavy-core.json` (wird beim ersten Start angelegt):

```json
{
  "items": ["minecraft:heavy_core", "minecraft:enchanted_golden_apple"]
}
```

- Mehrere Items moeglich. Ohne `minecraft:` davor wird `minecraft:` ergaenzt.
- Aenderungen gelten nach hoechstens 1 Sekunde, ohne Neustart.
- Die Item-ID siehst du im Spiel mit F3+H im Item-Tooltip.

- Reiner Client-Mod, braucht Fabric API.
- Hinweis: Ob das angezeigte Item auch die Beute ist, bestimmt der Server. In Vanilla ist das nicht
  garantiert. Automatisches Klicken kann auf Servern verboten sein, Serverregeln beachten.
- Bauen: `gradle build` (Gradle 9.7.1, JDK 25) oder GitHub Actions (Artifacts).
