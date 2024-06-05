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
    private static final double[] windDirections = new double[LAYERS];

    public NewWindSavedData(long seed) {
        Random random = new Random(seed);
        windDirections[0] = random.nextDouble() * 360;

        for (int i = 1; i < LAYERS; i++) {
            double prevDir = windDirections[i - 1];
            double minDelta = 60;
            double maxDelta = 180;
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
        return windDirections[normalize(height, level)];
    }

    public int getLayer(int yValue, Level level) {
        return normalize(yValue, level);
    }

    public double[] getWindDirections() {
        return windDirections;
    }

    private int normalize(int height, Level level) {
        int minBuildHeight = level.getMinBuildHeight();
        int maxBuildHeight = level.getMaxBuildHeight();

        if (height > maxBuildHeight || height < minBuildHeight) {
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
