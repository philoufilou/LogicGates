package com.ichphilipp.logicchips.blocks;

import com.ichphilipp.logicchips.utils.RegistryMgr;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Locale;
import java.util.function.Supplier;

public enum LogicChipsBlock implements Supplier<Block> {
    //TODO: check if using repeater's prop is a good idea
    GATE_FRAME(() -> new ChipFrame(BlockBehaviour.Properties.copy(Blocks.REPEATER)));

    public final String name;
    public final RegistrySupplier<Block> block;
    public final RegistrySupplier<BlockItem> item;

    @Override
    public Block get() {
        return this.block.get();
    }

    public RegistrySupplier<BlockItem> item() {
        return this.item;
    }

    LogicChipsBlock(Supplier<Block> template) {
        this.name = this.name().toLowerCase(Locale.ROOT);
        this.block = RegistryMgr.registerBlock(this.name, template);
        this.item = RegistryMgr.registerBlockItem(this.name, this.block);
    }

    public static void init() {
        for (LogicChipsBlock block : LogicChipsBlock.values()) {
            RegistryMgr.BLOCKS.put(block.name, block);
        }
    }
}
