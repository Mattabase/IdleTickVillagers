package gay.themattabase.idletickvillagers;

import net.creeperhost.polylib.data.lang.PolyLangContributions;

// Common init — called from each loader's entry point.
public final class IdleTickVillagers {

    public static final String MOD_ID = "idletickvillagers";

    private IdleTickVillagers() {}

    public static void init() {
        registerTranslations();
    }

    private static void registerTranslations() {
        // ── Config screen ──────────────────────────────────────────
        contribute("idletickvillagers.config.title", "IdleTick - Villagers Configuration");

        contribute("idletickvillagers.config.enabled", "IdleTick Enabled");
        contribute("idletickvillagers.config.enabled.tooltip", "Master switch — disables all optimisations when off.");

        // Brain throttle (Approach B)
        contribute("idletickvillagers.config.brain_throttle_enabled", "Brain Throttle");
        contribute("idletickvillagers.config.brain_throttle_enabled.tooltip",
                "Throttle villager brain behaviour ticks while keeping sensors active. Safe for farms.");
        contribute("idletickvillagers.config.brain_throttle_interval", "Brain Throttle Interval");
        contribute("idletickvillagers.config.brain_throttle_interval.tooltip",
                "Run brain behaviours every Nth tick (2–20). Lower = more responsive, higher = more savings.");

        // Full tick skip (Approach A, extreme)
        contribute("idletickvillagers.config.full_tick_skip_enabled", "Full Tick Skip (Extreme)");
        contribute("idletickvillagers.config.full_tick_skip_enabled.tooltip",
                "Skip the entire villager tick. Very aggressive — may break farms. Off by default.");
        contribute("idletickvillagers.config.full_tick_skip_interval", "Full Tick Skip Interval");
        contribute("idletickvillagers.config.full_tick_skip_interval.tooltip",
                "Full-tick every Nth tick (2–100). Lower = more responsive, higher = more savings.");

        // Safety checks
        contribute("idletickvillagers.config.dont_skip_if_panicking", "Don't Skip If Panicking");
        contribute("idletickvillagers.config.dont_skip_if_panicking.tooltip",
                "Never throttle villagers that are panicking (e.g. zombie nearby). Keeps iron farms working.");
        contribute("idletickvillagers.config.dont_skip_if_trading", "Don't Skip If Trading");
        contribute("idletickvillagers.config.dont_skip_if_trading.tooltip",
                "Never throttle villagers with an active trading player.");
        contribute("idletickvillagers.config.dont_skip_if_moving", "Don't Skip If Moving");
        contribute("idletickvillagers.config.dont_skip_if_moving.tooltip",
                "Never throttle villagers currently in motion.");
        contribute("idletickvillagers.config.dont_skip_if_on_fire", "Don't Skip If On Fire");
        contribute("idletickvillagers.config.dont_skip_if_on_fire.tooltip",
                "Never throttle villagers that are on fire.");
        contribute("idletickvillagers.config.dont_skip_if_hurt_marked", "Don't Skip If Hurt Marked");
        contribute("idletickvillagers.config.dont_skip_if_hurt_marked.tooltip",
                "Never throttle villagers flagged for knockback/damage sync.");

        // Confinement detection
        contribute("idletickvillagers.config.confinement_required", "Confinement Required");
        contribute("idletickvillagers.config.confinement_required.tooltip",
                "Only throttle villagers detected as confined. Free-roaming villagers tick normally.");
        contribute("idletickvillagers.config.confinement_threshold", "Confinement Threshold");
        contribute("idletickvillagers.config.confinement_threshold.tooltip",
                "Pathfinding failure score needed to consider a villager confined (1–50).");

        // ── Game rule descriptions ─────────────────────────────────
        contribute("gamerule.idletickvillagers.enabled", "Enable IdleTick villager optimisations");
        contribute("gamerule.idletickvillagers.brain_throttle", "Throttle villager brain behaviour ticks");
        contribute("gamerule.idletickvillagers.brain_throttle_interval", "Brain behaviour throttle interval (ticks)");
        contribute("gamerule.idletickvillagers.full_tick_skip", "Skip entire villager tick (extreme)");
        contribute("gamerule.idletickvillagers.full_tick_skip_interval", "Full tick skip interval (ticks)");
        contribute("gamerule.idletickvillagers.dont_skip_if_panicking", "Never throttle panicking villagers");
        contribute("gamerule.idletickvillagers.dont_skip_if_trading", "Never throttle trading villagers");
        contribute("gamerule.idletickvillagers.dont_skip_if_moving", "Never throttle moving villagers");
        contribute("gamerule.idletickvillagers.dont_skip_if_on_fire", "Never throttle burning villagers");
        contribute("gamerule.idletickvillagers.dont_skip_if_hurt_marked", "Never throttle hurt-marked villagers");
        contribute("gamerule.idletickvillagers.confinement_required", "Require confinement before throttling");
        contribute("gamerule.idletickvillagers.confinement_threshold", "Confinement pathfinding failure threshold");
    }

    private static void contribute(String key, String english) {
        PolyLangContributions.contribute(key, english);
    }
}
