package com.ichphilipp.logicchips.blocks;

import com.ichphilipp.logicchips.utils.RegistryMgr;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class LogicChipsBlock<T extends Block> implements Supplier<T> {
    private static final Map<String, LogicChipsBlock<?>> ALL = new LinkedHashMap<>();

    public static final LogicChipsBlock<ChipFrame> GATE_FRAME = new LogicChipsBlock<>(
        "gate_frame",
        () -> new ChipFrame(BlockBehaviour.Properties.copy(Blocks.REPEATER))
    );

    public static Map<String, LogicChipsBlock<?>> getAll() {
        return Collections.unmodifiableMap(ALL);
    }

    public final String name;
    public final RegistrySupplier<T> block;
    public final RegistrySupplier<BlockItem> item;

    @Override
    public T get() {
        return this.block.get();
    }

    public RegistrySupplier<BlockItem> item() {
        return this.item;
    }

    private LogicChipsBlock(String name, Supplier<T> template) {
        this.name = name.toLowerCase(Locale.ROOT);
        if (ALL.containsKey(name)) {
            throw new IllegalArgumentException("already registered");
        }
        this.block = RegistryMgr.registerBlock(this.name, template);
        this.item = RegistryMgr.registerBlockItem(this.name, this.block);
        ALL.put(this.name, this);
    }
}
