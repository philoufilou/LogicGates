package com.ichphilipp.logicchips.blocks;

import com.ichphilipp.logicchips.utils.RegistryMgr;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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

    public ChipFrameEntity(
        BlockPos blockPos,
        BlockState blockState
    ) {
        super(TYPE.get(), blockPos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        dynamicLogics = compoundTag.getInt("dyna");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putInt("dyna", dynamicLogics);
    }
}
