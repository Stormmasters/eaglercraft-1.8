
# Eagler Context Redacted Diff
# Copyright (c) 2025 lax1dude. All rights reserved.

# Version: 1.0
# Author: lax1dude

> DELETE  2  @  2 : 5

> DELETE  1  @  1 : 2

> INSERT  3 : 13  @  3

+ import java.util.Set;
+ 
+ import com.carrotsearch.hppc.IntObjectHashMap;
+ import com.carrotsearch.hppc.IntObjectMap;
+ import com.carrotsearch.hppc.ObjectIntHashMap;
+ import com.carrotsearch.hppc.ObjectIntMap;
+ import com.carrotsearch.hppc.cursors.IntCursor;
+ import com.google.common.collect.HashMultimap;
+ import com.google.common.collect.Lists;
+ 

> DELETE  6  @  6 : 9

> CHANGE  11 : 13  @  11 : 13

~ 	private IntObjectMap<List<PotionEffect>> effectCache = new IntObjectHashMap<>();
~ 	private static final ObjectIntMap<List<PotionEffect>> SUB_ITEMS_CACHE = new ObjectIntHashMap<>();

> CHANGE  23 : 24  @  23 : 24

~ 			List<PotionEffect> list = this.effectCache.get(stack.getMetadata());

> CHANGE  2 : 3  @  2 : 3

~ 				this.effectCache.put(stack.getMetadata(), list);

> CHANGE  7 : 8  @  7 : 8

~ 		List<PotionEffect> list = this.effectCache.get(meta);

> CHANGE  2 : 3  @  2 : 3

~ 			this.effectCache.put(meta, list);

> CHANGE  11 : 12  @  11 : 12

~ 			List<PotionEffect> list = this.getEffects(itemstack);

> CHANGE  1 : 3  @  1 : 3

~ 				for (int i = 0, l = list.size(); i < l; ++i) {
~ 					entityplayer.addPotionEffect(new PotionEffect(list.get(i)));

> CHANGE  56 : 57  @  56 : 57

~ 		List<PotionEffect> list = this.getEffects(meta);

> CHANGE  1 : 3  @  1 : 3

~ 			for (int i = 0, l = list.size(); i < l; ++i) {
~ 				if (Potion.potionTypes[list.get(i).getPotionID()].isInstant()) {

> CHANGE  33 : 34  @  33 : 34

~ 			List<PotionEffect> list1 = Items.potionitem.getEffects(itemstack);

> CHANGE  2 : 4  @  2 : 3

~ 				for (int i = 0, l = list1.size(); i < l; ++i) {
~ 					PotionEffect potioneffect = list1.get(i);

> CHANGE  4 : 5  @  4 : 5

~ 						for (Entry entry : (Set<Entry<Object, Object>>) map.entrySet()) {

> CHANGE  33 : 34  @  33 : 34

~ 				for (Entry entry1 : (Set<Entry<Object, Object>>) hashmultimap.entries()) {

> CHANGE  56 : 57  @  56 : 57

~ 							SUB_ITEMS_CACHE.put(list, i1);

> CHANGE  6 : 8  @  6 : 11

~ 		for (IntCursor cur : SUB_ITEMS_CACHE.values()) {
~ 			subItems.add(new ItemStack(itemIn, 1, cur.value));

> EOF
