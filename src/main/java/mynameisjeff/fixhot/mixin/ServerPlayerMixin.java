package mynameisjeff.fixhot.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Unique
    private final DamageSource fixhot$soulLinkDamageSource = this.damageSources().source(DamageTypes.MAGIC);

    private ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @WrapOperation(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean fixhot$stopInfiniteSoulLink(ServerPlayer instance, ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Boolean> original, @Local(argsOnly = true) DamageSource causingDamageSource) {
        if (causingDamageSource == this.fixhot$soulLinkDamageSource) {
            return false;
        }
        return original.call(instance, serverLevel, damageSource.is(DamageTypes.MAGIC) ? fixhot$soulLinkDamageSource : damageSource, f);
    }
}
