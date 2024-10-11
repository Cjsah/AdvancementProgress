package net.cjsah.adv.mixin;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementProgress.class)
public interface AccessorAdvancementProgress {
    @Accessor
    AdvancementRequirements getRequirements();
}
