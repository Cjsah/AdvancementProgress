package net.cjsah.adv;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class AdvancementUtil {
    public static final Map<Identifier, Function<String, String>> TO_TRANSLATE = new HashMap<>();


    static {
        TO_TRANSLATE.put(new Identifier("adventure/adventuring_time"), str -> "biome." + str.replace(":", "."));
        TO_TRANSLATE.put(new Identifier("adventure/kill_all_mobs"), str -> "entity." + str.replace(":", "."));
        TO_TRANSLATE.put(new Identifier("husbandry/balanced_diet"), str -> "item.minecraft." + str);
        TO_TRANSLATE.put(new Identifier("husbandry/bred_all_animals"), str -> "entity." + str.replace(":", "."));
        TO_TRANSLATE.put(new Identifier("husbandry/leash_all_frog_variants"), str -> "frog." + str.replace(":", "."));
        TO_TRANSLATE.put(new Identifier("husbandry/complete_catalogue"), str -> "cat." + str.replace(":", "."));
        TO_TRANSLATE.put(new Identifier("nether/explore_nether"), str -> "biome." + str.replace(":", "."));
    }

}
