package ru.rootpoint.creative_ciller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class Creative_killer implements ModInitializer {
    public static Config config;

    @Override
    public void onInitialize() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("creative_killer.json");
        if (configPath.toFile().exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                config = mapper.readValue(configPath.toFile(), Config.class);
            } catch (IOException ignored) {
                config = new Config(false, false);
            }
        }
        else config = new Config(false, false);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, ignored) -> {
            if (config.activeAllMode()) {
                CompletableFuture<Boolean> canKill = hasPermission((ServerPlayer) player, "creative_killer.killWithAnyItem");
                if (!world.isClientSide() && entity instanceof net.minecraft.world.entity.LivingEntity target) {
                    if (!config.permissionsForOpsNeeded()) {
                        MinecraftServer server;
                        if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            server = serverLevel.getServer();
                        } else throw new RuntimeException();

                        if (server.getPlayerList().isOp(player.nameAndId()) || canKill.join()) {

                            if (target instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                                targetPlayer.kill(serverLevel);
                                targetPlayer.discard();
                                return InteractionResult.SUCCESS;
                            } else {
                                target.kill(serverLevel);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                    else {
                        if (!(world instanceof ServerLevel serverLevel)) throw new RuntimeException();

                        if (canKill.join()) {

                            if (target instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                                targetPlayer.kill(serverLevel);
                                targetPlayer.discard();
                                return InteractionResult.SUCCESS;
                            } else {
                                target.kill(serverLevel);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            if (!world.isClientSide() && entity instanceof net.minecraft.world.entity.LivingEntity target) {
                net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);

                if (stack.getItem().equals(net.minecraft.world.item.Items.NETHERITE_SWORD)) {
                    var customName = stack.get(DataComponents.CUSTOM_NAME);

                    if (customName != null && "Creative Killer".equals(customName.getString())) {
                        CompletableFuture<Boolean> canKillAll = hasPermission((ServerPlayer) player, "creative-killer.kill");
                        CompletableFuture<Boolean> canKillPlayer = hasPermission((ServerPlayer) player, "creative-killer.kill.player");
                        CompletableFuture<Boolean> canKillMob = hasPermission((ServerPlayer) player, "creative-killer.kill.mob");

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

    public CompletableFuture<Boolean> hasPermission(@NonNull ServerPlayer player, String permission) {
        return Permissions.check(player.getGameProfile(), permission, false);
    }
}
