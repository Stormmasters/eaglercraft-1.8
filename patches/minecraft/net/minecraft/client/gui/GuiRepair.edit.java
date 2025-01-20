
# Eagler Context Redacted Diff
# Copyright (c) 2025 lax1dude. All rights reserved.

# Version: 1.0
# Author: lax1dude

> DELETE  2  @  2 : 4

> INSERT  1 : 6  @  1

+ 
+ import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
+ import net.lax1dude.eaglercraft.v1_8.Keyboard;
+ import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
+ import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

> DELETE  1  @  1 : 2

> DELETE  1  @  1 : 2

> DELETE  12  @  12 : 13

> CHANGE  70 : 71  @  70 : 71

~ 	protected void keyTyped(char parChar1, int parInt1) {

> CHANGE  21 : 22  @  21 : 22

~ 	protected void mouseClicked(int parInt1, int parInt2, int parInt3) {

> INSERT  46 : 61  @  46

+ 
+ 	public boolean blockPTTKey() {
+ 		return nameField.isFocused();
+ 	}
+ 
+ 	@Override
+ 	public boolean showCopyPasteButtons() {
+ 		return nameField.isFocused();
+ 	}
+ 
+ 	@Override
+ 	public void fireInputEvent(EnumInputEvent event, String param) {
+ 		nameField.fireInputEvent(event, param);
+ 	}
+ 

> EOF
