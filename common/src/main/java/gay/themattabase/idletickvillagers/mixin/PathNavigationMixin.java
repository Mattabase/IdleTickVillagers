package gay.themattabase.idletickvillagers.mixin;

import gay.themattabase.idletickvillagers.VillagerConfinementTracker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Tracks villager pathfinding success/failure for confinement detection.
@Mixin(PathNavigation.class)
public abstract class PathNavigationMixin {

    @Shadow(remap = false)
    protected Mob mob;

    @Inject(method = "moveTo(Lnet/minecraft/world/level/pathfinder/Path;D)Z",
            at = @At("RETURN"), remap = false)
    private void idleTick$trackPathResult(@Nullable Path path, double speed,
                                           CallbackInfoReturnable<Boolean> cir) {
        if (this.mob instanceof Villager villager && !villager.level().isClientSide()) {
            VillagerConfinementTracker.recordPathResult(villager, cir.getReturnValue());
        }
    }
}
