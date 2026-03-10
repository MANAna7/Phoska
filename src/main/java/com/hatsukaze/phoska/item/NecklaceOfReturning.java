package com.hatsukaze.phoska.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class NecklaceOfReturning extends Item {

    public NecklaceOfReturning() {
        super(new Item.Properties().stacksTo(1));
    }

    // Waystoneを右クリックしたときの登録は
    // 別途イベントハンドラで拾う

    public static void setAnchorPos(ItemStack stack, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("AnchorPos", pos.asLong());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static BlockPos getAnchorPos(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return BlockPos.of(tag.getLong("AnchorPos"));
    }

    public boolean hasAnchor(ItemStack stack) {
        return getAnchorPos(stack) != null;
    }
}