
# Eagler Context Redacted Diff
# Copyright (c) 2025 lax1dude. All rights reserved.

# Version: 1.0
# Author: lax1dude

> INSERT  2 : 3  @  2

+ import net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.DynamicLightManager;

> INSERT  55 : 59  @  55

+ 	protected float getEaglerDynamicLightsValueSimple(float partialTicks) {
+ 		return 0.25f;
+ 	}
+ 

> INSERT  150 : 161  @  150

+ 
+ 	protected void renderDynamicLightsEaglerAt(double entityX, double entityY, double entityZ, double renderX,
+ 			double renderY, double renderZ, float partialTicks, boolean isInFrustum) {
+ 		super.renderDynamicLightsEaglerAt(entityX, entityY, entityZ, renderX, renderY, renderZ, partialTicks,
+ 				isInFrustum);
+ 		if (isInFrustum && renderX * renderX + renderY * renderY + renderZ * renderZ < 150.0) {
+ 			float mag = 0.025f;
+ 			DynamicLightManager.renderDynamicLight("entity_" + getEntityId() + "_xp", entityX, entityY + 0.2, entityZ,
+ 					mag * 0.3f, mag, mag * 0.2f, false);
+ 		}
+ 	}

> EOF
