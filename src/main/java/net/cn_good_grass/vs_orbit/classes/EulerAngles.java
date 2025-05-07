package net.cn_good_grass.vs_orbit.classes;

import org.joml.Quaterniondc;

// 欧拉角类
public class EulerAngles {
    public final double yaw;    // 偏航角（绕Y轴）
    public final double pitch;  // 俯仰角（绕X轴）
    public final double roll;   // 滚动角（绕Z轴）

    public EulerAngles(double yaw, double pitch, double roll) { this.yaw = yaw; this.pitch = pitch; this.roll = roll; }

    @Override public String toString() { return String.format("Yaw: %.2f°, Pitch: %.2f°, Roll: %.2f°",  Math.toDegrees(yaw), Math.toDegrees(pitch), Math.toDegrees(roll)); }

    public static EulerAngles toEulerAngles(Quaterniondc q) {
        double w = q.w();
        double x = q.x();
        double y = q.y();
        double z = q.z();

        // 计算yaw (绕Y轴旋转)
        double sinr_cosp = 2 * (w * z + x * y);
        double cosr_cosp = 1 - 2 * (y * y + z * z);
        double roll = Math.atan2(sinr_cosp, cosr_cosp);

        // 计算pitch (绕X轴旋转)
        double sinp = 2 * (w * y - z * x);
        double pitch;
        if (Math.abs(sinp) >= 1) {
            pitch = Math.copySign(Math.PI / 2, sinp); // 使用90度，符号与sinp相同
        } else {
            pitch = Math.asin(sinp);
        }

        // 计算yaw (绕Z轴旋转)
        double siny_cosp = 2 * (w * x + y * z);
        double cosy_cosp = 1 - 2 * (x * x + y * y);
        double yaw = Math.atan2(siny_cosp, cosy_cosp);

        return new EulerAngles(yaw, pitch, roll);
    }
}
