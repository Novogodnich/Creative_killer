package ru.rootpoint.creative_ciller;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

import java.util.concurrent.CompletableFuture;

public class Creative_killer implements ModInitializer {

    @Override
    public void onInitialize() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, ignored) -> {
            CompletableFuture<Boolean> canKillAll = hasPermission((ServerPlayer) player, "creative-killer.kill");
            CompletableFuture<Boolean> canKillPlayer = hasPermission((ServerPlayer) player, "creative-killer.kill.player");
            CompletableFuture<Boolean> canKillMob = hasPermission((ServerPlayer) player, "creative-killer.kill.mob");

            if (!world.isClientSide() && entity instanceof net.minecraft.world.entity.LivingEntity target) {
                net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);

                if (stack.getItem().equals(net.minecraft.world.item.Items.NETHERITE_SWORD)) {
                    var customName = stack.get(DataComponents.CUSTOM_NAME);

                    if (customName != null && "Creative Killer".equals(customName.getString())) {

                        if (target instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                            MinecraftServer server;

                            if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                server = serverLevel.getServer();
                            } else throw new RuntimeException();

                            if (server.getPlayerList().isOp(player.nameAndId()) || canKillAll.join() || canKillPlayer.join()) {
                                targetPlayer.kill(serverLevel);
                                targetPlayer.discard();
                                return InteractionResult.SUCCESS;
                            }
                        }
                        else {
                            if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                MinecraftServer server = serverLevel.getServer();
                                if (server.getPlayerList().isOp(player.nameAndId()) || canKillAll.join() || canKillMob.join()) {
                                    target.kill(serverLevel);
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });


    }

    public CompletableFuture<Boolean> hasPermission(ServerPlayer player, String permission) {
        return Permissions.check(player.getGameProfile(), permission, false);
    }
}
