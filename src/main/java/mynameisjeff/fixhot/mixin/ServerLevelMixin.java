package mynameisjeff.fixhot.mixin;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntityGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.WorldModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ServerEntityGetter, WorldGenLevel {
    @Shadow public abstract void unlockEffect(WorldEffect worldEffect);

    private ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @WrapOperation(method = "cleanInventoryAndReward", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ItemUsedOnLocationTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void fixhot$fixInventoryAndReward$trigger(ItemUsedOnLocationTrigger instance, ServerPlayer serverPlayer, BlockPos blockPos, ItemStack itemStack, Operation<Void> original, @Local List<ItemStack> list) {
        original.call(instance, serverPlayer, blockPos, itemStack);

        var containerItems = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems();
        var bundleItems = itemStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).items();

        var iterator = Iterables.concat(containerItems, bundleItems);

        for (var stack : iterator) {
            WorldModifiers worldModifiers = stack.get(DataComponents.WORLD_MODIFIERS);
            if (worldModifiers != null) {
                for (WorldEffect worldEffect : worldModifiers.effects()) {
                    if (stack.has(DataComponents.WORLD_EFFECT_UNLOCK)) {
                        this.unlockEffect(worldEffect);
                    }
                }
            } else if (stack.is(ItemTags.CARRY_OVER)) {
                list.add(stack.copy());
            }

            CriteriaTriggers.INVENTORY_CASHED_IN.trigger(serverPlayer, blockPos, stack);
        }
    }
}
