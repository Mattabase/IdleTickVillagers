package gay.themattabase.idletickvillagers.fabric.datagen;

import net.creeperhost.polylib.datagen.FabricPolyLibLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricPolyLibLangProvider {

    public ModLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addModTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
    }
}
