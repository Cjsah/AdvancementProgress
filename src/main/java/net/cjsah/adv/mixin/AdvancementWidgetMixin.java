package net.cjsah.adv.mixin;

import net.cjsah.adv.AdvancementUtil;
import net.cjsah.adv.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Shadow @Final private int width;
    @Shadow private AdvancementProgress progress;
    @Shadow @Final private PlacedAdvancement advancement;
    @Shadow protected abstract List<StringVisitable> wrapDescription(Text text, int width);
    @Unique private List<OrderedText> texts;
    @Unique private List<OrderedText> shift;
    @Unique private List<OrderedText> title;
    @Unique private boolean isNormalDesc = true;
    @Unique private boolean isShiftDown = false;

    @Inject(method = "drawTooltip", at = @At("HEAD"))
    private void update(DrawContext context, int originX, int originY, float alpha, int x, int y, CallbackInfo ci) {
        Function<String, String> function = null;
        Identifier id = this.advancement.getAdvancementEntry().id();
        this.isNormalDesc = this.progress.isDone() || (function = AdvancementUtil.TO_TRANSLATE.get(id)) == null;
        this.isShiftDown = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_SHIFT);
        this.shift = this.getOrderedText(Constants.SHIFT);
        this.title = this.getOrderedText(Constants.TITLE);
        if (this.isNormalDesc) return;
        assert function != null;
        MutableText result = Text.literal("");
        AdvancementRequirements requirements = ((AccessorAdvancementProgress) this.progress).getRequirements();
        requirement:
        for (List<String> requirement : requirements.requirements()) {
            for (String detail : requirement) {
                CriterionProgress progress = this.progress.getCriterionProgress(detail);
                if (progress != null && progress.isObtained()) {
                    continue requirement;
                }
            }
            MutableText node = Text.literal("");
            for (String s : requirement) {
                node.append(Text.translatable(function.apply(s)));
                node.append("/");
            }
            node.getSiblings().removeLast();
            result.append(node);
            result.append(", ");
        }
        result.getSiblings().removeLast();
        this.texts = this.getOrderedText(result);
    }

    @Unique
    private List<OrderedText> getOrderedText(Text text) {
        return Language.getInstance().reorder(this.wrapDescription(text, this.width - 8));
    }

    @Redirect(method = "drawTooltip", at = @At(value = "INVOKE", target ="Ljava/util/List;size()I", ordinal = 0))
    private int descriptionHeight1(List<OrderedText> list) {
        if (this.isNormalDesc) {
            return list.size();
        } else return list.size() + this.shift.size();
    }

    @Redirect(method = "drawTooltip", at = @At(value = "INVOKE", target ="Ljava/util/List;size()I", ordinal = 1))
    private int descriptionHeight2(List<OrderedText> list) {
        return getDescriptionLength(list);
    }

    @Redirect(method = "drawTooltip", at = @At(value = "INVOKE", target ="Ljava/util/List;size()I", ordinal = 2))
    private int descriptionHeight3(List<OrderedText> list) {
        return getDescriptionLength(list);
    }

    @Redirect(method = "drawTooltip", at = @At(value = "INVOKE", target ="Ljava/util/List;size()I", ordinal = 3))
    private int descriptionHeight4(List<OrderedText> list) {
        return getDescriptionLength(list);
    }

    @Unique
    private int getDescriptionLength(List<OrderedText> list) {
        if (this.isNormalDesc) {
            return list.size();
        } else if (this.isShiftDown) {
            return list.size() + this.texts.size() + this.title.size();
        } else return list.size() + this.shift.size();
    }

    @SuppressWarnings("unchecked")
    @Redirect(method = "drawTooltip", at = @At(value = "INVOKE", target ="Ljava/util/List;get(I)Ljava/lang/Object;"))
    private <T> T getDescription(List<T> list, int i) {
        int length = list.size();
        if (i < length) return list.get(i);
        else if (this.isShiftDown) {
            int titleSize = this.title.size();
            if (i - length < titleSize) return (T) this.title.get(i - length);
            else return (T) this.texts.get(i - length - titleSize);
        } else return (T) this.shift.get(i - length);
    }
}
