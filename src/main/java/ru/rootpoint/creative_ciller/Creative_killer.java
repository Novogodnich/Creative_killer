package ru.rootpoint.creative_ciller;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;

public class Creative_killer implements ModInitializer {

    @Override
    public void onInitialize() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, ignored) -> {
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

                            if (server.getPlayerList().isOp(player.nameAndId())) {
                                targetPlayer.discard();
                                return InteractionResult.SUCCESS;
                            }
                        }
                        else {
                            if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                target.kill(serverLevel);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });


    }
}
