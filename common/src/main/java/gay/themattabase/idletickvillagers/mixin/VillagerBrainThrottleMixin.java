package gay.themattabase.idletickvillagers.mixin;

import gay.themattabase.idletickvillagers.IdleTickVillagersGameRules;
import gay.themattabase.idletickvillagers.VillagerConfinementTracker;
import gay.themattabase.idletickvillagers.VillagerSafetyChecks;
import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Approach B (default) — throttles Brain.tick() on confined idle villagers.
// Selective mode keeps sensors running every tick; aggressive mode skips everything.
// Safety bypasses for panicking, trading, moving, on fire, hurt-marked.
@Mixin(Villager.class)
public abstract class VillagerBrainThrottleMixin {

    @Unique
    private int idleTick$brainCounter = 0;

    @Redirect(
            method = "customServerAiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"
            ),
            remap = false
    )
    private void idleTick$throttleBrainTick(Brain<Villager> brain, ServerLevel level, net.minecraft.world.entity.LivingEntity body) {
        Villager villager = (Villager) (Object) this;
        IdleTickVillagersConfig cfg = IdleTickVillagersConfig.get();

        // ── No-op when disabled ────────────────────────────────────
        if (!cfg.enabled || !cfg.brainThrottleEnabled) {
            brain.tick(level, villager);
            return;
        }
        if (!level.getGameRules().get(IdleTickVillagersGameRules.ENABLED.get())
                || !level.getGameRules().get(IdleTickVillagersGameRules.BRAIN_THROTTLE_ENABLED.get())) {
            brain.tick(level, villager);
            return;
        }

        // ── Safety bypass — always tick these villagers ─────────────
        if (VillagerSafetyChecks.shouldBypass(villager)) {
            idleTick$brainCounter = 0;
            VillagerConfinementTracker.updateDebugGlow(villager);
            brain.tick(level, villager);
            return;
        }

        // ── Debug glow (runs before throttle decision) ────────────
        VillagerConfinementTracker.updateDebugGlow(villager);

        // ── Confinement gate — only throttle confined villagers ────
        if (VillagerSafetyChecks.isConfinementRequired(villager)) {
            if (!VillagerConfinementTracker.isConfined(villager)) {
                brain.tick(level, villager);
                return;
            }
        }

        // ── Throttle ───────────────────────────────────────────────
        int interval = level.getGameRules().get(IdleTickVillagersGameRules.BRAIN_THROTTLE_INTERVAL.get());
        if (interval < 2) interval = cfg.brainThrottleInterval;

        if (++idleTick$brainCounter < interval) {
            // Skip brain tick entirely — sensors, behaviours, and memory expiry all pause.
            // Everything else in customServerAiStep (trade timers, reputation, raid) continues.
            return;
        }
        idleTick$brainCounter = 0;
        brain.tick(level, villager);
    }
}
