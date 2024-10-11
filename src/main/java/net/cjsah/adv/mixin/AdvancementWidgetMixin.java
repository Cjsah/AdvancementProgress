package net.cjsah.adv.mixin;

import net.cjsah.adv.AdvancementUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;

@Environment(EnvType.CLIENT)
@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    private static final Text SHIFT = translatable("advancement.shift.title", literal("shift").formatted(Formatting.AQUA, Formatting.ITALIC));
    private static final Text TITLE = translatable("advancement.shift.need");
    @Shadow @Final private int width;
    @Shadow private AdvancementProgress progress;
    @Shadow @Final private Advancement advancement;
    @Shadow protected abstract List<StringVisitable> wrapDescription(Text text, int width);
    private List<OrderedText> texts;
    private List<OrderedText> shift;
    private List<OrderedText> title;
    private boolean isNormalDesc = true;
    private boolean isShiftDown = false;

    @Inject(method = "drawTooltip", at = @At("HEAD"))
    private void update(DrawContext context, int originX, int originY, float alpha, int x, int y, CallbackInfo ci) {
        Function<String, String> function = null;
        this.isNormalDesc = this.progress.isDone() || (function = AdvancementUtil.TO_TRANSLATE.get(this.advancement.getId())) == null;
        this.isShiftDown = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_SHIFT);
        this.shift = this.getOrderedText(SHIFT);
        this.title = this.getOrderedText(TITLE);
        if (!isNormalDesc) {
            assert function != null;
            MutableText text1 = literal("");
            boolean depart1 = false;
            String[][] requirements = ((AccessorAdvancementProgress)this.progress).getRequirements();
            for (String[] requirement : requirements) {
                boolean add = true;
                for (String detail : requirement) {
                    CriterionProgress criterionProgress = this.progress.getCriterionProgress(detail);
                    if (criterionProgress != null && criterionProgress.isObtained()) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    MutableText text2 = literal("");
                    boolean depart2 = false;
                    for (String s : requirement) {
                        if (depart2) text2.append("/");
                        else depart2 = true;
                        text2.append(translatable(function.apply(s)));
                    }
                    if (depart1) text1.append(", ");
                    else depart1 = true;
                    text1.append(text2);
                }
            }
            this.texts = this.getOrderedText(text1);
        }
    }

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
