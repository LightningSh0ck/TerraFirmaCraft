package net.dries007.tfc.objects.blocks.devices;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEBellows;
import net.dries007.tfc.objects.te.TEBlastFurnace;
import net.dries007.tfc.objects.te.TEMetalSheet;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IBellowsConsumerBlock;
import net.dries007.tfc.util.Multiblock;

@ParametersAreNonnullByDefault
public class BlockBlastFurnace extends Block implements IBellowsConsumerBlock
{
    private static final Multiblock BLAST_FURNACE_CHIMNEY;

    static
    {
        Predicate<IBlockState> stoneMatcher = state -> state.getMaterial() == Material.ROCK && state.isNormalCube();
        BLAST_FURNACE_CHIMNEY = new Multiblock()
            .match(new BlockPos(0, 0, 0), state -> state.getBlock() == BlocksTFC.SLAG || state.getBlock() == Blocks.AIR)
            .match(new BlockPos(0, 0, 1), stoneMatcher)
            .match(new BlockPos(0, 0, 2), tile -> tile.getFace(EnumFacing.NORTH), TEMetalSheet.class)
            .match(new BlockPos(0, 0, -1), stoneMatcher)
            .match(new BlockPos(0, 0, -2), tile -> tile.getFace(EnumFacing.SOUTH), TEMetalSheet.class)
            .match(new BlockPos(1, 0, 0), stoneMatcher)
            .match(new BlockPos(2, 0, 0), tile -> tile.getFace(EnumFacing.WEST), TEMetalSheet.class)
            .match(new BlockPos(1, 0, 0), stoneMatcher)
            .match(new BlockPos(2, 0, 0), tile -> tile.getFace(EnumFacing.EAST), TEMetalSheet.class)
            .match(new BlockPos(1, 0, 1), tile -> tile.getFace(EnumFacing.NORTH) && tile.getFace(EnumFacing.WEST), TEMetalSheet.class)
            .match(new BlockPos(-1, 0, 1), tile -> tile.getFace(EnumFacing.SOUTH) && tile.getFace(EnumFacing.WEST), TEMetalSheet.class)
            .match(new BlockPos(1, 0, -1), tile -> tile.getFace(EnumFacing.NORTH) && tile.getFace(EnumFacing.EAST), TEMetalSheet.class)
            .match(new BlockPos(-1, 0, -1), tile -> tile.getFace(EnumFacing.SOUTH) && tile.getFace(EnumFacing.EAST), TEMetalSheet.class);
    }

    public static int getChimneyLevels(World world, BlockPos pos)
    {
        if (world.getBlockState(pos.down()).getBlock() != BlocksTFC.CRUCIBLE)
        {
            // no crucible
            return 0;
        }
        for (int i = 0; i < 5; i++)
        {
            BlockPos center = pos.up(i);
            if (!BLAST_FURNACE_CHIMNEY.test(world, pos))
            {
                return i;
            }
        }
        return 5;
    }

    public static final PropertyBool LIT = PropertyBool.create("lit");

    public BlockBlastFurnace()
    {
        super(Material.IRON);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LIT, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LIT) ? 1 : 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.isSneaking() && !worldIn.isRemote)
        {
            TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.BLAST_FURNACE);
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LIT);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEBlastFurnace();
    }

    @Override
    public boolean canIntakeFrom(@Nonnull TEBellows te, @Nonnull Vec3i offset, @Nonnull EnumFacing facing)
    {
        return offset.equals(TEBellows.OFFSET_LEVEL);
    }

    @Override
    public void onAirIntake(@Nonnull TEBellows te, @Nonnull World world, @Nonnull BlockPos pos, int airAmount)
    {
        TEBlastFurnace teBlastFurnace = Helpers.getTE(world, pos, TEBlastFurnace.class);
        if (teBlastFurnace != null)
        {
            teBlastFurnace.onAirIntake(airAmount);
        }
    }
}
