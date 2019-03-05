package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketSheetUpdate;

@ParametersAreNonnullByDefault
public class TEMetalSheet extends TEBase
{
    private boolean[] faces;

    public TEMetalSheet()
    {
        this.faces = new boolean[6];
    }

    /**
     * Gets the number of faces that are present
     *
     * @return a number in [0, 6]
     */
    public int getFaceCount()
    {
        int n = 0;
        for (boolean b : faces)
        {
            if (b) n++;
        }
        return n;
    }

    /**
     * Checks if sheet is present for the given face
     *
     * @param face The face to check
     * @return true if present
     */
    public boolean getFace(EnumFacing face)
    {
        return faces[face.getIndex()];
    }

    public void setFace(EnumFacing face, boolean value)
    {
        faces[face.getIndex()] = value;
        markDirty();
        if (!world.isRemote)
        {
            // This needs to signal to client that there was a TE update that changed the result of getActualState
            // Unfortunately, using any other method of signaling a block update won't work, as no blockstate within the chunk has changed, so the chunk won't re-draw
            TerraFirmaCraft.getNetwork().sendToAllAround(new PacketSheetUpdate(this, face), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        for (EnumFacing face : EnumFacing.values())
        {
            faces[face.getIndex()] = nbt.getBoolean(face.getName());
        }
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        for (EnumFacing face : EnumFacing.values())
        {
            nbt.setBoolean(face.getName(), faces[face.getIndex()]);
        }
        return super.writeToNBT(nbt);
    }
}
