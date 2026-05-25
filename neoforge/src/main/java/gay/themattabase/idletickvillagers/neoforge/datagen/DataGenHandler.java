package gay.themattabase.idletickvillagers.neoforge.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenHandler {

    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModLangProvider::new);
    }
}
