package codyhuh.breezy.core.mixin;

import codyhuh.breezy.common.util.MixinUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void animateWind(int x, int y, int z, int something, RandomSource random, Block block,
                            BlockPos.MutableBlockPos pos, CallbackInfo ci,
                            int i, int j, int k, BlockState blockstate, FluidState fluidstate) {
        ClientLevel level = (ClientLevel)(Object) this;
        MixinUtil.tryAddWindParticle(level, new BlockPos(i, j, k), random);
    }
}
