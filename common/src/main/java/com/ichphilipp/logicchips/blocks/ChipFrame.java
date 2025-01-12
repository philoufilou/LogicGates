package com.ichphilipp.logicchips.blocks;

import com.ichphilipp.logicchips.items.Chip;
import com.ichphilipp.logicchips.items.DynamicChip;
import com.ichphilipp.logicchips.items.LogicChipsItem;
import com.ichphilipp.logicchips.items.ChipType;

import com.ichphilipp.logicchips.utils.BitWiseUtil;
import com.ichphilipp.logicchips.utils.Cast;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ChipFrame extends DiodeBlock implements EntityBlock {

    public static final EnumProperty<ChipType> TYPE = EnumProperty.create("type", ChipType.class);
    public static final BooleanProperty LEFT_INPUT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT_INPUT = BooleanProperty.create("right");
    public static final BooleanProperty BOTTOM_INPUT = BooleanProperty.create("bottom");

    public ChipFrame(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, ChipType.empty)
                .setValue(LEFT_INPUT, false)
                .setValue(RIGHT_INPUT, false)
                .setValue(BOTTOM_INPUT, false)
                .setValue(POWERED, false)
        );
    }

    @Override
    protected int getDelay(@NotNull BlockState state) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> arg) {
        arg.add(FACING, TYPE, POWERED, LEFT_INPUT, RIGHT_INPUT, BOTTOM_INPUT);
    }

    /**
     * Forge only
     */
    public boolean canConnectRedstone(
        BlockState blockState,
        BlockGetter world,
        BlockPos pos,
        @Nullable Direction side
    ) {
        val type = blockState.getValue(TYPE);
        val facing = blockState.getValue(FACING);
        return side == facing
            || (type.canConnectRight && side == facing.getClockWise())
            || (type.canConnectLeft && side == facing.getCounterClockWise())
            || (type.canConnectMid && side == facing.getOpposite());
    }

    @Override
    protected int getInputSignal(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state) {
        return this.isPowered(state, world, pos) ? 15 : 0;
    }

    @Override
    public void onRemove(
        @NotNull BlockState blockState,
        @NotNull Level world,
        @NotNull BlockPos blockPos,
        @NotNull BlockState newblockState,
        boolean isMoving
    ) {
        if (!isMoving && !blockState.is(newblockState.getBlock())) {
            super.onRemove(blockState, world, blockPos, newblockState, false);
            this.dropChip(world, blockPos, blockState);
        }
    }

    public boolean isPowered(@NotNull BlockState blockstate, Level world, @NotNull BlockPos pos) {
        val facing = blockstate.getValue(FACING);
        val type = blockstate.getValue(TYPE);

        val signalR = 0 != getFaceSignal(world, pos, facing.getCounterClockWise());
        val signalM = 0 != getFaceSignal(world, pos, facing);
        val signalL = 0 != getFaceSignal(world, pos, facing.getClockWise());

        world.setBlockAndUpdate(
            pos,
            blockstate.setValue(LEFT_INPUT, signalL)
                .setValue(RIGHT_INPUT, signalR)
                .setValue(BOTTOM_INPUT, signalM)
        );
        if (type.logic == null) {
            val frame = Cast.inst(ChipFrameEntity.class, world.getBlockEntity(pos));
            if (frame != null) {
                return BitWiseUtil.get(
                    frame.dynamicLogics,
                    BitWiseUtil.wrap(signalL, signalM, signalR)
                );
            }
            return false;
        }
        return type.logic.apply(signalL, signalM, signalR);
    }

    private int getFaceSignal(Level world, @NotNull BlockPos pos, Direction facing) {
        return getAlternateSignalAt(
            world,
            pos.relative(facing),
            facing
        );
    }

    public void dropChip(Level world, BlockPos pos, BlockState blockState) {
        val type = blockState.getValue(TYPE);
        if (type == ChipType.empty) {
            return;
        }
        Containers.dropItemStack(
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            computeStackForDrop(world, pos, blockState)
        );
        this.updateNeighborsInFront(world, pos, blockState);
    }

    private static ItemStack computeStackForDrop(Level world, BlockPos pos, BlockState blockState) {
        val type = blockState.getValue(TYPE);
        if (type == ChipType.dynamic) {
            val stack = LogicChipsItem.DYNAMIC.get()
                .getDefaultInstance()
                .copy();
            val be = world.getBlockEntity(pos);
            if (be instanceof ChipFrameEntity) {
                val entity = (ChipFrameEntity) be;
                val builder = new StringBuilder(DynamicChip.LOGIC_BITS_SIZE);
                val logic = entity.dynamicLogics;
                for (int i = 0; i < DynamicChip.LOGIC_BITS_SIZE; i++) {
                    builder.append(BitWiseUtil.get(logic, i) ? '1' : '0');
                }
                stack.setHoverName(new TextComponent(builder.toString()));
            }
            return stack;
        }
        return LogicChipsItem.getAll()
            .get(type.toChipName())
            .get()
            .getDefaultInstance()
            .copy();
    }

    @Override
    public @NotNull InteractionResult use(
        BlockState blockState,
        @NotNull Level world,
        @NotNull BlockPos blockPos,
        Player player,
        @NotNull InteractionHand hand,
        @NotNull BlockHitResult hit
    ) {
        val type = blockState.getValue(TYPE);
        val stack = player.getItemInHand(hand);
        val handitem = stack.getItem();
        val isClientSide = world.isClientSide;

        /// NOTE: INSERT ITEM ////////////////////////////////////////////////////////////////////////////////
        if (type == ChipType.empty && handitem instanceof Chip) {
            if (!isClientSide) {
                val newType = ((Chip) handitem).type;

                if (newType == ChipType.dynamic) {
                    val frame = Cast.inst(ChipFrameEntity.class, world.getBlockEntity(blockPos));
                    if (frame != null) {
                        val logicRaw = DynamicChip.readLogicFromName(stack.getHoverName());
                        if (logicRaw == null) {
                            return InteractionResult.PASS;
                        }
                        frame.dynamicLogics = BitWiseUtil.wrap(logicRaw);
                        frame.setChanged();
                    }
                }

                world.setBlock(
                    blockPos,
                    blockState
                        .setValue(TYPE, newType)
                        .setValue(POWERED, this.isPowered(blockState, world, blockPos)),
                    3
                );
                stack.shrink(1);
                world.playSound(null, blockPos, SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(isClientSide);
        }
        /// NOTE: DROP ITEM ////////////////////////////////////////////////////////////////////////////////
        else if (type != ChipType.empty) {
            if (!isClientSide) {
                world.setBlock(
                    blockPos,
                    blockState.setValue(TYPE, ChipType.empty).setValue(POWERED, false),
                    3
                );
                dropChip(world, blockPos, blockState);
                world.playSound(
                    null,
                    blockPos,
                    SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
                );
            }
            return InteractionResult.sidedSuccess(isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        if (!blockState.getValue(POWERED)) {
            return;
        }
        val direction = blockState.getValue(FACING);
        double x = (double) blockPos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
        double y = (double) blockPos.getY() + 0.4D + (random.nextDouble() - 0.5D) * 0.2D;
        double z = (double) blockPos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
        float scale = -5.0F;
        if (random.nextBoolean()) {
            scale /= 16.0F;
            x += (scale * direction.getStepX());
            z += (scale * direction.getStepZ());
        }
        level.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new ChipFrameEntity();
    }
}
