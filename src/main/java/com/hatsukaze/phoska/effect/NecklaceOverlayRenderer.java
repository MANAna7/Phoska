package com.hatsukaze.phoska.effect;

import com.hatsukaze.phoska.Phoska;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import static com.hatsukaze.phoska.network.ModClientPayloadHandler.overlayTicks;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Phoska.MODID, value = Dist.CLIENT)
public class NecklaceOverlayRenderer {

    // ── オーバーレイ設定 ──────────────────
    private static final int   OVERLAY_DURATION   = 208; // 全体フレーム数
    private static final int   PHASE_IN_END       = 88;  // 登場終わり
    private static final int   PHASE_HOLD_END     = 120;  // 慣性終わり
    private static final float ROTATION_SPEED     = 15.0f; // Y軸回転速度
    private static final float MAX_SCALE          = 2.5f;
    // ─────────────────────────────────────

    private static float easeOut(float t) { return 1.0f - (1.0f - t) * (1.0f - t); }
    private static float easeIn(float t)  { return t * t; }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (overlayTicks <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Phoska.MODID, "textures/item/necklace_of_returning.png"
        );

        int elapsed = OVERLAY_DURATION - overlayTicks; // 経過フレーム

        float scale;
        float rotY;

        if (elapsed < PHASE_IN_END) {
            // フェーズ1：登場
            float t = elapsed / (float) PHASE_IN_END;
            scale = 0.5f + MAX_SCALE * easeOut(t);
            rotY = elapsed * ROTATION_SPEED;

        } else if (elapsed < PHASE_HOLD_END) {
            // フェーズ2：慣性で止まる
            float t = (elapsed - PHASE_IN_END) / (float)(PHASE_HOLD_END - PHASE_IN_END);
            scale = MAX_SCALE;
            rotY = PHASE_IN_END * ROTATION_SPEED * (1.0f - easeIn(t));

        } else {
            // フェーズ3：逆再生
            float t = (elapsed - PHASE_HOLD_END) / (float)(OVERLAY_DURATION - PHASE_HOLD_END);
            scale = MAX_SCALE * (1.0f - easeIn(t));
            rotY = -(elapsed - PHASE_HOLD_END) * ROTATION_SPEED; // 逆回転
        }

        float alpha = (elapsed < PHASE_HOLD_END)
                ? elapsed / (float) PHASE_IN_END  // フェードイン
                : 1.0f - (elapsed - PHASE_HOLD_END) / (float)(OVERLAY_DURATION - PHASE_HOLD_END); // フェードアウト

        graphics.pose().pushPose();
        graphics.pose().translate(screenW / 2.0f, screenH / 2.0f, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().mulPose(Axis.YP.rotationDegrees(rotY));

        int size = 64;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        graphics.blit(
                texture,
                -size / 2, -size / 2, // 中央基準
                0, 0,
                size, size,
                size, size
        );
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // リセット必須

        graphics.pose().popPose();
        overlayTicks--;
    }
}