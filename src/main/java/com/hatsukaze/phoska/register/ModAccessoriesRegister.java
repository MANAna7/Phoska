package com.hatsukaze.phoska.register;

import com.hatsukaze.phoska.Phoska;
import com.hatsukaze.phoska.item.NecklaceOfReturning;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModAccessoriesRegister {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, Phoska.MODID);

    public static final Supplier<Item> RETURN_NECKLACE =
            register("necklace_of_returning", NecklaceOfReturning::new);

    //Supplier　ファクトリークラス
    private static <T extends Item> Supplier<T> register(
            String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
