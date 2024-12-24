package com.ichphilipp.logicchips.utils;

import com.google.common.collect.ImmutableSet;
import com.ichphilipp.logicchips.LogicChips;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lombok.val;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.http.util.Asserts;

public class RegistryMgr {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<Item> ITEM = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCK = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister
        .create(LogicChips.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static <T extends Block> RegistrySupplier<BlockItem> registerBlockItem(String name, RegistrySupplier<T> block) {
        return ITEM.register(name, () -> new BlockItem(block.get(), LogicChips.DEFAULT_ITEM_PROP));
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBE(
        RegistrySupplier<? extends Block> block,
        BlockEntityType.BlockEntitySupplier<T> getterBE
    ) {
        Asserts.check(
            LogicChips.MOD_ID.equals(block.getId().getNamespace()),
            "namespace can only be: " + LogicChips.MOD_ID
        );
        val name = block.getId().getPath();
        return BLOCK_ENTITY_TYPE.register(
            name,
            () -> new BlockEntityType<>(
                getterBE,
                ImmutableSet.of(block.get()),
                Util.fetchChoiceType(References.BLOCK_ENTITY, name)
            )
        );
    }
}
