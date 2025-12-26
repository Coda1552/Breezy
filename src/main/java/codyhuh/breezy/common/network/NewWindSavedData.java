package codyhuh.breezy.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Random;

public class NewWindSavedData extends SavedData {
    private static final int LAYERS = 12;
    final int BLEND_RADIUS = 12;
    private static final double[] windDirections = new double[LAYERS];

    public NewWindSavedData(long seed) {
        Random random = new Random(seed);
        windDirections[0] = random.nextDouble() * 360;

        for (int i = 1; i < LAYERS; i++) {
            double prevDir = windDirections[i - 1];
            double minDelta = 60;
            double maxDelta = 120;
            double dirDelta = minDelta + ((maxDelta - minDelta) * random.nextDouble());
            if (random.nextBoolean()) {
                windDirections[i] = (prevDir + dirDelta) % 360;
            } else {
                windDirections[i] = (prevDir - dirDelta + 360) % 360;
            }
        }
    }

    public NewWindSavedData(CompoundTag tag) {
        ListTag listTag = tag.getList("Directions", Tag.TAG_DOUBLE);
        for (int i = 0; i < listTag.size(); i++) {
            windDirections[i] = listTag.getDouble(i);
        }
    }

    public static void resetWindDirections(int timePassed, double percentage) {
        Random random = new Random(timePassed);
        double maxChange = 360 * percentage;

        for (int i = 0; i < LAYERS; i++) {
            double change = (random.nextDouble() * 2 - 1) * maxChange;
            windDirections[i] = (windDirections[i] + change + 360) % 360;
        }
    }

    public double getWindAtHeight(int height, Level level) {
        int idx = normalize(height, level);
        // get the wind direction for current layer, calculate layer height, boundaries of current layer
        double currentAngle = windDirections[idx];
        double layerHeight = (double) (level.getMaxBuildHeight() + 32 - (level.getMinBuildHeight() - 32)) / LAYERS;
        double layerStartY = level.getMinBuildHeight() - 32 + (idx * layerHeight);
        double layerEndY = layerStartY + layerHeight;

        // calc dist from current height to boundaries, determine if blending possible
        double distToLowerBoundary = height - layerStartY;
        double distToUpperBoundary = layerEndY - height;
        boolean canBlendLower = distToLowerBoundary <= BLEND_RADIUS;
        boolean canBlendUpper = distToUpperBoundary <= BLEND_RADIUS;

        if (canBlendLower || canBlendUpper) {
            // blend with lower layer or upper layer, obtain relevant angle
            boolean chooseLower = blendBelowOrNot(canBlendLower, canBlendUpper, distToLowerBoundary, distToUpperBoundary);
            double neighborAngle = getNeighborWindAngle(idx, chooseLower);

            // calculate blend factor as a percentage, return wind
            double dist = chooseLower ? distToLowerBoundary : distToUpperBoundary;
            double blendFactor = calcBlendPercentile(dist, BLEND_RADIUS);

            return interpolateWindAngle(currentAngle, neighborAngle, blendFactor);
        }

        return windDirections[idx];
    }

    private boolean blendBelowOrNot(boolean canBlendLower, boolean canBlendUpper, double distToLowerBoundary, double distToUpperBoundary) {
        if (canBlendLower && canBlendUpper) {
            return distToLowerBoundary < distToUpperBoundary;
        }
        return canBlendLower;
    }

    private double getNeighborWindAngle(int idx, boolean chooseLower) {
        if (chooseLower) {
            return windDirections[Math.max(0, idx - 1)]; // Blend with lower layer
        } else {
            return windDirections[Math.min(LAYERS - 1, idx + 1)]; // Blend with upper layer
        }
    }

    private double calcBlendPercentile(double dist, int blendRadius) {
        double t = (blendRadius - dist) / (double) blendRadius;
        return Mth.clamp(t, 0.0D, 1.0D);
    }

    private double interpolateWindAngle(double currentAngle, double neighborAngle, double blendFactor) {
        double diff = neighborAngle - currentAngle;
        if (diff > 180.0) diff -= 360.0;
        if (diff <= -180.0) diff += 360.0;

        double interp = currentAngle + (diff * blendFactor * 0.5);
        return Mth.positiveModulo(interp, 360.0D);
    }

    public int getLayer(int yValue, Level level) {
        return normalize(yValue, level);
    }

    public double[] getWindDirections() {
        return windDirections;
    }

    private int normalize(int height, Level level) {
        int minBuildHeight = level.getMinBuildHeight() - 32;
        int maxBuildHeight = level.getMaxBuildHeight() + 32;

        if (height >= maxBuildHeight || height <= minBuildHeight) {
            return 0;
        }

        return ((height - minBuildHeight) * LAYERS) / (maxBuildHeight - minBuildHeight);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag directionsTag = new ListTag();
        tag.put("Directions", directionsTag);
        for (double dir : windDirections) {
            directionsTag.add(DoubleTag.valueOf(dir));
        }
        return tag;
    }
}
