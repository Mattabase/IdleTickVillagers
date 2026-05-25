# IdleTick — Villagers

A performance mod that throttles idle villager brain ticks to reduce server load. Designed to be **iron-farm safe** out of the box.

## What it does

Vanilla Minecraft runs the full villager AI brain every single tick — sensors, memory expiry, behaviour start, and behaviour tick — for every loaded villager, even when they're standing still in a 1×1 trading hall. This is one of the largest contributors to server-side lag in villager-heavy worlds.

IdleTick addresses this by throttling the **behaviour** portion of the brain tick for idle, confined villagers. Sensors and memory expiry still run every tick, so time-critical detections like zombie proximity (iron farms) and player approach (trading) are never delayed.

With default settings (selective mode, interval 20), Brain.tick() cost is reduced by roughly **71%** for throttled villagers.

## Modes

### Selective mode (default)

Every tick, **sensors and memory expiry** still run at full speed. Only `startEachNonRunningBehavior()` and `tickEachRunningBehavior()` are throttled — these control the villager's own decision-making and movement.

This is the safe default. Iron farms, trading, and all sensor-driven mechanics work at full speed.

### Aggressive mode

Skips the **entire** `Brain.tick()` call on throttled ticks, including sensors. Saves more performance but delays the first zombie detection cycle in iron farms.

> **Warning:** Aggressive mode at the default interval of 20 means sensors only fire every 20th tick (once per second). This can noticeably delay iron golem spawning in iron farms. If you use aggressive mode, consider lowering the interval to 3–5 for faster sensor response.

### Full tick skip (extreme, off by default)

Skips the entire `LivingEntity.tick()` — not just the brain, but movement, physics, and AI goals. This is very aggressive and may break farms. It exists as an option for servers that want maximum savings on purely decorative villagers.

## Confinement detection

By default, only **confined** villagers are throttled. A villager is considered confined when:

- Its pathfinder repeatedly fails to find valid paths (threshold configurable, default 5 failures)
- Its position stays within a 3-block range over a 15-second sampling window

This means villagers that are free-roaming, restocking, or pathfinding to a bed/workstation run at full speed until they settle into a stationary position.

### Unconfined villager throttle

For servers with large numbers of free-roaming villagers, an optional **unconfined throttle interval** can be set (default 1 = off). When enabled, free-roaming villagers are throttled at a separate, more conservative interval.

Unconfined villagers are **immediately unthrottled** when any danger is detected:
- Hostile mob nearby (zombie, pillager, vindicator, etc.)
- Recently took damage (environmental or mob — lava, cactus, falling, attacks)
- On fire
- In water (need full pathfinding to escape)

## Safety bypasses

Villagers in certain states are **never** throttled, regardless of confinement:

- **Panicking** — zombie nearby, iron farm safety
- **Trading** — active player trade
- **Moving** — currently in motion
- **On fire** — burning
- **Hurt marked** — pending knockback/damage sync

Each of these can be individually toggled via config or gamerule.

## Configuration

All settings are available in three ways:

1. **Config file** — `config/idletickvillagers.json` (read on startup)
2. **In-game config screen** — via Mod Menu (Fabric) or the Mods screen (NeoForge)
3. **Gamerules** — per-world, changeable at runtime via `/gamerule`

Gamerules always override the config file when set.

### Config reference

| Setting | Default | Range | Description |
|---|---|---|---|
| `enabled` | `true` | — | Master switch |
| `brainThrottleEnabled` | `true` | — | Enable brain behaviour throttling |
| `brainThrottleInterval` | `20` | 2–20 | Tick behaviours every Nth tick for confined villagers |
| `unconfinedBrainThrottleInterval` | `1` | 1–20 | Tick behaviours every Nth tick for unconfined villagers (1 = off) |
| `aggressiveBrainThrottle` | `false` | — | Also throttle sensors (see warning above) |
| `fullTickSkipEnabled` | `false` | — | Skip entire villager tick (extreme) |
| `fullTickSkipInterval` | `4` | 2–100 | Full-tick skip interval |
| `confinementRequired` | `true` | — | Only throttle confined villagers |
| `confinementThreshold` | `5` | 1–50 | Pathfinding failures before a villager is considered confined |
| `dontSkipIfPanicking` | `true` | — | Never throttle panicking villagers |
| `dontSkipIfTrading` | `true` | — | Never throttle trading villagers |
| `dontSkipIfMoving` | `true` | — | Never throttle moving villagers |
| `dontSkipIfOnFire` | `true` | — | Never throttle burning villagers |
| `dontSkipIfHurtMarked` | `true` | — | Never throttle hurt-marked villagers |

### Gamerules

All settings have corresponding gamerules under the `idletickvillagers` namespace. Examples:

```
/gamerule idletickvillagers:enabled true
/gamerule idletickvillagers:brain_throttle_interval 10
/gamerule idletickvillagers:unconfined_brain_throttle_interval 5
/gamerule idletickvillagers:aggressive_brain_throttle true
```

### Debug

Use `/idletickvillagers glow` to toggle a debug glow effect on villagers that are currently being throttled. Useful for verifying which villagers are confined.

## Iron farm compatibility

With default settings (selective mode, interval 20), iron farms work at full speed because:

1. **GolemSensor** is a *sensor*, and sensors run every tick in selective mode
2. **NearestLivingEntitySensor** detects the zombie every tick, keeping the `NEAREST_HOSTILE` memory fresh
3. The panic → golem spawn chain is entirely sensor-driven, not behaviour-driven

If you switch to **aggressive mode**, sensors are also throttled. At interval 20, zombie detection only happens once per second, which can reduce iron farm rates. To mitigate this, lower the interval to 3–5 when using aggressive mode.

## Compatibility

- **Minecraft** 26.1.2
- **NeoForge** 26.1.2.43+
- **Fabric** 0.146.1+
- Requires **PolyLib** 2.0.6+

## License

MIT
