package gay.themattabase.idletickvillagers.neoforge;

import gay.themattabase.idletickvillagers.IdleTickVillagers;
import gay.themattabase.idletickvillagers.IdleTickVillagersCommand;
import gay.themattabase.idletickvillagers.IdleTickVillagersGameRules;
import gay.themattabase.idletickvillagers.client.IdleTickVillagersConfigScreen;
import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import gay.themattabase.idletickvillagers.neoforge.datagen.DataGenHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gamerules.GameRule;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod("idletickvillagers")
public class IdleTickVillagersNeoForge {

    private static final DeferredRegister<GameRule<?>> GAME_RULES =
            DeferredRegister.create(Registries.GAME_RULE, IdleTickVillagers.MOD_ID);

    static {
        IdleTickVillagersGameRules.ENABLED = reg("enabled", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.BRAIN_THROTTLE_ENABLED = reg("brain_throttle", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.BRAIN_THROTTLE_INTERVAL = regInt("brain_throttle_interval", () -> IdleTickVillagersGameRules.createInt(3, 2, 20));
        IdleTickVillagersGameRules.FULL_TICK_SKIP_ENABLED = reg("full_tick_skip", () -> IdleTickVillagersGameRules.createBool(false));
        IdleTickVillagersGameRules.FULL_TICK_SKIP_INTERVAL = regInt("full_tick_skip_interval", () -> IdleTickVillagersGameRules.createInt(4, 2, 100));
        IdleTickVillagersGameRules.CONFINEMENT_REQUIRED = reg("confinement_required", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.CONFINEMENT_THRESHOLD = regInt("confinement_threshold", () -> IdleTickVillagersGameRules.createInt(5, 1, 50));
        IdleTickVillagersGameRules.DONT_SKIP_IF_PANICKING = reg("dont_skip_if_panicking", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.DONT_SKIP_IF_TRADING = reg("dont_skip_if_trading", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.DONT_SKIP_IF_MOVING = reg("dont_skip_if_moving", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.DONT_SKIP_IF_ON_FIRE = reg("dont_skip_if_on_fire", () -> IdleTickVillagersGameRules.createBool(true));
        IdleTickVillagersGameRules.DONT_SKIP_IF_HURT_MARKED = reg("dont_skip_if_hurt_marked", () -> IdleTickVillagersGameRules.createBool(true));
    }

    @SuppressWarnings("unchecked")
    private static Supplier<GameRule<Boolean>> reg(String name, Supplier<GameRule<Boolean>> sup) {
        return (Supplier<GameRule<Boolean>>) (Supplier<?>) GAME_RULES.register(name, sup);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<GameRule<Integer>> regInt(String name, Supplier<GameRule<Integer>> sup) {
        return (Supplier<GameRule<Integer>>) (Supplier<?>) GAME_RULES.register(name, sup);
    }

    public IdleTickVillagersNeoForge(IEventBus eventBus, ModContainer container) {
        GAME_RULES.register(eventBus);
        IdleTickVillagers.init();
        IdleTickVillagersConfig.load(FMLPaths.CONFIGDIR.get());

        eventBus.addListener(DataGenHandler::gatherData);
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class,
                event -> IdleTickVillagersCommand.register(event.getDispatcher()));

        if (FMLLoader.getCurrent().getDist().isClient()) {
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (mc, parent) -> new IdleTickVillagersConfigScreen(parent));
        }
    }
}
