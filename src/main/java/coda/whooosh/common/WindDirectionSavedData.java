package coda.whooosh.common;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Random;

public class WindDirectionSavedData extends SavedData {
    private static final int INTERVAL = 12;
    private final Direction[] directions = new Direction[INTERVAL];

    public WindDirectionSavedData(Random random) {
        for (int i = 0; i < INTERVAL; ++i) {
            directions[i] = Direction.from2DDataValue(random.nextInt(4));
        }
    }

    public WindDirectionSavedData(CompoundTag tag) {
        ListTag listTag = tag.getList("Directions", Tag.TAG_INT);

        for (int i = 0; i < listTag.size(); i++) {
            directions[i] = Direction.from2DDataValue(listTag.getInt(i));
        }
    }

    public Direction getWindDirection(int height, Level level) {
        return directions[normalize(height, level)];
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag directionsTag = new ListTag();
        tag.put("Directions", directionsTag);
        for (Direction dir : directions) {
            directionsTag.add(IntTag.valueOf(dir.get2DDataValue()));
        }
        return tag;
    }

    private int normalize(int height, Level level) {
        return ((height - level.getMinBuildHeight()) * INTERVAL) / (level.getMaxBuildHeight() - level.getMinBuildHeight());
    }
}