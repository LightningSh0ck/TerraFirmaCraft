package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.te.TEMetalSheet;
import net.dries007.tfc.util.Helpers;

public class PacketSheetUpdate implements IMessage
{
    private BlockPos pos;
    private EnumFacing face;
    private boolean state;

    @SuppressWarnings("unused")
    public PacketSheetUpdate() {}

    public PacketSheetUpdate(TEMetalSheet tile, EnumFacing face)
    {
        this.pos = tile.getPos();
        this.face = face;
        this.state = tile.getFace(face);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        face = EnumFacing.byIndex(buf.readInt());
        state = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(face.getIndex());
        buf.writeBoolean(state);
    }

    public static class Handler implements IMessageHandler<PacketSheetUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSheetUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            World world = player.getEntityWorld();
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                TEMetalSheet te = Helpers.getTE(world, message.pos, TEMetalSheet.class);
                if (te != null)
                {
                    te.setFace(message.face, message.state);
                }
                // Re-render the sheet block
                world.markBlockRangeForRenderUpdate(message.pos, message.pos);
            });
            return null;
        }
    }
}
