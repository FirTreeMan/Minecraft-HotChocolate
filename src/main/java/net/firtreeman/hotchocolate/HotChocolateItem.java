package net.firtreeman.hotchocolate;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class HotChocolateItem extends Item {
    public HotChocolateItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player) {
            player.getFoodData().eat(pStack.getItem(), pStack, player);
            if (player instanceof ServerPlayer serverplayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, pStack);
                serverplayer.awardStat(Stats.ITEM_USED.get(this));
            }
        }

        for (Pair<MobEffectInstance, Float> pair : pStack.getFoodProperties(pEntityLiving).getEffects()) {
            if (!pLevel.isClientSide && pair.getFirst() != null && pLevel.random.nextFloat() < pair.getSecond()) {
               pEntityLiving.addEffect(new MobEffectInstance(pair.getFirst()));
            }
         }

        if (pEntityLiving instanceof Player && !((Player)pEntityLiving).getAbilities().instabuild) {
            pStack.shrink(1);
        }

        pEntityLiving.gameEvent(GameEvent.EAT);

        return pStack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.DRINK;
}

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }
}
