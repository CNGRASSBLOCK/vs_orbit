package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.force_applier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.cn_good_grass.vs_orbit.procedures.vs_orbit.classes.Force;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public class ForceApplier extends Force {
    @JsonCreator public ForceApplier(@JsonProperty("name") String name, @JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z, @JsonProperty("time") double time) { super(name, x, y, z, time); }

    @JsonIgnore
    public void applyForces(PhysShipImpl physShip) {
        if ((Double.isInfinite(x) || Double.isNaN(x)) || (Double.isInfinite(y) || Double.isNaN(y)) || (Double.isInfinite(z) || Double.isNaN(z))) return;
        physShip.applyInvariantForce(new Vector3d(x, y, z));
    }

}
