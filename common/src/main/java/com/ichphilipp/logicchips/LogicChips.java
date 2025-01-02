package com.ichphilipp.logicchips;

import com.ichphilipp.logicchips.blocks.ChipFrameEntity;
import com.ichphilipp.logicchips.blocks.LogicChipsBlock;
import com.ichphilipp.logicchips.items.LogicChipsItem;
import com.ichphilipp.logicchips.utils.RegistryMgr;
import me.shedaniel.architectury.registry.CreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class LogicChips {

    public static final String MOD_ID = "logicchips";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final CreativeModeTab ITEM_GROUP = CreativeTabs.create(
        new ResourceLocation(MOD_ID, "tab"),
        () -> LogicChipsItem.AND_GATE.get().getDefaultInstance()
    );
    public static final Item.Properties DEFAULT_ITEM_PROP = new Item.Properties().tab(ITEM_GROUP);
    public static final Item.Properties DEFAULT_CHIP_PROP = new Item.Properties().tab(ITEM_GROUP).stacksTo(16);

    private LogicChips() {}

    public static void init() {
        LogicChipsBlock.getAll();//trigger initialization
        RegistryMgr.BLOCK.register();

        LogicChipsItem.getAll();
        RegistryMgr.ITEM.register();

        ChipFrameEntity.TYPE.isPresent();
        RegistryMgr.BLOCK_ENTITY_TYPE.register();
    }

    public static ResourceLocation rl(@NotNull String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
