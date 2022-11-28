package com.lowdragmc.lowdraglib.utils;

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

/**
 * @author KilaBash
 * @date 2022/6/17
 * @implNote ShapeUtils
 */
public class ShapeUtils {

    public static AxisAlignedBB rotate(AxisAlignedBB AxisAlignedBB, Direction facing) {
        switch (facing) {
            case SOUTH: {
                return rotate(AxisAlignedBB, new Vector3(0, 1, 0), 180);
            }
            case EAST: {
                return rotate(AxisAlignedBB, new Vector3(0, 1, 0), -90);
            }
            case WEST: {
                return rotate(AxisAlignedBB, new Vector3(0, 1, 0), 90);
            }
            case UP: {
                return rotate(AxisAlignedBB, new Vector3(1, 0, 0), 90);
            }
            case DOWN: {
                return rotate(AxisAlignedBB, new Vector3(1, 0, 0), -90);
            }
        }
        return AxisAlignedBB;
    }

    public static AxisAlignedBB rotate(AxisAlignedBB AxisAlignedBB, Vector3 axis, double degree) {
        Vector3 min = new Vector3(AxisAlignedBB.minX, AxisAlignedBB.minY, AxisAlignedBB.minZ).subtract(0.5);
        Vector3 max = new Vector3(AxisAlignedBB.maxX, AxisAlignedBB.maxY, AxisAlignedBB.maxZ).subtract(0.5);
        double radians = Math.toRadians(degree);
        min.rotate(radians, axis);
        max.rotate(radians, axis);
        min.add(0.5);
        max.add(0.5);
        return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static VoxelShape rotate(VoxelShape shape, Direction facing) {
        return shape.toAabbs().stream().map(AxisAlignedBB -> VoxelShapes.create(rotate(AxisAlignedBB, facing))).reduce(VoxelShapes.empty(), VoxelShapes::or);
    }

    public static VoxelShape rotate(VoxelShape shape, Vector3 axis, double degree) {
        return shape.toAabbs().stream().map(AxisAlignedBB -> VoxelShapes.create(rotate(AxisAlignedBB, axis, degree))).reduce(VoxelShapes.empty(), VoxelShapes::or);
    }
}
