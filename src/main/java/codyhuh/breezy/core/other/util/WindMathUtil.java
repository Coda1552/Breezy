package codyhuh.breezy.core.other.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WindMathUtil {
    public static double stepX(double angle) {
        return Mth.cos((float)(angle * 0.017453292519943295));
    }

    public static double stepZ(double angle) {
        return Mth.sin((float) (angle * 0.017453292519943295));
    }

    public static Vec3 vec3Lerp(Vec3 start, Vec3 end, float t) {
        return new Vec3(
                Mth.lerp(start.x, end.x, t),
                Mth.lerp(start.y, end.y, t),
                Mth.lerp(start.z, end.z, t)
        );
    }
}
