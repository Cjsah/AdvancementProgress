package net.cjsah.adv.mixin;

import net.minecraft.advancement.AdvancementProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementProgress.class)
public interface AccessorAdvancementProgress {
    @Accessor
    String[][] getRequirements();
}
