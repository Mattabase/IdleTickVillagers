package gay.themattabase.idletickvillagers.fabric;

import gay.themattabase.idletickvillagers.IdleTickVillagers;
import gay.themattabase.idletickvillagers.IdleTickVillagersCommand;
import gay.themattabase.idletickvillagers.IdleTickVillagersGameRules;
import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;

public class IdleTickVillagersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        registerGameRules();
        IdleTickVillagers.init();
        IdleTickVillagersConfig.load(FabricLoader.getInstance().getConfigDir());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                IdleTickVillagersCommand.register(dispatcher));
    }

    private void registerGameRules() {
        IdleTickVillagersGameRules.ENABLED = regBool("enabled", true);
        IdleTickVillagersGameRules.BRAIN_THROTTLE_ENABLED = regBool("brain_throttle", true);
        IdleTickVillagersGameRules.BRAIN_THROTTLE_INTERVAL = regInt("brain_throttle_interval", 3, 2, 20);
        IdleTickVillagersGameRules.FULL_TICK_SKIP_ENABLED = regBool("full_tick_skip", false);
        IdleTickVillagersGameRules.FULL_TICK_SKIP_INTERVAL = regInt("full_tick_skip_interval", 4, 2, 100);
        IdleTickVillagersGameRules.CONFINEMENT_REQUIRED = regBool("confinement_required", true);
        IdleTickVillagersGameRules.CONFINEMENT_THRESHOLD = regInt("confinement_threshold", 5, 1, 50);
        IdleTickVillagersGameRules.DONT_SKIP_IF_PANICKING = regBool("dont_skip_if_panicking", true);
        IdleTickVillagersGameRules.DONT_SKIP_IF_TRADING = regBool("dont_skip_if_trading", true);
        IdleTickVillagersGameRules.DONT_SKIP_IF_MOVING = regBool("dont_skip_if_moving", true);
        IdleTickVillagersGameRules.DONT_SKIP_IF_ON_FIRE = regBool("dont_skip_if_on_fire", true);
        IdleTickVillagersGameRules.DONT_SKIP_IF_HURT_MARKED = regBool("dont_skip_if_hurt_marked", true);
    }

    private static java.util.function.Supplier<GameRule<Boolean>> regBool(String name, boolean def) {
        GameRule<Boolean> rule = Registry.register(BuiltInRegistries.GAME_RULE,
                Identifier.fromNamespaceAndPath(IdleTickVillagers.MOD_ID, name),
                IdleTickVillagersGameRules.createBool(def));
        return () -> rule;
    }

    private static java.util.function.Supplier<GameRule<Integer>> regInt(String name, int def, int min, int max) {
        GameRule<Integer> rule = Registry.register(BuiltInRegistries.GAME_RULE,
                Identifier.fromNamespaceAndPath(IdleTickVillagers.MOD_ID, name),
                IdleTickVillagersGameRules.createInt(def, min, max));
        return () -> rule;
    }
}
