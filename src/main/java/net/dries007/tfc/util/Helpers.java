/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.blocks.BlockPeat;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.lambda.FacingChecker;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.minecraft.util.EnumFacing.*;

public final class Helpers
{
    private static final Joiner JOINER_DOT = Joiner.on('.');


    /**
     * Used by horizontal rotatable blocks to search for a valid rotation in a given space,
     * starting from a preferred rotation(like the direction a player is looking upon placing it)
     * usage: facingPriorityLists.get(preferredFacing.getHorizontalIndex())
     */
    public static final List<List<EnumFacing>> facingPriorityLists = new ArrayList<>(4);

    public static void spreadGrass(World world, BlockPos pos, IBlockState us, Random rand)
    {
        if (world.getLightFromNeighbors(pos.up()) < 4 && world.getBlockState(pos.up()).getLightOpacity(world, pos.up()) > 2)
        {
            if (us.getBlock() instanceof BlockPeat)
            {
                world.setBlockState(pos, BlocksTFC.PEAT.getDefaultState());
            }
            else if (us.getBlock() instanceof BlockRockVariant)
            {
                BlockRockVariant block = ((BlockRockVariant) us.getBlock());
                world.setBlockState(pos, block.getVariant(block.type.getNonGrassVersion()).getDefaultState());
            }
        }
        else
        {
            if (world.getLightFromNeighbors(pos.up()) < 9) return;

            for (int i = 0; i < 4; ++i)
            {
                BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                if (world.isOutsideBuildHeight(target) || !world.isBlockLoaded(target)) return;
                BlockPos up = target.add(0, 1, 0);

                IBlockState current = world.getBlockState(target);
                if (!BlocksTFC.isSoil(current) || BlocksTFC.isGrass(current)) continue;
                if (world.getLightFromNeighbors(up) < 4 || world.getBlockState(up).getLightOpacity(world, up) > 3)
                    continue;

                if (current.getBlock() instanceof BlockPeat)
                {
                    world.setBlockState(target, BlocksTFC.PEAT_GRASS.getDefaultState());
                }
                else if (current.getBlock() instanceof BlockRockVariant)
                {
                    Rock.Type spreader = Rock.Type.GRASS;
                    if ((us.getBlock() instanceof BlockRockVariant) && ((BlockRockVariant) us.getBlock()).type == Rock.Type.DRY_GRASS)
                        spreader = Rock.Type.DRY_GRASS;

                    BlockRockVariant block = ((BlockRockVariant) current.getBlock());
                    world.setBlockState(target, block.getVariant(block.type.getGrassVersion(spreader)).getDefaultState());
                }
            }

            for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
            {
                if (plant.getPlantType() == Plant.PlantType.SHORT_GRASS && rand.nextFloat() < 0.01f)
                {
                    float temp = ClimateTFC.getHeightAdjustedBiomeTemp(world, pos.up());
                    BlockShortGrassTFC plantBlock = BlockShortGrassTFC.get(plant);

                    if (world.isAirBlock(pos.up()) &&
                        plant.isValidLocation(temp, ChunkDataTFC.getRainfall(world, pos.up()), world.getLightFromNeighbors(pos.up())) &&
                        plant.isValidGrowthTemp(temp) &&
                        rand.nextDouble() < plantBlock.getGrowthRate(world, pos.up()))
                    {
                        world.setBlockState(pos.up(), plantBlock.getDefaultState());
                    }
                }
            }
        }
    }

    public static boolean containsAnyOfCaseInsensitive(Collection<String> input, String... items)
    {
        Set<String> itemsSet = Arrays.stream(items).map(String::toLowerCase).collect(Collectors.toSet());
        return input.stream().map(String::toLowerCase).anyMatch(itemsSet::contains);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> T getTE(IBlockAccess world, BlockPos pos, Class<T> aClass)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!aClass.isInstance(te)) return null;
        return (T) te;
    }

    public static String getEnumName(Enum<?> anEnum)
    {
        return JOINER_DOT.join(TFCConstants.MOD_ID, "enum", anEnum.getDeclaringClass().getSimpleName(), anEnum).toLowerCase();
    }

    public static String getTypeName(IForgeRegistryEntry<?> type)
    {
        //noinspection ConstantConditions
        return JOINER_DOT.join(TFCConstants.MOD_ID, "types", type.getRegistryType().getSimpleName(), type.getRegistryName().getPath()).toLowerCase();
    }

    public static boolean playerHasItemMatchingOre(InventoryPlayer playerInv, String ore)
    {
        for (ItemStack stack : playerInv.mainInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        for (ItemStack stack : playerInv.armorInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        for (ItemStack stack : playerInv.offHandInventory)
        {
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, ore))
            {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, int amount)
    {
        if (stack.getCount() <= amount)
        {
            return ItemStack.EMPTY;
        }
        stack.shrink(amount);
        return stack;
    }

    @Nonnull
    public static ItemStack consumeItem(ItemStack stack, EntityPlayer player, int amount)
    {
        return player.isCreative() ? stack : consumeItem(stack, amount);
    }

    /**
     * Simple method to spawn items in the world at a precise location, rather than using InventoryHelper
     */
    public static void spawnItemStack(World world, BlockPos pos, ItemStack stack)
    {
        if (stack.isEmpty())
            return;
        EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        world.spawnEntity(entityitem);
    }

    //an ugly initialisation, don't mind this
    static
    {
        List<EnumFacing> list = new ArrayList<>(4);
        list.add(SOUTH);
        list.add(WEST);
        list.add(EAST);
        list.add(NORTH);
        facingPriorityLists.add(list);
        list = new ArrayList<>(4);
        list.add(WEST);
        list.add(NORTH);
        list.add(SOUTH);
        list.add(EAST);
        facingPriorityLists.add(list);
        list = new ArrayList<>(4);
        list.add(NORTH);
        list.add(EAST);
        list.add(WEST);
        list.add(SOUTH);
        facingPriorityLists.add(list);
        list = new ArrayList<>(4);
        list.add(EAST);
        list.add(SOUTH);
        list.add(NORTH);
        list.add(WEST);
        facingPriorityLists.add(list);
    }

    /**
     * @see #getAValidFacing
     * very simillar, made for horizontally rotatable blocks
     * @param preferredSide must be an {@link EnumFacing#HORIZONTALS}
     * @return A valid Horizontal facing or null if none is
     */
    public static EnumFacing getAValidHorizontal(World worldIn, BlockPos pos, FacingChecker checker, EnumFacing preferredSide)
    {
        int index = preferredSide.getHorizontalIndex();
        if (index == -1)
            throw new IllegalArgumentException("Received side was not a horizontal");
        return getAValidFacing(worldIn, pos, checker, facingPriorityLists.get(preferredSide.getHorizontalIndex()));
    }

    /**
     * This is meant to avoid Intellij's warnings about null fields that are injected to at runtime
     * Use this for things like @ObjectHolder, @CapabilityInject, etc.
     * AKA - The @Nullable is intentional. If it crashes your dev env, then fix your dev env, not this. :)
     *
     * @param <T> anything and everything
     * @return null, but not null
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull()
    {
        return null;
    }

    /**
     * Primarily for use in placing checks. Determines a valid facing for a block.
     *
     * @param pos           position that the block does or is going to occupy.
     * @param checker       the checking algorithm. For simple solid side checking,
     * @param possibleSides a collection of all sides the block can face, sorted by priority.
     * @return Found facing or null is none is found.
     * @see FacingChecker#canHangAt
     */
    public static EnumFacing getAValidFacing(World worldIn, BlockPos pos, FacingChecker checker, Collection<EnumFacing> possibleSides)
    {
        for (EnumFacing side : possibleSides)
        {
            if (side != null && checker.canFace(worldIn, pos, side))
            {
                return side;
            }
        }
        return null;
    }
}
