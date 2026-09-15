package ru.rootpoint.creative_ciller;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class Creative_ciller implements ModInitializer {

    @Override
    public void onInitialize() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {

            if (!world.isClient() && entity instanceof PlayerEntity targetPlayer) {

                ItemStack stack = player.getStackInHand(hand);

                if (stack.isOf(Items.NETHERITE_SWORD)) {

                    Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);

                    if (customName != null && "Creative Killer".equals(customName.getString())) {

                        if (player.getCommandSource((ServerWorld) world).getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry())) {
                            targetPlayer.discard();
                            return ActionResult.SUCCESS;
                        }

                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
