package gay.themattabase.idletickvillagers.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Exposes Brain's private tick sub-methods for selective throttling.
@Mixin(Brain.class)
public interface BrainAccessor<E extends LivingEntity> {

    @Invoker("forgetOutdatedMemories")
    void idleTick$forgetOutdatedMemories();

    @Invoker("tickSensors")
    void idleTick$tickSensors(ServerLevel level, E body);

    @Invoker("startEachNonRunningBehavior")
    void idleTick$startEachNonRunningBehavior(ServerLevel level, E body);

    @Invoker("tickEachRunningBehavior")
    void idleTick$tickEachRunningBehavior(ServerLevel level, E body);
}
