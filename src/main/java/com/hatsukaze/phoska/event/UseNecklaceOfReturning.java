package com.hatsukaze.phoska.event;

import com.hatsukaze.phoska.Phoska;
import com.hatsukaze.phoska.item.NecklaceOfReturning;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Set;

@EventBusSubscriber(modid = Phoska.MODID)
public class UseNecklaceOfReturning {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // ペンダント装備チェック
        ItemStack pendant = findPendant(player);
        if (pendant.isEmpty()) return;

        // 死亡キャンセルして1HP残す
        event.setCanceled(true);
        player.setHealth(1.0f);

        // ベッドにテレポート
        BlockPos respawn = player.getRespawnPosition();
        ResourceKey<Level> respawnDim = player.getRespawnDimension();
        ServerLevel respawnLevel = player.getServer().getLevel(respawnDim);

        if (respawnLevel != null && respawn != null) {
            player.teleportTo(respawnLevel,
                    respawn.getX() + 0.5,
                    respawn.getY(),
                    respawn.getZ() + 0.5,
                    Set.of(), player.getYRot(), player.getXRot());
        } else {
            // ベッドなし→初期スポーン地点
            ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
            BlockPos worldSpawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld,
                    worldSpawn.getX() + 0.5,
                    worldSpawn.getY(),
                    worldSpawn.getZ() + 0.5,
                    Set.of(), player.getYRot(), player.getXRot());
        }

        // どちらの場合も消費とエフェクト
        pendant.shrink(1);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));

    }

    private static ItemStack findPendant(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(
                        item -> item.getItem() instanceof NecklaceOfReturning))
                .flatMap(r -> r)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }
}
