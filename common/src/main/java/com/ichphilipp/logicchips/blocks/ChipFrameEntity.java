package com.ichphilipp.logicchips.blocks;

import com.ichphilipp.logicchips.utils.RegistryMgr;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author ZZZank
 */
public class ChipFrameEntity extends BlockEntity {
    public int dynamicLogics = 0;

    public static final RegistrySupplier<BlockEntityType<ChipFrameEntity>> TYPE = RegistryMgr
        .registerBE(LogicChipsBlock.GATE_FRAME.block, ChipFrameEntity::new);

    public ChipFrameEntity() {
        super(TYPE.get());
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        dynamicLogics = compoundTag.getInt("dyna");
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("dyna", dynamicLogics);
        return super.save(tag);
    }
}
