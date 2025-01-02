package com.ichphilipp.logicchips.utils;

import com.google.common.collect.ImmutableSet;
import com.ichphilipp.logicchips.LogicChips;
import lombok.val;
import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.http.util.Asserts;

import java.util.function.Supplier;

public class RegistryMgr {

    public static final DeferredRegister<Item> ITEM = DeferredRegister
        .create(LogicChips.MOD_ID, Registry.ITEM_REGISTRY);
    public static final DeferredRegister<Block> BLOCK = DeferredRegister
        .create(LogicChips.MOD_ID, Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister
        .create(LogicChips.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);

    public static <T extends Block> RegistrySupplier<BlockItem> registerBlockItem(String name, RegistrySupplier<T> block) {
        return ITEM.register(name, () -> new BlockItem(block.get(), LogicChips.DEFAULT_ITEM_PROP));
    }

    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBE(
        RegistrySupplier<? extends Block> block,
        Supplier<T> getterBE
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
