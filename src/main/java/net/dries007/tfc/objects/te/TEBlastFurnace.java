package net.dries007.tfc.objects.te;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class TEBlastFurnace extends TEInventory implements ITickable
{
    public static final int SLOT_TUYERE = 0;

    private Queue<ItemStack> oreStacks = new ArrayBlockingQueue<>(20);
    private int maxOreItems = 0; // Max ore stacks in the blast furnace (if it ever goes below, the top will be ejected)
    private int delayTimer = 0; // Time before checking the multiblock status of the blast furnace

    private int fuelTicks = 0;
    private int fluxAmount = 0;
    private int airTicks = 0;

    private float temperature;
    private float targetTemperature;

    public TEBlastFurnace()
    {
        super(1);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "tuyere");
    }

    @Override
    public void update()
    {

    }

    /**
     * Passed from BlockBlastFurnace's IBellowsConsumerBlock
     *
     * @param airAmount the air amount
     */
    public void onAirIntake(int airAmount)
    {
        airTicks += airAmount;
        if (airTicks > 600)
        {
            airTicks = 600;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        oreStacks.clear();
        NBTTagList ores = nbt.getTagList("ores", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < ores.tagCount(); i++)
        {
            ItemStack oreStack = new ItemStack(ores.getCompoundTagAt(i));
            oreStacks.offer(oreStack);
        }
        fluxAmount = nbt.getInteger("flux");
        fuelTicks = nbt.getInteger("fuel");
        airTicks = nbt.getInteger("air");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList ores = new NBTTagList();
        for (ItemStack stack : oreStacks)
        {
            ores.appendTag(stack.serializeNBT());
        }
        nbt.setTag("ores", ores);
        nbt.setInteger("flux", fluxAmount);
        nbt.setInteger("fuel", fuelTicks);
        nbt.setInteger("air", airTicks);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }
}
