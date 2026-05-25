package gay.themattabase.idletickvillagers;

import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

// Tracks villager confinement via pathfinding failures + movement range sampling.
// Confined = either repeated pathfind failures OR stayed within 3 blocks over 15 seconds.
public final class VillagerConfinementTracker {

    private static final WeakHashMap<Villager, Integer> failScores = new WeakHashMap<>();

    private static final WeakHashMap<Villager, long[]> movementSamples = new WeakHashMap<>();

    private static final int SAMPLE_INTERVAL = 300; // ~15 seconds
    private static final double MOVE_THRESHOLD_SQ = 3.0 * 3.0; // 3 blocks

    private static boolean debugGlowEnabled = false;
    private static final Set<Villager> glowingVillagers = Collections.newSetFromMap(new WeakHashMap<>());

    private VillagerConfinementTracker() {}

    // ── Pathfinding-failure tracking ──────────────────────────

    public static void recordPathResult(Villager villager, boolean success) {
        int score = failScores.getOrDefault(villager, 0);
        if (success) {
            score = Math.max(0, score - 2);
        } else {
            score = Math.min(score + 1, 100);
        }
        failScores.put(villager, score);
    }

    // ── Confinement query ─────────────────────────────────────

    public static boolean isConfined(Villager villager) {
        return hasPathfindingFailures(villager) || hasLimitedMovement(villager);
    }

    private static boolean hasPathfindingFailures(Villager villager) {
        return failScores.getOrDefault(villager, 0) >= getThreshold(villager);
    }

    private static boolean hasLimitedMovement(Villager villager) {
        long currentTick = villager.level().getGameTime();
        long[] sample = movementSamples.get(villager);

        if (sample == null) {
            // First time seeing this villager — record baseline
            movementSamples.put(villager, makeSample(villager, currentTick));
            return false;
        }

        long elapsed = currentTick - sample[3]; // sample[3] = tick of last sample
        if (elapsed < SAMPLE_INTERVAL) {
            // Not time to re-evaluate yet — return last known result
            return sample[4] == 1; // sample[4] = 1 means "confined last check"
        }

        // Time to check: how far did they move from the sample point?
        double dx = villager.getX() - Double.longBitsToDouble(sample[0]);
        double dy = villager.getY() - Double.longBitsToDouble(sample[1]);
        double dz = villager.getZ() - Double.longBitsToDouble(sample[2]);
        double distSq = dx * dx + dy * dy + dz * dz;

        boolean confined = distSq < MOVE_THRESHOLD_SQ;

        // Record new sample
        long[] newSample = makeSample(villager, currentTick);
        newSample[4] = confined ? 1 : 0;
        movementSamples.put(villager, newSample);

        return confined;
    }

    private static long[] makeSample(Villager villager, long tick) {
        return new long[]{
                Double.doubleToLongBits(villager.getX()),
                Double.doubleToLongBits(villager.getY()),
                Double.doubleToLongBits(villager.getZ()),
                tick,
                0 // confined flag
        };
    }

    private static int getThreshold(Villager villager) {
        IdleTickVillagersConfig cfg = IdleTickVillagersConfig.get();
        GameRules rules = ((ServerLevel) villager.level()).getGameRules();
        int ruleVal = rules.get(IdleTickVillagersGameRules.CONFINEMENT_THRESHOLD.get());
        return ruleVal > 0 ? ruleVal : cfg.confinementThreshold;
    }

    public static int getScore(Villager villager) {
        return failScores.getOrDefault(villager, 0);
    }

    // ── Debug glow ─────────────────────────────────────────────

    public static boolean isDebugGlowEnabled() {
        return debugGlowEnabled;
    }

    public static boolean toggleDebugGlow() {
        debugGlowEnabled = !debugGlowEnabled;
        if (!debugGlowEnabled) {
            for (Villager v : glowingVillagers) {
                v.setGlowingTag(false);
            }
            glowingVillagers.clear();
        }
        return debugGlowEnabled;
    }

    public static void updateDebugGlow(Villager villager) {
        if (!debugGlowEnabled) {
            if (glowingVillagers.remove(villager)) {
                villager.setGlowingTag(false);
            }
            return;
        }
        villager.setGlowingTag(true);
        glowingVillagers.add(villager);
    }

    // Clears glow when a villager is no longer being throttled.
    public static void clearDebugGlow(Villager villager) {
        if (glowingVillagers.remove(villager)) {
            villager.setGlowingTag(false);
        }
    }
}
