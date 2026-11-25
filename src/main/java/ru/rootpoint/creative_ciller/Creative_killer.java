package ru.rootpoint.creative_ciller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Creative_killer implements ModInitializer {
    public static Config config;
    public static final ModMetadata metadata = FabricLoader.getInstance().getModContainer("creative_killer").get().getMetadata();

    @Override
    public void onInitialize() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("creative_killer.json");
        if (configPath.toFile().exists()) {
            final ObjectMapper mapper = new ObjectMapper();
            try {
                config = mapper.readValue(configPath.toFile(), Config.class);
            } catch (IOException ignored) {
                config = new Config(false, true);
                LogService.error("Error reading config file. Using default values.");
            }
        }
        else {
            config = new Config(false, true);
            LogService.warning("Config file not found. Using default values.");
        }

        AttackEntityCallback.EVENT.register((player, world, hand, entity, ignored) -> {
            if (config.activeAllMode()) {
                final CompletableFuture<Boolean> canKill = hasPermission((ServerPlayer) player, "creative_killer.killWithAnyItem");
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
                                LogService.info(player.getName() + " killed " + targetPlayer.getName() + " without Creative Killer");
                                return InteractionResult.SUCCESS;
                            } else {
                                target.kill(serverLevel);
                                LogService.info(player.getName() + " killed entity " + target.getName() + " without Creative Killer");
                                return InteractionResult.SUCCESS;
                            }
                        } else LogService.info(player.getName() + " does not have rights to kill " + target.getName() + " without Creative Killer");
                    }
                    else {
                        if (!(world instanceof ServerLevel serverLevel)) throw new RuntimeException();

                        if (canKill.join()) {

                            if (target instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                                targetPlayer.kill(serverLevel);
                                targetPlayer.discard();
                                LogService.info(player.getName() + " killed " + targetPlayer.getName() + " without Creative Killer");
                                return InteractionResult.SUCCESS;
                            } else {
                                target.kill(serverLevel);
                                LogService.info(player.getName() + " killed entity " + target.getName() + " without Creative Killer");
                                return InteractionResult.SUCCESS;
                            }
                        } else LogService.info(player.getName() + " does not have rights to kill " + target.getName() + " without Creative Killer");
                    }
                }
            }

            if (!world.isClientSide() && entity instanceof net.minecraft.world.entity.LivingEntity target) {
                net.minecraft.world.item.ItemStack stack = player.getItemInHand(hand);

                if (stack.getItem().equals(net.minecraft.world.item.Items.NETHERITE_SWORD)) {
                    var customName = stack.get(DataComponents.CUSTOM_NAME);

                    if (customName != null && "Creative Killer".equals(customName.getString())) {
                        final CompletableFuture<Boolean> canKillAll = hasPermission((ServerPlayer) player, "creative-killer.kill");
                        final CompletableFuture<Boolean> canKillPlayer = hasPermission((ServerPlayer) player, "creative-killer.kill.player");
                        final CompletableFuture<Boolean> canKillMob = hasPermission((ServerPlayer) player, "creative-killer.kill.mob");

                        if (target instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                            MinecraftServer server;

                            if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                server = serverLevel.getServer();
                            } else throw new RuntimeException();

                            if (server.getPlayerList().isOp(player.nameAndId()) || canKillAll.join() || canKillPlayer.join()) {
                                targetPlayer.kill(serverLevel);
                                targetPlayer.discard();
                                LogService.info(player.getName() + " killed " + targetPlayer.getName() + " with Creative Killer");
                                return InteractionResult.SUCCESS;
                            } else LogService.info(player.getName() + " does not have rights to kill " + targetPlayer.getName() + " with Creative Killer");
                        }
                        else {
                            if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                MinecraftServer server = serverLevel.getServer();
                                if (server.getPlayerList().isOp(player.nameAndId()) || canKillAll.join() || canKillMob.join()) {
                                    target.kill(serverLevel);
                                    LogService.info(player.getName() + " killed entity " + target.getName() + " with Creative Killer");
                                    return InteractionResult.SUCCESS;
                                } else LogService.info(player.getName() + " does not have rights to kill entity " + target.getName() + " with Creative Killer");
                            }
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });

        try (final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            final HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://root-point.ru/api/latest-version?mod=CK")).build();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LogService.error("Error getting latest version: Wrong response code(" + response.statusCode() + ")");
            } else {
                final ObjectMapper mapper = new ObjectMapper();
                Map<String, String> latestVersions = mapper.readValue(response.body(), new TypeReference<>() {});

                final String expectedVersion = FabricLoader.getInstance().getModContainer("minecraft").map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("unknown");

                String latest = latestVersions.get(expectedVersion);
                if (latest != null && !latest.equals(metadata.getVersion().getFriendlyString())) {
                    LogService.warning("Not latest version of mod. Latest: " + latest + ", current: " + metadata.getVersion().getFriendlyString());
                }
            }
        } catch (IOException | InterruptedException e) {
            LogService.error("Error getting latest version: " + e.getMessage());
        }
    }

    public CompletableFuture<Boolean> hasPermission(@NonNull ServerPlayer player, String permission) {
        return Permissions.check(player.getGameProfile(), permission, false);
    }
}
