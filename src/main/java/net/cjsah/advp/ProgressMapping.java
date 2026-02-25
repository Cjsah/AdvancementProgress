package net.cjsah.advp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ProgressMapping {
    public static final Component SHIFT = Component.translatable("advancement.shift.title", Component.literal("Shift").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
    public static final Component TITLE = Component.translatable("advancement.shift.need");

    private static final Map<ResourceLocation, Function<String, String>> TO_TRANSLATE = new HashMap<>();

    static {
        TO_TRANSLATE.put(ResourceLocation.parse("adventure/adventuring_time"), str -> "biome." + str.replace(":", "."));
        TO_TRANSLATE.put(ResourceLocation.parse("adventure/kill_all_mobs"), str -> "entity." + str.replace(":", "."));
        TO_TRANSLATE.put(ResourceLocation.parse("husbandry/balanced_diet"), str -> "item.minecraft." + str);
        TO_TRANSLATE.put(ResourceLocation.parse("husbandry/bred_all_animals"), str -> "entity." + str.replace(":", "."));
        TO_TRANSLATE.put(ResourceLocation.parse("husbandry/leash_all_frog_variants"), str -> "frog." + str.replace(":", "."));
        TO_TRANSLATE.put(ResourceLocation.parse("husbandry/complete_catalogue"), str -> "cat." + str.replace(":", "."));
        TO_TRANSLATE.put(ResourceLocation.parse("husbandry/whole_pack"), str -> "wolf." + str.replace(":", "."));
        TO_TRANSLATE.put(ResourceLocation.parse("nether/explore_nether"), str -> "biome." + str.replace(":", "."));
    }

    public static Function<String, String> get(ResourceLocation id) {
        return TO_TRANSLATE.get(id);
    }
}
