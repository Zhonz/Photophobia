package com.zzandbrokensnake.photophobia.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import com.zzandbrokensnake.photophobia.PhotophobiaMod;
import com.zzandbrokensnake.photophobia.entity.GhostEntity;

public class GhostEntityRenderer extends MobEntityRenderer<GhostEntity, BipedEntityModel<GhostEntity>> {
    private static final Identifier TEXTURE = new Identifier(PhotophobiaMod.MOD_ID, "textures/entity/ghost.png");

    public GhostEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(GhostEntity entity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(GhostEntity entity) {
        // 幽灵在附身时会有抖动效果
        return entity.isPossessing();
    }
}
