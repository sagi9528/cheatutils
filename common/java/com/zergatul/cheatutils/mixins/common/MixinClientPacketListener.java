package com.zergatul.cheatutils.mixins.common;

import com.zergatul.cheatutils.common.Events;
import com.zergatul.cheatutils.helpers.MixinClientPacketListenerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {

    @Shadow public abstract Connection getConnection();

    @ModifyVariable(
            at = @At("HEAD"),
            method = "handleLogin(Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;)V",
            argsOnly = true,
            ordinal = 0)
    private ClientboundLoginPacket onModifyLoginPacket(ClientboundLoginPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        return new ClientboundLoginPacket(
                packet.playerId(),
                packet.hardcore(),
                packet.gameType(),
                packet.previousGameType(),
                packet.levels(),
                packet.registryHolder(),
                packet.dimensionType(),
                packet.dimension(),
                packet.seed(),
                packet.maxPlayers(),
                mc.options.renderDistance().get(),
                mc.options.simulationDistance().get(),
                packet.reducedDebugInfo(),
                packet.showDeathScreen(),
                packet.isDebug(),
                packet.isFlat(),
                packet.lastDeathLocation());
    }

    @ModifyVariable(
            at = @At("HEAD"),
            method = "handleSetChunkCacheRadius(Lnet/minecraft/network/protocol/game/ClientboundSetChunkCacheRadiusPacket;)V",
            argsOnly = true,
            ordinal = 0)
    private ClientboundSetChunkCacheRadiusPacket onModifySetChunkCacheRadiusPacket(ClientboundSetChunkCacheRadiusPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        return new ClientboundSetChunkCacheRadiusPacket(mc.options.renderDistance().get());
    }

    @ModifyArg(
            method = "handleDisconnect(Lnet/minecraft/network/protocol/game/ClientboundDisconnectPacket;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;disconnect(Lnet/minecraft/network/chat/Component;)V"),
            index = 0)
    private Component onDisconnect(Component component) {
        if (MixinClientPacketListenerHelper.appendDisconnectMessage == null) {
            return component;
        }

        MutableComponent replacement = MutableComponent.create(component.getContents());
        String message = MixinClientPacketListenerHelper.appendDisconnectMessage;
        MixinClientPacketListenerHelper.appendDisconnectMessage = null;
        return replacement.append("\n").append(message);
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;playerId()I"),
            method = "handleLogin(Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;)V")
    private void onPlayerLoggingIn(ClientboundLoginPacket packet, CallbackInfo info) {
        Events.ClientPlayerLoggingIn.trigger(this.getConnection());
    }

    /*@ModifyVariable(
            at = @At("HEAD"),
            method = "sendChat(Ljava/lang/String;)V",
            argsOnly = true,
            ordinal = 0)
    private String onSendChat(String message) {
        if (message.startsWith("/")) {
            return message;
        }

        String s = "";
        Random rnd = new Random();
        for (int i = 0; i < message.length(); i++) {
            s += "&" + Integer.toHexString(rnd.nextInt(6,16));
            s += message.substring(i, i + 1);
        }

        return s;
    }*/
}