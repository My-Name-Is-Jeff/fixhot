package mynameisjeff.fixhot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    private LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "canUseSlot", at = @At("RETURN"))
    private boolean fixhot$fixArmorSlot$canUseSlot(boolean original, @Local(argsOnly = true) EquipmentSlot equipmentSlot) {
        if ((Object) this instanceof Player player) {
            if (equipmentSlot.isArmor() && !player.isActive(PlayerUnlocks.ARMAMENTS)) return false;
        }
        return original;
    }
}
