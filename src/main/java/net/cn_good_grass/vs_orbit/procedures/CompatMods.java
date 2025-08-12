package net.cn_good_grass.vs_orbit.procedures;

import net.minecraftforge.fml.ModList;

public enum CompatMods {
	COMPUTERCRAFT("computercraft"),
	CREATE("create"),
	MEKANISM("mekanism");

	private final String modId;

	CompatMods(String modId) {
		this.modId = modId;
	}

	public boolean isLoaded() {
		return ModList.get().isLoaded(asId());
	}

	public String asId() {
		return modId;
	}
}
