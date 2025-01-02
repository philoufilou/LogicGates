package com.ichphilipp.logicchips.items;

import com.ichphilipp.logicchips.LogicChips;
import com.ichphilipp.logicchips.utils.*;
import java.util.List;

import lombok.val;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class Chip extends Item {

    public final ChipType type;

    public Chip(ChipType type) {
        super(LogicChips.DEFAULT_CHIP_PROP);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        val langKey = Screen.hasShiftDown()
            ? String.format("tooltip.%s.%s", LogicChips.MOD_ID, stack.getItem())
            : "tooltip.logicchips.shift";
        list.add(Component.translatable(langKey));
        super.appendHoverText(stack, context, list, tooltipFlag);
    }
}
