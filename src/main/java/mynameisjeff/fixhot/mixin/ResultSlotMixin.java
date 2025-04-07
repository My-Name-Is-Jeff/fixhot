package mynameisjeff.fixhot.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.UnlockCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin extends Slot {
    @Shadow @Final private Player player;

    public ResultSlotMixin(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    @Inject(method = "onQuickCraft", at = @At("RETURN"))
    private void fixhot$fixResultSlot$onQuickCraft(ItemStack itemStack, int i, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            UnlockCondition.onCraftedItem(player.level(), serverPlayer, itemStack);
        }
    }
}
