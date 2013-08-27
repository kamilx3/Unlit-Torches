package pelep.unlittorch.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.PacketSender;
import pelep.unlittorch.handler.TickHandler;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemLanternLit extends ItemLantern
{
    public ItemLanternLit(int id)
    {
        super(id);
        this.setUnlocalizedName("ut_lanternLit");
        this.func_111206_d("unlittorch:lantern_completeon");
    }
    
    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        return (ist.stackTagCompound == null || !ist.stackTagCompound.getBoolean("handle")) ? "item.ut_lanternLit" : "item.ut_lanternLitHandle";
    }
    
    @Override
    public String getUnlocalizedName()
    {
        return "ut_lanternLit";
    }
    
    
    //--------------------------------rendering--------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        this.itemIcon = ir.registerIcon("unlittorch:lantern_bodyon");
        this.icon_complete = ir.registerIcon("unlittorch:lantern_completeon");
    }
    
    
    //---------------------------------itemuse---------------------------------//
    
    
    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        ist.itemID = ConfigCommon.blockIdLanternUnlit;
        world.playSoundAtEntity(p, "fire.fire",  1F, world.rand.nextFloat() * 0.4F + 1.5F);
        
        return ist;
    }
    
    
    //---------------------------------update---------------------------------//
    
    
    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int slot, boolean held)
    {
        EntityPlayer p = (EntityPlayer) e;
        
        if (!world.isRemote && !p.capabilities.isCreativeMode && !ConfigCommon.lanternIsSimple)
        {
            int d = ist.getItemDamage();
            
            if (d >= this.getMaxDamage())
            {
                this.killItemLantern(world, p, ist);
                return;
            }
            
            if (TickHandler.updateAge == 0)
            {
                ist.setItemDamage(d + 1);
            }
            
            if (held && TickHandler.updateBurn == 0 && (ist.stackTagCompound == null || !ist.stackTagCompound.getBoolean("handle")))
            {
                p.attackEntityFrom(DamageSource.inFire, 1);
            }
        }
    }
    
    private void killItemLantern(World world, EntityPlayer p, ItemStack ist)
    {
        world.playSoundAtEntity(p, "fire.fire", 1F, world.rand.nextFloat() * 0.4F + 1.5F);
        ist.setItemDamage(this.getMaxDamage());
        ist.itemID = ConfigCommon.blockIdLanternUnlit;
    }
    
    @Override
    public boolean onEntityItemUpdate(EntityItem ei)
    {
        if (!ei.worldObj.isRemote && ei.getEntityItem().itemID == this.itemID && !ConfigCommon.lanternIsSimple)
        {
            ItemStack ist = ei.getEntityItem();
            int d = ist.getItemDamage();
            
            if (d >= this.getMaxDamage())
            {
                this.killEntityLantern(ei);
            }
            
            if (TickHandler.updateAge == 0)
            {
                ist.setItemDamage(d + 1);
            }
        }
        
        return false;
    }
    
    private void killEntityLantern(EntityItem ei)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "fire.fire", 1F, ei.worldObj.rand.nextFloat() * 0.4F + 1.5F);
        ei.getEntityItem().setItemDamage(this.getMaxDamage());
        ei.getEntityItem().itemID = ConfigCommon.blockIdLanternUnlit;
        PacketSender.sendEntityPacket(ei, (byte) 7);
    }
}
