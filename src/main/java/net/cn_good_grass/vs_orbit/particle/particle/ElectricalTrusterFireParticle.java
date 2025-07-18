package net.cn_good_grass.vs_orbit.particle.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectricalTrusterFireParticle extends TextureSheetParticle {
    protected ElectricalTrusterFireParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteSet) {
        super(level, x, y, z, xd, yd, zd);
        this.spriteSet = spriteSet;
        this.pickSprite(spriteSet);

        this.friction = 0.98F; // 摩擦力
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 0.75F; // 粒子大小
        this.lifetime = 20; // 粒子存在时间(ticks)

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    private final SpriteSet spriteSet;
    public static class ElectricalTrusterFireParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public ElectricalTrusterFireParticleProvider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ElectricalTrusterFireParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
    public static ElectricalTrusterFireParticleProvider provider(SpriteSet spriteSet) { return new ElectricalTrusterFireParticleProvider(spriteSet); }

    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_OPAQUE; }
    @Override  public void tick() { super.tick(); }
}