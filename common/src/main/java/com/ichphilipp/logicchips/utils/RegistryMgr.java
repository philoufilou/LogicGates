package com.ichphilipp.logicchips.utils;

import com.google.common.collect.ImmutableSet;
import com.ichphilipp.logicchips.LogicChips;
import com.ichphilipp.logicchips.blocks.LogicChipsBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryMgr {

    public static final Map<String, LogicChipsBlock> BLOCKS = new HashMap<>();

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<Item> ITEM_REG = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCK_REG = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BE_TYPE_REG = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        return ITEM_REG.register(name, item);
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCK_REG.register(name, block);
    }

    public static <T extends Block> RegistrySupplier<BlockItem> registerBlockItem(String name, RegistrySupplier<T> block) {
        return ITEM_REG.register(name, () -> new BlockItem(block.get(), LogicChips.DEFAULT_ITEM_PROP));
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBE(
        String name,
        BlockEntityType.BlockEntitySupplier<T> getterBE,
        Block... validBlocks
    ) {
        return BE_TYPE_REG.register(
            name,
            () -> new BlockEntityType<>(
                getterBE,
                ImmutableSet.copyOf(validBlocks),
                Util.fetchChoiceType(References.BLOCK_ENTITY, name)
            )
        );
    }

    public static void init() {
        TABS.register();
        ITEM_REG.register();
        BLOCK_REG.register();
        BE_TYPE_REG.register();
    }
}
