package gay.themattabase.idletickvillagers.mixin;

import gay.themattabase.idletickvillagers.IdleTickVillagersGameRules;
import gay.themattabase.idletickvillagers.VillagerConfinementTracker;
import gay.themattabase.idletickvillagers.VillagerSafetyChecks;
import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Approach A (extreme, OFF by default) — skips the entire LivingEntity.tick().
// Very aggressive, may break farms. Same safety bypasses as brain throttle.
@Mixin(LivingEntity.class)
public abstract class VillagerFullTickMixin {

    @Unique
    private int idleTick$fullSkipCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void idleTick$skipFullVillagerTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        // Only affect villagers, server-side
        if (!(self instanceof Villager villager) || self.level().isClientSide()) return;

        IdleTickVillagersConfig cfg = IdleTickVillagersConfig.get();

        // ── No-op when disabled ────────────────────────────────────
        if (!cfg.enabled || !cfg.fullTickSkipEnabled) return;
        ServerLevel serverLevel = (ServerLevel) villager.level();
        if (!serverLevel.getGameRules().get(IdleTickVillagersGameRules.ENABLED.get())
                || !serverLevel.getGameRules().get(IdleTickVillagersGameRules.FULL_TICK_SKIP_ENABLED.get())) {
            return;
        }

        // ── Safety bypass ──────────────────────────────────────────
        if (VillagerSafetyChecks.shouldBypass(villager)) {
            idleTick$fullSkipCounter = 0;
            VillagerConfinementTracker.updateDebugGlow(villager);
            return;
        }

        // ── Debug glow (runs before throttle decision) ────────────
        VillagerConfinementTracker.updateDebugGlow(villager);

        // ── Confinement gate — only throttle confined villagers ────
        if (VillagerSafetyChecks.isConfinementRequired(villager)) {
            if (!VillagerConfinementTracker.isConfined(villager)) {
                return;
            }
        }

        // ── Throttle ───────────────────────────────────────────────
        int interval = serverLevel.getGameRules().get(IdleTickVillagersGameRules.FULL_TICK_SKIP_INTERVAL.get());
        if (interval < 2) interval = cfg.fullTickSkipInterval;

        if (++idleTick$fullSkipCounter < interval) {
            self.tickCount++;
            ci.cancel();
            return;
        }
        idleTick$fullSkipCounter = 0;
    }
}
