package com.ichphilipp.logicchips.blocks;

import com.ichphilipp.logicchips.utils.RegistryMgr;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author ZZZank
 */
public class ChipFrameEntity extends BlockEntity {
    public int dynamicLogics = 0;

    public static final RegistrySupplier<BlockEntityType<ChipFrameEntity>> TYPE = RegistryMgr.registerBE(
        "chip_frame",
        ChipFrameEntity::new,
        LogicChipsBlock.GATE_FRAME.get()
    );

    public ChipFrameEntity(
        BlockPos blockPos,
        BlockState blockState
    ) {
        super(TYPE.get(), blockPos, blockState);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        dynamicLogics = compoundTag.getInt("dyna");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("dyna", dynamicLogics);
    }
}
