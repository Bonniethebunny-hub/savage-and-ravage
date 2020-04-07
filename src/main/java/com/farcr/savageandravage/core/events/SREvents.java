package com.farcr.savageandravage.core.events;

import com.farcr.savageandravage.common.entity.CreeperSporeCloudEntity;
import com.farcr.savageandravage.common.entity.CreepieEntity;
import com.farcr.savageandravage.common.entity.SkeletonVillagerEntity;
import com.farcr.savageandravage.common.entity.goals.ImprovedCrossbowGoal;
import com.farcr.savageandravage.core.registry.SREntities;
import com.farcr.savageandravage.core.registry.SRItems;

import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SREvents
{
	@SubscribeEvent
	public static void onLivingSpawned(EntityJoinWorldEvent event) 
	{
		if (event.getEntity() instanceof PillagerEntity) 
		{
			PillagerEntity pillager = (PillagerEntity)event.getEntity();
			ImprovedCrossbowGoal<PillagerEntity> aiCrossBow = new ImprovedCrossbowGoal<PillagerEntity>(pillager, 1.0D, 8.0F, 9.0D);
			 pillager.goalSelector.goals.stream().map(it -> it.inner).filter(it -> it instanceof RangedCrossbowAttackGoal<?>)
              .findFirst().ifPresent(crossbowGoal -> {
     		    pillager.goalSelector.removeGoal(crossbowGoal);  
     		    pillager.goalSelector.addGoal(3, aiCrossBow);
           });
		}
		
		if (event.getEntity() instanceof VillagerEntity)  
		{
		   VillagerEntity villager = (VillagerEntity)event.getEntity();
		   villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, SkeletonVillagerEntity.class, 15.0F, 0.5D, 0.5D));
		}
		
		if (event.getEntity() instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)event.getEntity();
			creeper.explosionRadius = 0;
		}
	}
	
	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event)
	{
		if (event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity && !(event.getExplosion().getExplosivePlacedBy() instanceof CreepieEntity))
		{
			CreeperEntity creeper = (CreeperEntity)event.getExplosion().getExplosivePlacedBy();
			creeper.entityDropItem(new ItemStack(SRItems.CREEPER_SPORES.get(), 1 + creeper.world.rand.nextInt(11)));
			CreeperSporeCloudEntity spores = new CreeperSporeCloudEntity(SREntities.CREEPER_SPORE_CLOUD.get(), event.getWorld());
			spores.copyLocationAndAnglesFrom(creeper);
			//spores.size = (int) Math.round(creeper.getHealth()); //spawns way too many creepies right now, i will attempt to make em spawn less but its 10 pm here
			creeper.world.addEntity(spores);
		}
	}

}
