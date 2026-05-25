package gay.themattabase.idletickvillagers.neoforge.datagen;

import gay.themattabase.idletickvillagers.IdleTickVillagers;
import net.creeperhost.polylib.datagen.PolyLibLangProvider;
import net.minecraft.data.PackOutput;

public class ModLangProvider extends PolyLibLangProvider {

    public ModLangProvider(PackOutput output) {
        super(output, IdleTickVillagers.MOD_ID);
    }

    @Override
    protected void addModTranslations() {
    }
}
