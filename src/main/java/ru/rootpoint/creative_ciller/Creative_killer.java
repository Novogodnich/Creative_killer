package ru.rootpoint.creative_ciller;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.concurrent.CompletableFuture;

public class Creative_killer implements ModInitializer {

    @Override
    public void onInitialize() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, ignored) -> {
            if (!world.isClient() && entity instanceof LivingEntity target) {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isOf(Items.NETHERITE_SWORD)) {
                    Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);

                    if (customName != null && "Creative Killer".equals(customName.getString())) {
                        CompletableFuture<Boolean> canKillAll = hasPermission((ServerPlayerEntity) player, "creative-killer.kill");
                        CompletableFuture<Boolean> canKillPlayer = hasPermission((ServerPlayerEntity) player, "creative-killer.kill.player");
                        CompletableFuture<Boolean> canKillMob = hasPermission((ServerPlayerEntity) player, "creative-killer.kill.mob");

                        if (target instanceof PlayerEntity targetPlayer) {
                            if (player.getCommandSource((ServerWorld) world).getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry()) || canKillAll.join() || canKillPlayer.join()) {
                                targetPlayer.kill((ServerWorld) world);
                                targetPlayer.discard();
                                return ActionResult.SUCCESS;
                            }
                        }
                        else {
                            if (player.getCommandSource((ServerWorld) world).getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry()) || canKillAll.join() || canKillMob.join()) {
                                target.kill((ServerWorld) world);
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });

    }

    public CompletableFuture<Boolean> hasPermission(ServerPlayerEntity player, String permission) {
        return Permissions.check(player.getGameProfile(), permission, false);
    }
}
