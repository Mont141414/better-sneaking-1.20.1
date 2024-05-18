package mont.bettersneaking.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Objects;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
	@Shadow
	private ClientWorld world;

	protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
		super(client, connection, connectionState);
	}

	@Redirect(method = "onEntityTrackerUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/EntityTrackerUpdateS2CPacket;trackedValues()Ljava/util/List;", ordinal = 0))
	public List<DataTracker.SerializedEntry<?>> RemovePoseFromPackets(EntityTrackerUpdateS2CPacket packet) {
		if (Objects.equals(world.getEntityById(packet.id()), client.player)) {
			packet.trackedValues().removeIf(entry -> entry.handler().equals(TrackedDataHandlerRegistry.ENTITY_POSE));
		}
		return packet.trackedValues();
	}
}
