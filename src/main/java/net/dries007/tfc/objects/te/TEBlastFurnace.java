package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class TEBlastFurnace extends TEInventory implements ITickable
{
    public static final int SLOT_TUYERE = 0;

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
}
