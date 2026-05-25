package gay.themattabase.idletickvillagers.neoforge;

import gay.themattabase.idletickvillagers.client.IdleTickVillagersConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClientSetup {
    public static void init(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> new IdleTickVillagersConfigScreen(parent));
    }
}
