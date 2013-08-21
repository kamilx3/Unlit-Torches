package pelep.unlittorch.block;

import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.entity.TileEntityLantern;
import pelep.unlittorch.handler.IgnitersHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockLanternUnlit extends BlockLantern
{
    public BlockLanternUnlit()
    {
        super(ConfigCommon.blockIdLanternUnlit, false);
        this.setLightValue(0F);
        this.setUnlocalizedName("ut_lanternUnlit");
        this.func_111022_d("unlittorch:lantern_off");
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float i, float j, float k)
    {
        boolean activate = this.activateBlock(world, x, y, z, p, side, i, j, k);
        
        if (!activate)
        {
            TileEntityLantern te = getTileEntityLantern(world, x, y, z);
            int age = te.getAge();
            
            if (p.capabilities.isCreativeMode || (age < ConfigCommon.lanternLifespanMax && this.checkPlayer(p)))
            {
                boolean handle = te.hasHandle();
                
                world.setBlock(x, y, z, ConfigCommon.blockIdLanternLit, world.getBlockMetadata(x, y, z), 3);
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 0.8F);
                
                te = getTileEntityLantern(world, x, y, z);
                te.setAge(age);
                te.setHandle(handle);
                
                return true;
            }
        }
        
        return activate;
    }
    
    private boolean checkPlayer(EntityPlayer p)
    {
        int tinderbox = -1;
        int igniter = -1;
        
        for (int i = 0; i < p.inventory.mainInventory.length; i++)
        {
            ItemStack ist = p.inventory.mainInventory[i];
            
            if (ist != null)
            {
                int id = ist.itemID;
                int d = ist.getItemDamage();
                
                if (id == ConfigCommon.itemIdTinderboxFS && IgnitersHandler.isLanternIgniter(id, d))
                {
                    if (ist.attemptDamageItem(1, p.getRNG()))
                    {
                        p.inventory.setInventorySlotContents(i, new ItemStack(ConfigCommon.itemIdTinderbox, 1, 0));
                    }
                    
                    return true;
                }
                else if (tinderbox == -1 && id == ConfigCommon.itemIdTinderbox && d == 0)
                {
                    tinderbox = i;
                }
                else if (igniter == -1 && IgnitersHandler.isLanternIgniter(id, d) && id != Item.flint.itemID && id != Item.gunpowder.itemID)
                {
                    igniter = i;
                }
            }
        }
        
        boolean light = false;
        
        if (igniter != -1 && tinderbox != -1)
        {
            ItemStack ign = p.inventory.mainInventory[igniter];
            light = true;
            
            if (ign.itemID == 50)
            {
                ign.setItemDamage(ign.getItemDamage() + (ign.getMaxDamage() / 10));
            }
            else if (ign.isItemStackDamageable())
            {
                ign.damageItem(1, p);
            }
            else if (ign.itemID != Item.bucketLava.itemID)
            {
                p.inventory.decrStackSize(igniter, 1);
            }
        }
        
        return light;
    }
}
