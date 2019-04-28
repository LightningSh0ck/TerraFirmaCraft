/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;

public interface OreBlockQuantity
{
    int quantityDropped(IBlockState state, int fortune, @Nonnull Random random);

    //lambda will return from 1 to max
    static OreBlockQuantity rng(int max)
    {
        return (state, fortune, random) -> 1 + random.nextInt(max);
    }
}
