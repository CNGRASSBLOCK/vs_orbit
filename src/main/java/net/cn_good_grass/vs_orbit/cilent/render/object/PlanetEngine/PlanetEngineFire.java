package net.cn_good_grass.vs_orbit.cilent.render.object.PlanetEngine;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PlanetEngineFire {
    public static final List<PlanetEngineFire> fires_server = new ArrayList<>();
    public static final List<PlanetEngineFire> fires_cilent = new ArrayList<>();

    public BlockPos blockPos;
    public String WorldId;
    public int r;
    public int h;

    public PlanetEngineFire(BlockPos blockPos, String WorldId, int r, int h) {
        this.blockPos = blockPos;
        this.WorldId = WorldId;
        this.r = r;
        this.h = h;
    }
    
    @Override public String toString() { return "{" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ() + "}，" + WorldId + "，" + r + "，" + h; }
    @Override public boolean equals(Object obj) { return (obj instanceof PlanetEngineFire && this.toString().equals(obj.toString())); }
    public static boolean has(PlanetEngineFire planetEngineFire) {
        for (PlanetEngineFire planetEngineFires : PlanetEngineFire.fires_server) if (planetEngineFires.blockPos.equals(planetEngineFire.blockPos)) return true;
        return false;
    }
    public static int indexOf(PlanetEngineFire planetEngineFire) {
        for (int i = 0;i < PlanetEngineFire.fires_server.size();i++) if (PlanetEngineFire.fires_server.get(i).blockPos.equals(planetEngineFire.blockPos)) return i;
        return -1;
    }
}
