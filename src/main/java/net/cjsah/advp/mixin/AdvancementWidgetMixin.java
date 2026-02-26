package net.cjsah.advp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.cjsah.advp.DescriptionModifyList;
import net.cjsah.advp.ProgressMapping;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

//#if MC >= 12109
//$$ import net.minecraft.client.Minecraft;
//#else
import net.minecraft.client.gui.screens.Screen;
//#endif

@Environment(EnvType.CLIENT)
@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Shadow @Final private int width;
    @Shadow @Final private AdvancementNode advancementNode;
    @Shadow protected abstract List<FormattedText> findOptimalLines(Component component, int width);
    @Shadow @Final private List<FormattedCharSequence> description;

    @Unique private Function<String, String> advp$mapping;

    @Unique
    private DescriptionModifyList advp$getDescriptionList() {
        return (DescriptionModifyList) this.description;
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/locale/Language;getVisualOrder(Ljava/util/List;)Ljava/util/List;"))
    private List<FormattedCharSequence> redirect(Language instance, List<FormattedText> list, Operation<List<FormattedCharSequence>> original, @Local(ordinal =
        //#if MC >= 12104
        //$$ 2
        //#else
        1
        //#endif
    ) int width) {
        List<FormattedCharSequence> origin = original.call(instance, list);
        this.advp$mapping = ProgressMapping.get(this.advancementNode.holder().id());
        return new DescriptionModifyList(origin, this.findOptimalLines(ProgressMapping.SHIFT, width), this.findOptimalLines(ProgressMapping.TITLE, width));
    }

    @Inject(method = "setProgress", at = @At("RETURN"))
    private void updateProgress(AdvancementProgress progress, CallbackInfo ci) {
        DescriptionModifyList desc = this.advp$getDescriptionList();
        if (progress.isDone() || this.advp$mapping == null) {
            desc.setShowMore(false);
            return;
        }
        MutableComponent component = Component.literal("");
        AdvancementRequirements requirements = ((AccessorAdvancementProgress) progress).getRequirements();
        requirement:
        for (List<String> requirement : requirements.requirements()) {
            MutableComponent node = Component.literal("");
            for (String detail : requirement) {
                CriterionProgress criterion = progress.getCriterion(detail);
                if (criterion != null && criterion.isDone()) {
                    continue requirement;
                }
                node.append(Component.translatable(this.advp$mapping.apply(detail)));
                node.append("/");
            }
            node.getSiblings().removeLast();
            component.append(node);
            component.append(", ");
        }
        component.getSiblings().removeLast();
        desc.updateContents(this.findOptimalLines(component, this.width - 8));
    }

    @Inject(method = "drawHover", at = @At("HEAD"))
    private void draw(GuiGraphics guiGraphics, int originX, int originY, float alpha, int x, int y, CallbackInfo ci) {
        DescriptionModifyList desc = this.advp$getDescriptionList();
        boolean shiftDown =
            //#if MC >= 12109
            //$$ Minecraft.getInstance().hasShiftDown();
            //#else
            Screen.hasShiftDown();
            //#endif
        if (desc.isShiftKeyDown() != shiftDown) desc.setShiftKeyDown(shiftDown);
    }
}
