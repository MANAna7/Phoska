package com.hatsukaze.amuletofreturning.event;

import com.hatsukaze.amuletofreturning.AmuletOfReturningMain;
import com.hatsukaze.amuletofreturning.compat.WaystonesCompat;
import com.hatsukaze.amuletofreturning.compat.WaystoneBindData;
import com.hatsukaze.amuletofreturning.item.AmuletOfReturning;
import com.hatsukaze.amuletofreturning.network.AmuletOfReturningActivatedPayload;
import com.hatsukaze.amuletofreturning.register.ModDataComponentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Set;

@EventBusSubscriber(modid = AmuletOfReturningMain.MODID)
public class UseAmuletOfReturning {
    static Vec3 pos;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // ペンダント装備チェック
        ItemStack pendant = findPendant(player);
        if (pendant.isEmpty()) return;

        // 死亡キャンセルして1HP残す
        event.setCanceled(true);
        player.setHealth(1.0f);

        // --- テレポート先の決定 ---
        boolean teleported = false;

        // 1. Waystone紐付けチェック（Waystones modが入っている場合のみ）
        if (WaystonesCompat.isLoaded()) {
            WaystoneBindData bindData = pendant.get(ModDataComponentRegister.WAYSTONE_BIND.get());
            if (bindData != null) {
                ResourceKey<Level> dimKey = ResourceKey.create(
                        Registries.DIMENSION, bindData.dimension());
                ServerLevel targetLevel = player.getServer().getLevel(dimKey);

                if (targetLevel != null && WaystonesCompat.isWaystoneBlock(targetLevel, bindData.pos())) {
                    BlockPos wp = bindData.pos();
                    player.teleportTo(targetLevel,
                            wp.getX() + 1.5, wp.getY(), wp.getZ() + 0.5,
                            Set.of(), player.getYRot(), player.getXRot());
                    AmuletOfReturningMain.LOGGER.info("Teleported {} to bound Waystone at {}",
                            player.getName().getString(), wp);
                    teleported = true;
                } else {
                    // Waystoneが壊されてた → bindデータ消してフォールバック
                    pendant.remove(ModDataComponentRegister.WAYSTONE_BIND.get());
                    AmuletOfReturningMain.LOGGER.warn("Bound Waystone no longer exists, falling back to respawn");
                }
            }
        }

        // 2. ベッド/リスポーンアンカー
        if (!teleported) {
            BlockPos respawn = player.getRespawnPosition();
            ResourceKey<Level> respawnDim = player.getRespawnDimension();
            ServerLevel respawnLevel = player.getServer().getLevel(respawnDim);

            if (respawnLevel != null && respawn != null) {
                player.teleportTo(respawnLevel,
                        respawn.getX() + 0.5, respawn.getY(), respawn.getZ() + 0.5,
                        Set.of(), player.getYRot(), player.getXRot());
            } else {
                // 3. 初期スポーン地点
                ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
                BlockPos worldSpawn = overworld.getSharedSpawnPos();
                player.teleportTo(overworld,
                        worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5,
                        Set.of(), player.getYRot(), player.getXRot());
            }
        }

        // エフェクト
        if (!player.level().isClientSide) {
            PlayEffect(player);
        }

        // どちらの場合も消費とエフェクト
        pendant.shrink(1);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));

        // 経験値ペナルティ（75%ロスト）
        int totalXp = player.totalExperience;
        player.setExperiencePoints(0);
        player.setExperienceLevels(0);
        player.giveExperiencePoints(totalXp / 4);

        // テレポート系処理が終了後、位置を取得
        pos = player.position();
        player.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        AmuletOfReturningMain.LOGGER.info("sending amulet packet to {}", player.getName().getString());

        PacketDistributor.sendToPlayer(player, new AmuletOfReturningActivatedPayload());
    }

    private static ItemStack findPendant(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(
                        item -> item.getItem() instanceof AmuletOfReturning))
                .flatMap(r -> r)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    private static void PlayEffect(ServerPlayer player) {
        ServerLevel serverLevel = (ServerLevel) player.level();
        for (int i = 0; i < 30; i++) {
            double ox = (player.level().random.nextDouble() - 0.5) * 2.0;
            double oy = player.level().random.nextDouble() * 2.0;
            double oz = (player.level().random.nextDouble() - 0.5) * 2.0;
            serverLevel.sendParticles(
                    ParticleTypes.SOUL,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    10, ox, oy, oz, 0.1
            );
            serverLevel.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    1, ox, oy, oz, 0.1
            );
            serverLevel.sendParticles(
                    ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    4, ox, oy, oz, 0.1
            );
        }
    }
}