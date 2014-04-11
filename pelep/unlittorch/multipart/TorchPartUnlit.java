package pelep.unlittorch.multipart;

import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.TileMultipart;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import pelep.unlittorch.block.BlockTorchUnlit;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;

/**
 * @author pelep
 */
public class TorchPartUnlit extends TorchPart
{
    public TorchPartUnlit() {}

    public TorchPartUnlit(int md, int age, boolean eternal)
    {
        super(md, age, eternal);
    }

    @Override
    public Block getBlock()
    {
        return BlockTorchUnlit.instance;
    }

    @Override
    public String getType()
    {
        return "unlittorch:torch_unlit";
    }

    @Override
    public void onEntityCollision(Entity e)
    {
        if (!world().isRemote && (e.isBurning() || e instanceof EntityBlaze || e instanceof EntityMagmaCube || e instanceof EntityFireball))
            ignitePart("fire.fire");
    }

    @Override
    public boolean activate(EntityPlayer ep, MovingObjectPosition mop, ItemStack ist)
    {
        if (ep.isSneaking())
        {
            if (ist != null) return false;

            if (!world().isRemote)
            {
                ItemStack torch = new ItemStack(getBlockId(), 1, age);
                torch.setTagCompound(eternal ? new NBTTagCompound() : null);
                ep.inventory.setInventorySlotContents(ep.inventory.currentItem, torch);
                tile().remPart(this);
            }

            return true;
        }
        else if (ist != null)
        {
            int id = ist.itemID;
            int d = ist.getItemDamage();

            if (id == ConfigCommon.blockIdTorchLit)
            {
                ignitePart("fire.fire");
                return true;
            }
            else if (IgnitersHandler.canIgniteSetTorch(id, d))
            {
                if (id == Block.torchWood.blockID || id == Item.bucketLava.itemID)
                {
                    ignitePart("fire.fire");
                }
                else if (id == Item.flint.itemID)
                {
                    ignitePart("fire.ignite");
                    if (!ep.capabilities.isCreativeMode) ep.inventory.decrStackSize(ep.inventory.currentItem, 1);
                }
                else if (id == Item.flintAndSteel.itemID)
                {
                    ignitePart("fire.ignite");
                    ist.damageItem(1, ep);
                }
                else
                {
                    ignitePart("fire.fire");

                    if (!ep.capabilities.isCreativeMode)
                    {
                        if (Item.itemsList[id].isDamageable())
                        {
                            ist.damageItem(1, ep);
                        }
                        else
                        {
                            ep.inventory.decrStackSize(ep.inventory.currentItem, 1);
                        }
                    }
                }

                return true;
            }
        }

        if (BlockTorchUnlit.useIgniter(ep))
        {
            ignitePart("fire.fire");
            return true;
        }

        return false;
    }


    //----------------------------------util----------------------------------//


    private void ignitePart(String sound)
    {
        if (world().isRemote) return;

        World world = world();
        BlockCoord pos = new BlockCoord(tile());

        tile().remPart(this);
        TileMultipart.addPart(world, pos, new TorchPartLit(meta, age, eternal));

        if (!"".equals(sound))
            world.playSoundEffect(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
    }
}
