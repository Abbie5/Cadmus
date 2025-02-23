package earth.terrarium.cadmus.mixin.fabric.common.flags;

import earth.terrarium.cadmus.api.claims.ClaimApi;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void place(BlockPlaceContext blockPlaceContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ClaimApi.API.canPlaceBlock(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos(), blockPlaceContext.getPlayer())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
