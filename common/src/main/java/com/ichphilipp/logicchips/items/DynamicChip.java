package com.ichphilipp.logicchips.items;

import com.ichphilipp.logicchips.utils.BitWiseUtil;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author ZZZank
 */
public class DynamicChip extends Chip {

    public static final int LOGIC_BITS_SIZE = 8;

    public DynamicChip() {
        super(ChipType.dynamic);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        TooltipContext context,
        @NotNull List<Component> tooltips,
        @NotNull TooltipFlag flag
    ) {
        super.appendHoverText(stack, context, tooltips, flag);
        val logicData = readLogicFromName(stack.getHoverName());
        if (logicData == null) {
            return;
        }
        val logic = BitWiseUtil.wrap(logicData);
        val allBool = new boolean[]{false, true};
        for (val left : allBool) {
            for (val mid : allBool) {
                for (val right : allBool) {
                    val tip = Component.empty()
                        .append(signal(left ? REGULAR_AQUA : DARK_AQUA))
                        .append(" + ")
                        .append(signal(mid ? REGULAR_PURPLE : DARK_PURPLE))
                        .append(" + ")
                        .append(signal(right ? REGULAR_YELLOW : DARK_YELLOW))
                        .append(" -> ")
                        .append(signal(BitWiseUtil.get(logic, BitWiseUtil.wrap(left, mid, right)) ? REGULAR_RED : DARK_RED))
                    ;
                    tooltips.add(tip);
                }
            }
        }
    }

    public static final TextColor DARK_YELLOW = TextColor.parseColor("#404000").getOrThrow();
    public static final TextColor REGULAR_YELLOW = TextColor.fromLegacyFormat(ChatFormatting.YELLOW);
    public static final TextColor DARK_AQUA = TextColor.parseColor("#004040").getOrThrow();
    public static final TextColor REGULAR_AQUA = TextColor.fromLegacyFormat(ChatFormatting.AQUA);
    public static final TextColor DARK_PURPLE = TextColor.parseColor("#400040").getOrThrow();
    public static final TextColor REGULAR_PURPLE = TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE);
    public static final TextColor DARK_RED = TextColor.parseColor("#400000").getOrThrow();
    public static final TextColor REGULAR_RED = TextColor.fromLegacyFormat(ChatFormatting.RED);

    private static @NotNull MutableComponent signal(TextColor color) {
        return Component.literal("█").setStyle(Style.EMPTY.withColor(color));
    }

    public static boolean @Nullable [] readLogicFromName(@NotNull Component hoverName) {
        val name = hoverName.getString();
        if (name.length() < LOGIC_BITS_SIZE) {
            return null;
        }
        val logics = new boolean[LOGIC_BITS_SIZE];
        for (int i = 0; i < LOGIC_BITS_SIZE; i++) {
            val c = name.charAt(i);
            if (c == '0') {
                logics[i] = false;
            } else if (c == '1') {
                logics[i] = true;
            } else {
                return null;
            }
        }
        return logics;
    }
}
