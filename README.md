# Ominous Heavy Core (Fabric 26.3)

Jeder Ominous Vault (Trial Chambers) gibt **immer einen Heavy Core**.

- Reiner Client-Mod (`"environment": "client"`), kein Server nötig.
- Funktioniert nur in **Einzelspieler-/eigenen LAN-Welten**: Dort läuft der Server im selben Prozess und liest die Loot-Tabelle aus der Mod.
- Auf fremden Servern hat der Client keinen Einfluss auf den Loot (und das wäre dort auch ein Exploit).
- Umsetzung per Datenpaket: `data/minecraft/loot_table/chests/trial_chambers/reward_ominous.json` überschreibt die Vanilla-Tabelle.

## Bauen
`gradle build` (Gradle 9.7.1) -> `build/libs/ominous-heavy-core-1.0.0.jar` (JDK 25), oder über GitHub Actions (Artifacts).
