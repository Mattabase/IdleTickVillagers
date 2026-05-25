package gay.themattabase.idletickvillagers.fabric.datagen;

import gay.themattabase.idletickvillagers.IdleTickVillagers;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenEntrypoint implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        // Ensure translations are registered before datagen runs
        IdleTickVillagers.init();

        var pack = generator.createPack();
        pack.addProvider(ModLangProvider::new);
    }
}
