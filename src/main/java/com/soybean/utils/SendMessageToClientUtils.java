package com.soybean.utils;

import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * @author soybean
 * @date 2024/9/26 16:53
 * @description
 */
public class SendMessageToClientUtils {
    // 方法1：发送聊天消息
    public static void sendChatMessage(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.of(message));
    }

    // 方法2：发送动作栏消息（类似于原本想要的效果）
    public static void sendActionBarMessage(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.of(message), true);
    }

    // 方法3：发送覆盖消息（最接近原本的客户端代码效果）
    public static void sendOverlayMessage(ServerPlayerEntity player, String message) {
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of(message)));
    }

    // 方法4：发送标题
    public static void sendTitle(ServerPlayerEntity player, String title, String subtitle) {
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.of(title)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of(subtitle)));
    }
}
