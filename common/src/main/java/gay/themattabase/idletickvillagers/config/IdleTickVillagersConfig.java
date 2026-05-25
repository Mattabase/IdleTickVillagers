package gay.themattabase.idletickvillagers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IdleTickVillagersConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static IdleTickVillagersConfig INSTANCE = new IdleTickVillagersConfig();
    private static Path configPath;

    // ── Master switch ──────────────────────────────────────────────
    public boolean enabled = true;

    // ── Brain throttle (Approach B, default ON) ────────────────────
    public boolean brainThrottleEnabled = true;
    public int brainThrottleInterval = 3;

    // ── Full tick skip (Approach A, default OFF, extreme) ──────────
    public boolean fullTickSkipEnabled = false;
    public int fullTickSkipInterval = 4;

    // ── Confinement detection ──────────────────────────────────────
    public boolean confinementRequired = true;
    public int confinementThreshold = 5;

    // ── Safety checks ──────────────────────────────────────────────
    public boolean dontSkipIfPanicking = true;
    public boolean dontSkipIfTrading = true;
    public boolean dontSkipIfMoving = true;
    public boolean dontSkipIfOnFire = true;
    public boolean dontSkipIfHurtMarked = true;

    public static IdleTickVillagersConfig get() {
        return INSTANCE;
    }

    public static void load(Path configDir) {
        configPath = configDir.resolve("idletickvillagers.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                INSTANCE = GSON.fromJson(json, IdleTickVillagersConfig.class);
                if (INSTANCE == null) INSTANCE = new IdleTickVillagersConfig();
                INSTANCE.clampValues();
            } catch (IOException e) {
                INSTANCE = new IdleTickVillagersConfig();
            }
        } else {
            INSTANCE = new IdleTickVillagersConfig();
            save();
        }
    }

    public static void save() {
        if (configPath == null) return;
        INSTANCE.clampValues();
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            // Silent — config save failure is non-fatal
        }
    }

    private void clampValues() {
        brainThrottleInterval = Math.clamp(brainThrottleInterval, 2, 20);
        fullTickSkipInterval = Math.clamp(fullTickSkipInterval, 2, 100);
        confinementThreshold = Math.clamp(confinementThreshold, 1, 50);
    }
}
