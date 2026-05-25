package gay.themattabase.idletickvillagers.client;

import gay.themattabase.idletickvillagers.config.IdleTickVillagersConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class IdleTickVillagersConfigScreen extends Screen {

    private final Screen parent;

    public IdleTickVillagersConfigScreen(Screen parent) {
        super(Component.translatable("idletickvillagers.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        IdleTickVillagersConfig cfg = IdleTickVillagersConfig.get();
        LinearLayout layout = LinearLayout.vertical().spacing(2);

        // ── Master switch ──────────────────────────────────────────
        layout.addChild(toggle("idletickvillagers.config.enabled", cfg.enabled,
                (btn, val) -> cfg.enabled = val));

        // ── Brain throttle (Approach B) ────────────────────────────
        layout.addChild(toggle("idletickvillagers.config.brain_throttle_enabled", cfg.brainThrottleEnabled,
                (btn, val) -> cfg.brainThrottleEnabled = val));

        layout.addChild(intervalButton("idletickvillagers.config.brain_throttle_interval",
                cfg.brainThrottleInterval, 2, 20,
                btn -> {
                    cfg.brainThrottleInterval = nextInterval(cfg.brainThrottleInterval, 2, 20);
                    btn.setMessage(intervalLabel("idletickvillagers.config.brain_throttle_interval",
                            cfg.brainThrottleInterval));
                }));

        // ── Full tick skip (Approach A, extreme) ───────────────────
        layout.addChild(toggle("idletickvillagers.config.full_tick_skip_enabled", cfg.fullTickSkipEnabled,
                (btn, val) -> cfg.fullTickSkipEnabled = val));

        layout.addChild(intervalButton("idletickvillagers.config.full_tick_skip_interval",
                cfg.fullTickSkipInterval, 2, 100,
                btn -> {
                    cfg.fullTickSkipInterval = nextInterval(cfg.fullTickSkipInterval, 2, 100);
                    btn.setMessage(intervalLabel("idletickvillagers.config.full_tick_skip_interval",
                            cfg.fullTickSkipInterval));
                }));

        // ── Confinement detection ─────────────────────────────────
        layout.addChild(toggle("idletickvillagers.config.confinement_required", cfg.confinementRequired,
                (btn, val) -> cfg.confinementRequired = val));

        layout.addChild(intervalButton("idletickvillagers.config.confinement_threshold",
                cfg.confinementThreshold, 1, 50,
                btn -> {
                    cfg.confinementThreshold = nextInterval(cfg.confinementThreshold, 1, 50);
                    btn.setMessage(intervalLabel("idletickvillagers.config.confinement_threshold",
                            cfg.confinementThreshold));
                }));

        // ── Safety checks ──────────────────────────────────────────
        layout.addChild(toggle("idletickvillagers.config.dont_skip_if_panicking", cfg.dontSkipIfPanicking,
                (btn, val) -> cfg.dontSkipIfPanicking = val));

        layout.addChild(toggle("idletickvillagers.config.dont_skip_if_trading", cfg.dontSkipIfTrading,
                (btn, val) -> cfg.dontSkipIfTrading = val));

        layout.addChild(toggle("idletickvillagers.config.dont_skip_if_moving", cfg.dontSkipIfMoving,
                (btn, val) -> cfg.dontSkipIfMoving = val));

        layout.addChild(toggle("idletickvillagers.config.dont_skip_if_on_fire", cfg.dontSkipIfOnFire,
                (btn, val) -> cfg.dontSkipIfOnFire = val));

        layout.addChild(toggle("idletickvillagers.config.dont_skip_if_hurt_marked", cfg.dontSkipIfHurtMarked,
                (btn, val) -> cfg.dontSkipIfHurtMarked = val));

        // ── Done ───────────────────────────────────────────────────
        layout.addChild(Button.builder(CommonComponents.GUI_DONE, btn -> onClose())
                .width(200).build());

        layout.arrangeElements();
        layout.setPosition((this.width - 200) / 2, 30);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void onClose() {
        IdleTickVillagersConfig.save();
        this.minecraft.setScreen(this.parent);
    }

    private static CycleButton<Boolean> toggle(String translationKey, boolean initial,
                                                CycleButton.OnValueChange<Boolean> onChange) {
        CycleButton<Boolean> btn = CycleButton.onOffBuilder(initial)
                .create(0, 0, 200, 20, Component.translatable(translationKey), onChange);
        btn.setTooltip(Tooltip.create(Component.translatable(translationKey + ".tooltip")));
        return btn;
    }

    private static Button intervalButton(String translationKey, int value, int min, int max,
                                          Button.OnPress onPress) {
        Button btn = Button.builder(intervalLabel(translationKey, value), onPress)
                .width(200)
                .tooltip(Tooltip.create(Component.translatable(translationKey + ".tooltip")))
                .build();
        return btn;
    }

    private static Component intervalLabel(String translationKey, int value) {
        return Component.translatable(translationKey).append(": " + value);
    }

    private static int nextInterval(int current, int min, int max) {
        if (current < 10) return Math.min(current + 1, max);
        if (current < 20) return Math.min(current + 5, max);
        if (current < 50) return Math.min(current + 10, max);
        if (current < max) return max;
        return min; // wrap
    }
}
