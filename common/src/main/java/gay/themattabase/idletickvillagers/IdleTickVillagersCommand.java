package gay.themattabase.idletickvillagers;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class IdleTickVillagersCommand {

    private IdleTickVillagersCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("idletickvillagers")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("glow")
                                .executes(ctx -> {
                                    boolean now = VillagerConfinementTracker.toggleDebugGlow();
                                    ctx.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    now ? "idletickvillagers.command.glow.on"
                                                            : "idletickvillagers.command.glow.off"),
                                            true);
                                    return now ? 1 : 0;
                                })
                        )
        );
    }
}
