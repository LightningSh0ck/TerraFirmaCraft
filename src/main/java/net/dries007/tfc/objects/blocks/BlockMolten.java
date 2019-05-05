/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class BlockMolten extends Block
{
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 4);

    private static final AxisAlignedBB[] MOLTEN_AABB = new AxisAlignedBB[] {
        new AxisAlignedBB(0, 0, 0, 1, 0.25, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.5, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.75, 1),
        FULL_BLOCK_AABB
    };

    public BlockMolten()
    {
        super(Material.ROCK);

        setDefaultState(this.getBlockState().getBaseState().withProperty(LIT, false).withProperty(LAYERS, 1));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LAYERS, (meta & 0b11) + 1).withProperty(LIT, meta > 3);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LAYERS) + (state.getValue(LIT) ? 4 : 0) - 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return MOLTEN_AABB[state.getValue(LAYERS) - 1];
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return MOLTEN_AABB[blockState.getValue(LAYERS) - 1];
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return MOLTEN_AABB[state.getValue(LAYERS) - 1];
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LIT, LAYERS);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        // NOOP
    }
}
