package gay.themattabase.idletickvillagers.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import gay.themattabase.idletickvillagers.client.IdleTickVillagersConfigScreen;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return IdleTickVillagersConfigScreen::new;
    }
}
