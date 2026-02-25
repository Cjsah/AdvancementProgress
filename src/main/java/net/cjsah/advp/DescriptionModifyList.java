package net.cjsah.advp;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class DescriptionModifyList extends ArrayList<FormattedCharSequence> {
    private final List<FormattedCharSequence> original;
    private final List<FormattedCharSequence> shiftText;
    private final List<FormattedCharSequence> titleText;
    private List<FormattedCharSequence> contents;
    private boolean shiftKeyDown;
    private boolean showMore;


    public DescriptionModifyList(List<FormattedCharSequence> original, List<FormattedText> shiftText, List<FormattedText> titleText) {
        this.original = original;
        this.shiftText = Language.getInstance().getVisualOrder(shiftText);
        this.titleText = Language.getInstance().getVisualOrder(titleText);
        this.contents = new ArrayList<>();
        this.shiftKeyDown = false;
        this.showMore = false;
        this.addAll(original);
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
        this.setDirty();
    }

    public void setShiftKeyDown(boolean shiftKeyDown) {
        this.shiftKeyDown = shiftKeyDown;
        this.setDirty();
    }

    public boolean isShiftKeyDown() {
        return this.shiftKeyDown;
    }

    public void updateContents(List<FormattedText> contents) {
        this.contents = Language.getInstance().getVisualOrder(contents);
        this.showMore = true;
        this.setDirty();
    }

    public void setDirty() {
        this.clear();
        this.addAll(this.original);
        if (!this.showMore) return;
        if (this.shiftKeyDown) {
            this.addAll(this.titleText);
            this.addAll(this.contents);
        } else {
            this.addAll(this.shiftText);
        }
    }
}
