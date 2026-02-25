package net.cjsah.advp.mixin;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementProgress.class)
public interface AccessorAdvancementProgress {
    @Accessor
    AdvancementRequirements getRequirements();
}
