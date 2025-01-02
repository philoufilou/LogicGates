package com.ichphilipp.logicchips.items;

import com.ichphilipp.logicchips.api.TriBoolLogic;
import lombok.val;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * dont change its name unless you can replace all name usage (java/model/?)
 */
public enum ChipType implements StringRepresentable {
    empty((L, B, R) -> false, 0),
    not((L, B, R) -> !B, 1),
    and((L, B, R) -> L && R, 2),
    nand((L, B, R) -> !(L && R), 2),
    or((L, B, R) -> L || R, 2),
    nor((L, B, R) -> !(L || R), 2),
    xor((L, B, R) -> L ^ R, 2),
    xnor((L, B, R) -> L == R, 2),
    and_3((L, B, R) -> L && B && R, 2),
    nand_3((L, B, R) -> !(L && B && R), 3),
    or_3((L, B, R) -> L || B || R, 3),
    nor_3((L, B, R) -> !(L || B || R), 3),
    xor_3((L, B, R) -> ((L ? 1 : 0) + (B ? 1 : 0) + (R ? 1 : 0)) % 2 == 1, 3),
    xnor_3((L, B, R) -> ((L ? 1 : 0) + (B ? 1 : 0) + (R ? 1 : 0)) % 2 == 0, 3),
    dynamic(null, 3),
    ;

    @Nullable
    public final TriBoolLogic logic;
    public final boolean canConnectLeft;
    public final boolean canConnectMid;
    public final boolean canConnectRight;

    ChipType(@Nullable TriBoolLogic logic, int canConnect) {
        this.logic = logic;
        canConnectLeft = canConnect == 2 || canConnect == 3;
        canConnectRight = canConnect == 2 || canConnect == 3;
        canConnectMid = canConnect == 1 || canConnect == 3;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

    public String toChipName() {
        val typeName = this.name().toLowerCase(Locale.ROOT);
        return typeName.endsWith("_3")
            ? typeName.substring(0, typeName.length() - "_3".length()) + "_gate_3" //or_3 -> or_gate_3
            : typeName + "_gate";
    }

    public boolean isDynamic() {
        return logic == null;
    }
}
