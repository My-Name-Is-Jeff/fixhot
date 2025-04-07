package mynameisjeff.fixhot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShieldItem.class)
public abstract class ShieldItemMixin extends Item {
    private ShieldItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    private InteractionResult fixhot$fixShield$use(InteractionResult original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) Player player, @Local(argsOnly = true) InteractionHand interactionHand) {
        // If the original result is SUCCESS, it means it is the original behavior of the shield
        // Is this really a bug? Maybe it's supposed to be a troll like the beds.
        // But then why would they have a starting shield? For the fire shield unlock?
        if (original != InteractionResult.SUCCESS) return original;

        BlocksAttacks blocksAttacks = this.components().get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks != null) {
            player.startUsingItem(interactionHand);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.PASS;
        }
    }
}
