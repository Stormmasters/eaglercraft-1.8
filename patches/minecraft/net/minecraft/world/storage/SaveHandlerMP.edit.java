
# Eagler Context Redacted Diff
# Copyright (c) 2025 lax1dude. All rights reserved.

# Version: 1.0
# Author: lax1dude

> CHANGE  2 : 3  @  2 : 3

~ import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

> DELETE  1  @  1 : 2

> DELETE  2  @  2 : 5

> DELETE  6  @  6 : 13

> CHANGE  13 : 19  @  13 : 14

~ 	public String getWorldDirectoryName() {
~ 		return "none";
~ 	}
~ 
~ 	@Override
~ 	public IChunkLoader getChunkLoader(WorldProvider var1) {

> CHANGE  3 : 6  @  3 : 5

~ 	@Override
~ 	public VFile2 getWorldDirectory() {
~ 		return null;

> CHANGE  2 : 4  @  2 : 3

~ 	@Override
~ 	public VFile2 getMapFileFromName(String var1) {

> INSERT  2 : 3  @  2

+ 

> EOF
