package gay.themattabase.idletickvillagers;

import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;

// Safety checks shared by both throttle mixins.
public final class VillagerSafetyChecks {

    private VillagerSafetyChecks() {}

    public static boolean shouldBypass(Villager villager) {
        GameRules rules = ((ServerLevel) villager.level()).getGameRules();
        IdleTickVillagersConfig cfg = IdleTickVillagersConfig.get();

        // Panicking — zombie nearby, iron farm safety
        if (effectiveBool(rules, IdleTickVillagersGameRules.DONT_SKIP_IF_PANICKING.get(), cfg.dontSkipIfPanicking)) {
            if (villager.getBrain().isActive(Activity.PANIC)) return true;
        }

        // Currently trading with a player
        if (effectiveBool(rules, IdleTickVillagersGameRules.DONT_SKIP_IF_TRADING.get(), cfg.dontSkipIfTrading)) {
            if (villager.getTradingPlayer() != null) return true;
        }

        // Moving (horizontal velocity > threshold)
        if (effectiveBool(rules, IdleTickVillagersGameRules.DONT_SKIP_IF_MOVING.get(), cfg.dontSkipIfMoving)) {
            Vec3 vel = villager.getDeltaMovement();
            if (vel.x * vel.x + vel.z * vel.z > 1.0E-6) return true;
        }

        // On fire
        if (effectiveBool(rules, IdleTickVillagersGameRules.DONT_SKIP_IF_ON_FIRE.get(), cfg.dontSkipIfOnFire)) {
            if (villager.isOnFire()) return true;
        }

        // Hurt marked (pending knockback/damage sync)
        if (effectiveBool(rules, IdleTickVillagersGameRules.DONT_SKIP_IF_HURT_MARKED.get(), cfg.dontSkipIfHurtMarked)) {
            if (villager.hurtMarked) return true;
        }

        return false;
    }

    public static boolean isConfinementRequired(Villager villager) {
        GameRules rules = ((ServerLevel) villager.level()).getGameRules();
        IdleTickVillagersConfig cfg = IdleTickVillagersConfig.get();
        return effectiveBool(rules, IdleTickVillagersGameRules.CONFINEMENT_REQUIRED.get(), cfg.confinementRequired);
    }

    // Gamerule overrides config.
    public static boolean isFeatureEnabled(Villager villager, boolean configEnabled) {
        GameRules rules = ((ServerLevel) villager.level()).getGameRules();
        return configEnabled && rules.get(IdleTickVillagersGameRules.ENABLED.get());
    }

    private static boolean effectiveBool(GameRules rules, net.minecraft.world.level.gamerules.GameRule<Boolean> rule, boolean configVal) {
        // Both must agree the safety check is on — either can activate it
        return configVal || rules.get(rule);
    }
}
