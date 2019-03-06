package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Multiblock implements BiPredicate<World, BlockPos>
{
    private final List<BiFunction<World, BlockPos, Boolean>> conditions;

    public Multiblock()
    {
        this.conditions = new ArrayList<>();
    }

    public Multiblock match(BlockPos posOffset, BiFunction<World, BlockPos, Boolean> condition)
    {
        conditions.add((world, pos) -> condition.apply(world, pos.add(posOffset)));
        return this;
    }

    public Multiblock match(BlockPos posOffset, Predicate<IBlockState> stateMatcher)
    {
        conditions.add((world, pos) -> stateMatcher.test(world.getBlockState(pos.add(posOffset))));
        return this;
    }

    public <T extends TileEntity> Multiblock match(BlockPos posOffset, Predicate<T> tileEntityPredicate, Class<T> teClass)
    {
        conditions.add((world, pos) -> {
            T tile = Helpers.getTE(world, pos.add(posOffset), teClass);
            if (tile != null)
            {
                return tileEntityPredicate.test(tile);
            }
            return false;
        });
        return this;
    }

    public Multiblock matchOneOf(BlockPos baseOffset, Multiblock subMultiblock)
    {
        conditions.add((world, pos) -> {
            for (BiFunction<World, BlockPos, Boolean> condition : subMultiblock.conditions)
            {
                if (condition.apply(world, pos.add(baseOffset)))
                {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    public Multiblock matchAllOf(BlockPos baseOffset, Multiblock subMultiblock)
    {
        conditions.add((world, pos) -> {
            for (BiFunction<World, BlockPos, Boolean> condition : subMultiblock.conditions)
            {
                if (!condition.apply(world, pos.add(baseOffset)))
                {
                    return false;
                }
            }
            return true;
        });
        return this;
    }

    @Override
    public boolean test(World world, BlockPos pos)
    {
        for (BiFunction<World, BlockPos, Boolean> condition : conditions)
        {
            if (!condition.apply(world, pos))
            {
                return false;
            }
        }
        return true;
    }
}
