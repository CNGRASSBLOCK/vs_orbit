package net.cn_good_grass.vs_orbit.procedures.valkyrienskies.thruster;

import net.minecraft.util.StringRepresentable;
import org.joml.Vector3d;

public class ThrusterData {

	public enum ThrusterMode implements StringRepresentable  {
		POSITION("position"),
		GLOBAL("global");

		private final String name;

		// Constructor that takes a string parameter
		ThrusterMode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}


		public ThrusterMode toggle() {
			return this == POSITION ? GLOBAL : POSITION;
		}
	}

	public final Vector3d dir;
	public volatile double throttle;
	public volatile ThrusterMode mode;

	public ThrusterData(Vector3d dir, double throttle, ThrusterMode mode) {
		this.dir = dir;
		this.throttle = throttle;
		this.mode = mode;
	}

	public String toString() {
		return "Direction: " + this.dir + " Throttle: " + this.throttle + " Mode: " + this.mode;
	}
}
