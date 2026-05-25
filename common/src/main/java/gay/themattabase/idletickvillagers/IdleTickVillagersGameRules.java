package gay.themattabase.idletickvillagers;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

import java.util.function.Supplier;

// Gamerule declarations — each loader registers these and assigns the Supplier fields.
// Gamerules override config values when a level is available.
public final class IdleTickVillagersGameRules {

    // ── Master switch ──────────────────────────────────────────────
    public static Supplier<GameRule<Boolean>> ENABLED;

    // ── Brain throttle (Approach B, default ON) ────────────────────
    public static Supplier<GameRule<Boolean>> BRAIN_THROTTLE_ENABLED;
    public static Supplier<GameRule<Integer>> BRAIN_THROTTLE_INTERVAL;

    // ── Full tick skip (Approach A, default OFF, extreme) ──────────
    public static Supplier<GameRule<Boolean>> FULL_TICK_SKIP_ENABLED;
    public static Supplier<GameRule<Integer>> FULL_TICK_SKIP_INTERVAL;

    // ── Confinement detection ─────────────────────────────────────
    public static Supplier<GameRule<Boolean>> CONFINEMENT_REQUIRED;
    public static Supplier<GameRule<Integer>> CONFINEMENT_THRESHOLD;

    // ── Safety checks ──────────────────────────────────────────────
    public static Supplier<GameRule<Boolean>> DONT_SKIP_IF_PANICKING;
    public static Supplier<GameRule<Boolean>> DONT_SKIP_IF_TRADING;
    public static Supplier<GameRule<Boolean>> DONT_SKIP_IF_MOVING;
    public static Supplier<GameRule<Boolean>> DONT_SKIP_IF_ON_FIRE;
    public static Supplier<GameRule<Boolean>> DONT_SKIP_IF_HURT_MARKED;

    private IdleTickVillagersGameRules() {}

    public static GameRule<Boolean> createBool(boolean defaultValue) {
        return new GameRule<>(
                GameRuleCategory.MOBS,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                GameRuleTypeVisitor::visitBoolean,
                Codec.BOOL,
                b -> b ? 1 : 0,
                defaultValue,
                FeatureFlagSet.of()
        );
    }

    public static GameRule<Integer> createInt(int defaultValue, int min, int max) {
        return new GameRule<>(
                GameRuleCategory.MOBS,
                GameRuleType.INT,
                IntegerArgumentType.integer(min, max),
                GameRuleTypeVisitor::visitInteger,
                Codec.intRange(min, max),
                i -> i,
                defaultValue,
                FeatureFlagSet.of()
        );
    }
}
