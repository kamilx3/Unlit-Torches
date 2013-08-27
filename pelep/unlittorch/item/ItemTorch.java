package pelep.unlittorch.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.config.ConfigCommon;
import pelep.unlittorch.handler.IgnitersHandler;
import pelep.unlittorch.handler.PacketSender;
import pelep.unlittorch.handler.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

public class ItemTorch extends ItemBlock
{
    private static Icon icon_unlit;
    
    public ItemTorch(int id)
    {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setNoRepair();
        this.setUnlocalizedName("ut_torchLit");
        this.func_111206_d("torch_on");
    }
    
    @Override
    public boolean shouldPassSneakingClickToBlock(World world, int x, int y, int z)
    {
        return world.getBlockId(x, y, z) == this.itemID;
    }
    
    
    //--------------------------------register---------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int id, CreativeTabs ct, List list)
    {
        list.add(new ItemStack(id, 1, 1));
        list.add(new ItemStack(id, 1, 0));
    }
    
    @Override
    public String getUnlocalizedName(ItemStack ist)
    {
        return ist.getItemDamage() == 0 ? "item.ut_torchUnlit" : "item.ut_torchLit";
    }
    
    
    //--------------------------------rendering--------------------------------//
    
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getSpriteNumber()
    {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
        this.itemIcon = ir.registerIcon("torch_on");
        icon_unlit = ir.registerIcon("unlittorch:torch_off");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int d)
    {
        return d == 0 ? icon_unlit : this.itemIcon;
    }
    
    
    //---------------------------------itemuse---------------------------------//
    
    
    @Override
    public boolean hitEntity(ItemStack ist, EntityLivingBase el, EntityLivingBase ep)
    {
        EntityPlayer p = (EntityPlayer) ep;
        
        if (!p.worldObj.isRemote && !p.capabilities.isCreativeMode)
        {
            int d = ist.getItemDamage();
            
            if (el.isBurning() || el instanceof EntityBlaze || el instanceof EntityMagmaCube)
            {
                if (d == 0)
                {
                    ist.setItemDamage(this.getMaxLifespan() / 3);
                }
                else
                {
                    d -= (this.getMaxLifespan() / 3);
                    ist.setItemDamage(Math.max(d, 1));
                }
                
                p.worldObj.playSoundAtEntity(p, "fire.fire",  1F, itemRand.nextFloat() * 0.4F + 0.8F);
            }
            else if (d != 0)
            {
                d += (this.getMaxLifespan() / 24);
                ist.setItemDamage(d);
            }
        }
        
        return false;
    }
    
    @Override
    public boolean onBlockDestroyed(ItemStack ist, World world, int id, int x, int y, int z, EntityLivingBase ep)
    {
        if (!world.isRemote && ist.getItemDamage() != 0)
        {
            EntityPlayer p = (EntityPlayer) ep;
            
            if (!p.capabilities.isCreativeMode)
            {
                int d = ist.getItemDamage() + (this.getMaxLifespan() / 16);
                ist.setItemDamage(d);
            }
        }
        
        return false;
    }
    
    private boolean hitLiquid(EntityPlayer p)
    {
        MovingObjectPosition mop = this.rayTraceFromEntity(p, false, true, true, false);
        
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
        {
            return p.worldObj.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ) == Material.water;
        }
        
        return false;
    }
    
    private boolean hitFlame(EntityPlayer p)
    {
        MovingObjectPosition mop = this.rayTraceFromEntity(p, false, false, false, true);
        
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE)
        {
            Material m = p.worldObj.getBlockMaterial(mop.blockX, mop.blockY, mop.blockZ);
            return m == Material.fire || m == Material.lava;
        }
        
        return false;
    }
    
    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer p, World world, int x, int y, int z, int side, float i, float j, float k)
    {
        int d = ist.getItemDamage();
        
        if (this.hitFlame(p) || (this.hitLiquid(p) && d > 0))
        {
            return false;
        }
        
        int id = world.getBlockId(x, y, z);
        int md = world.getBlockMetadata(x, y, z);
        
        if (id == this.itemID)
        {
            return false;
        }
        else if (IgnitersHandler.isHeldTorchIgniter(id, md))
        {
            this.renewTorch(p, world, ist, 1, "fire.fire");
            return false;
        }
        
        if (id == Block.snow.blockID && (md & 7) < 1)
        {
            side = 1;
        }
        else if (id != Block.vine.blockID &&
                id != Block.tallGrass.blockID &&
                id != Block.deadBush.blockID &&
                (Block.blocksList[id] == null ||
                !Block.blocksList[id].isBlockReplaceable(world, x, y, z)))
        {
            switch (side)
            {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            case 5:
                x++;
            }
        }
        
        if (ist.stackSize == 0)
        {
            return false;
        }
        else if (!p.canPlayerEdit(x, y, z, side, ist))
        {
            return false;
        }
        else if (y == 255 && Block.blocksList[this.itemID].blockMaterial.isSolid())
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(this.itemID, x, y, z, false, side, p, ist))
        {
            Block block = Block.blocksList[this.itemID];
            md = block.onBlockPlaced(world, x, y, z, side, i, j, k, d);
            
            if (this.placeTorchAt(ist, p, world, x, y, z, md))
            {
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1F) / 2F, block.stepSound.getPitch() * 0.8F);
                ist.stackSize--;
            }
            
            return true;
        }
        
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer p, ItemStack ist)
    {
        if (world.getBlockId(x, y, z) == this.itemID && p.isSneaking())
        {
            return true;
        }
        
        return super.canPlaceItemBlockOnSide(world, x, y, z, side, p, ist);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer p)
    {
        if (this.hitLiquid(p) && ist.getItemDamage() > 0)
        {
            this.renewTorch(p, world, ist, 0, "random.fizz");
        }
        else if (this.hitFlame(p))
        {
            this.renewTorch(p, world, ist, 1, "fire.fire");
        }
        
        return ist;
    }
    
    private void renewTorch(EntityPlayer p, World world, ItemStack ist, int d, String sound)
    {
        if (p.capabilities.isCreativeMode)
        {
            int size = p.isSneaking() ? 1 : ist.stackSize;
            p.swingItem();
            p.inventory.addItemStackToInventory(new ItemStack(this.itemID, size, d));
            world.playSoundAtEntity(p, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
        }
        else if (!p.isSneaking())
        {
            p.swingItem();
            world.playSoundAtEntity(p, sound, 1F, world.rand.nextFloat() * 0.4F + 0.8F);
            ist.setItemDamage(d);
        }
        else if (p.isSneaking() && p.inventory.addItemStackToInventory(new ItemStack(this.itemID, 1, d)))
        {
            p.swingItem();
            world.playSoundAtEntity(p, sound, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
            ist.stackSize--;
        }
    }
    
    private MovingObjectPosition rayTraceFromEntity(EntityLivingBase el, boolean collidableOnly, boolean fluid, boolean flowing, boolean fire)
    {
        float pitch = el.prevRotationPitch + (el.rotationPitch - el.prevRotationPitch);
        float yaw = el.prevRotationYaw + (el.rotationYaw - el.prevRotationYaw);
        
        double v1x = el.prevPosX + (el.posX - el.prevPosX);
        double v1y = el.prevPosY + (el.posY - el.prevPosY) + 1.62D - el.yOffset;
        double v1z = el.prevPosZ + (el.posZ - el.prevPosZ);
        
        float f = -MathHelper.cos(-pitch * 0.017453292F);
        
        double v2x = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI) * f;
        double v2y = MathHelper.sin(-pitch * 0.017453292F);
        double v2z = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI) * f;
        
        double reach = 5D;
        
        if (el instanceof EntityPlayerMP)
        {
            reach = ((EntityPlayerMP)el).theItemInWorldManager.getBlockReachDistance();
        }
        
        Vec3 v1 = el.worldObj.getWorldVec3Pool().getVecFromPool(v1x, v1y, v1z);
        Vec3 v2 = v1.addVector(v2x * reach, v2y * reach, v2z * reach);
        
        return this.rayTrace(el.worldObj, v1, v2, collidableOnly, fluid, flowing, fire);
    }
    
    private MovingObjectPosition rayTrace(World world, Vec3 v1, Vec3 v2, boolean collidableOnly, boolean fluid, boolean flowing, boolean fire)
    {
        if (!Double.isNaN(v1.xCoord) &&
            !Double.isNaN(v1.yCoord) &&
            !Double.isNaN(v1.zCoord) &&
            !Double.isNaN(v2.xCoord) &&
            !Double.isNaN(v2.yCoord) &&
            !Double.isNaN(v2.zCoord))
        {
            int v1x = MathHelper.floor_double(v1.xCoord);
            int v1y = MathHelper.floor_double(v1.yCoord);
            int v1z = MathHelper.floor_double(v1.zCoord);
            int v2x = MathHelper.floor_double(v2.xCoord);
            int v2y = MathHelper.floor_double(v2.yCoord);
            int v2z = MathHelper.floor_double(v2.zCoord);
            
            int id = world.getBlockId(v1x, v1y, v1z);
            int md = world.getBlockMetadata(v1x, v1y, v1z);
            Block block = Block.blocksList[id];
            
            if (block != null)
            {
                if (fluid && flowing)
                {
                    md = 0;
                }
                
                MovingObjectPosition mop;
                
                if (!collidableOnly && fire && (block.blockMaterial == Material.fire || block.blockMaterial == Material.lava))
                {
                    mop = block.collisionRayTrace(world, v1x, v1y, v1z, v1, v2);
                    
                    if (mop != null)
                    {
                        return mop;
                    }
                }
                
                if (id > 0 && block.canCollideCheck(md, fluid) && (!collidableOnly || block.getCollisionBoundingBoxFromPool(world, v1x, v1y, v1z) != null))
                {
                    mop = block.collisionRayTrace(world, v1x, v1y, v1z, v1, v2);
                    
                    if (mop != null)
                    {
                        return mop;
                    }
                }
            }
            
            int d = 200;
            
            while (d-- >= 0)
            {
                if (Double.isNaN(v1.xCoord) || Double.isNaN(v1.yCoord) || Double.isNaN(v1.zCoord))
                {
                    return null;
                }
                
                if (v1x == v2x && v1y == v2y && v1z == v2z)
                {
                    return null;
                }
                
                boolean xIsAngled = true;
                boolean yIsAngled = true;
                boolean zIsAngled = true;
                double xAdjust = 999D;
                double yAdjust = 999D;
                double zAdjust = 999D;
                
                if (v2x > v1x)
                {
                    xAdjust = v1x + 1D;
                }
                else if (v2x < v1x)
                {
                    xAdjust = v1x;
                }
                else
                {
                    xIsAngled = false;
                }
                
                if (v2y > v1y)
                {
                    yAdjust = v1y + 1D;
                }
                else if (v2y < v1y)
                {
                    yAdjust = v1y;
                }
                else
                {
                    yIsAngled = false;
                }
                
                if (v2z > v1z)
                {
                    zAdjust = v1z + 1D;
                }
                else if (v2z < v1z)
                {
                    zAdjust = v1z;
                }
                else
                {
                    zIsAngled = false;
                }
                
                double xaf = 999D;
                double yaf = 999D;
                double zaf = 999D;
                double xLength = v2.xCoord - v1.xCoord;
                double yLength = v2.yCoord - v1.yCoord;
                double zLength = v2.zCoord - v1.zCoord;
                
                if (xIsAngled)
                {
                    xaf = (xAdjust - v1.xCoord) / xLength;
                }

                if (yIsAngled)
                {
                    yaf = (yAdjust - v1.yCoord) / yLength;
                }
                
                if (zIsAngled)
                {
                    zaf = (zAdjust - v1.zCoord) / zLength;
                }
                
                byte b;
                
                if (xaf < yaf && xaf < zaf)
                {
                    if (v2x > v1x)
                    {
                        b = 4;
                    }
                    else
                    {
                        b = 5;
                    }
                    
                    v1.xCoord = xAdjust;
                    v1.yCoord += yLength * xaf;
                    v1.zCoord += zLength * xaf;
                }
                else if (yaf < zaf)
                {
                    if (v2y > v1y)
                    {
                        b = 0;
                    }
                    else
                    {
                        b = 1;
                    }
                    
                    v1.xCoord += xLength * yaf;
                    v1.yCoord = yAdjust;
                    v1.zCoord += zLength * yaf;
                }
                else
                {
                    if (v2z > v1z)
                    {
                        b = 2;
                    }
                    else
                    {
                        b = 3;
                    }
                    
                    v1.xCoord += xLength * zaf;
                    v1.yCoord += yLength * zaf;
                    v1.zCoord = zAdjust;
                }
                
                Vec3 v3 = world.getWorldVec3Pool().getVecFromPool(v1.xCoord, v1.yCoord, v1.zCoord);
                
                v1x = (int)(v3.xCoord = MathHelper.floor_double(v1.xCoord));
                v1y = (int)(v3.yCoord = MathHelper.floor_double(v1.yCoord));
                v1z = (int)(v3.zCoord = MathHelper.floor_double(v1.zCoord));
                
                if (b == 5)
                {
                    v1x--;
                    v3.xCoord++;
                }
                
                if (b == 1)
                {
                    v1y--;
                    v3.yCoord++;
                }
                
                if (b == 3)
                {
                    v1z--;
                    v3.zCoord++;
                }
                
                id = world.getBlockId(v1x, v1y, v1z);
                md = world.getBlockMetadata(v1x, v1y, v1z);
                block = Block.blocksList[id];
                
                if (block != null)
                {
                    if (fluid && flowing)
                    {
                        md = 0;
                    }
                    
                    MovingObjectPosition mop;
                    
                    if (!collidableOnly && fire && (block.blockMaterial == Material.fire || block.blockMaterial == Material.lava))
                    {
                        mop = block.collisionRayTrace(world, v1x, v1y, v1z, v1, v2);
                        
                        if (mop != null)
                        {
                            return mop;
                        }
                    }
                    
                    if (id > 0 && block.canCollideCheck(md, fluid) && (!collidableOnly || block.getCollisionBoundingBoxFromPool(world, v1x, v1y, v1z) != null))
                    {
                        mop = block.collisionRayTrace(world, v1x, v1y, v1z, v1, v2);
                        
                        if (mop != null)
                        {
                            return mop;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    
    //-------------------------------place-block------------------------------//
    
    
    private boolean placeTorchAt(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int md)
    {
        if (world.setBlock(x, y, z, this.itemID, md, 3))
        {
            if (world.getBlockId(x, y, z) == this.itemID)
            {
                Block.blocksList[this.itemID].onBlockPlacedBy(world, x, y, z, player, ist);
            }
            
            return true;
        }
        
        return false;
    }
    
    
    //---------------------------------update---------------------------------//
    
    
    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int slot, boolean held)
    {
        EntityPlayer p = (EntityPlayer) e;
        
        if (!world.isRemote && !p.capabilities.isCreativeMode && !ConfigCommon.torchIsSimple)
        {
            int d = ist.getItemDamage();
            
            if (p.handleLavaMovement() || p.isBurning())
            {
                if (d != 1)
                {
                    world.playSoundAtEntity(p, "fire.fire", 1F, 1F);
                }
                
                for (int i = slot; i < p.inventory.mainInventory.length; i++)
                {
                    ItemStack inv = p.inventory.mainInventory[i];
                    
                    if (inv != null && inv.itemID == this.itemID)
                    {
                        inv.setItemDamage(1);
                    }
                }
                
                return;
            }
            else if (d > 0)
            {
                if (p.isInsideOfMaterial(Material.water))
                {
                    world.playSoundAtEntity(p, "random.fizz", 0.8F, 1F);
                    
                    for (int i = slot; i < p.inventory.mainInventory.length; i++)
                    {
                        ItemStack inv = p.inventory.mainInventory[i];
                        
                        if (inv != null && inv.itemID == this.itemID && inv.getItemDamage() > 0)
                        {
                            inv.setItemDamage(0);
                        }
                    }
                    
                    return;
                }
                
                if (d >= this.getMaxLifespan())
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        this.destroyItemTorch(world, p, ist, slot, false);
                    }
                    else
                    {
                        this.killItemTorch(world, p, ist, "fire.fire");
                    }
                    
                    return;
                }
                
                if (d > ConfigCommon.torchLifespanMin && ConfigCommon.torchKillChance > 0 && world.rand.nextInt(ConfigCommon.torchKillChance) == 0)
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        this.destroyItemTorch(world, p, ist, slot, false);
                    }
                    else
                    {
                        this.killItemTorch(world, p, ist, "fire.fire");
                    }
                    
                    if (p.inventory.getStackInSlot(slot) == null)
                    {
                        return;
                    }
                }
                
                int x = MathHelper.floor_double(p.posX);
                int y = MathHelper.floor_double(p.posY);
                int z = MathHelper.floor_double(p.posZ);
                
                if (world.canLightningStrikeAt(x, y, z) && ((held && world.rand.nextInt(50) == 0) || world.rand.nextInt(80) == 0))
                {
                    this.killItemTorch(world, p, ist, "random.fizz");
                    return;
                }
                
                if (TickHandler.updateAge == 0)
                {
                    int add = held ? 2 : 1;
                    ist.setItemDamage(d + add);
                }
            }
        }
    }
    
    private void destroyItemTorch(World world, EntityPlayer p, ItemStack ist, int slot, boolean random)
    {
        world.playSoundAtEntity(p, "fire.fire", 1F, 1F);
        
        if (random)
        {
            int dec = world.rand.nextInt(ist.stackSize) + 1;
            p.inventory.decrStackSize(slot, dec);
        }
        else
        {
            p.inventory.setInventorySlotContents(slot, null);
        }
    }
    
    private void killItemTorch(World world, EntityPlayer p, ItemStack ist, String sound)
    {
        world.playSoundAtEntity(p, sound, 0.8F, 1F);
        ist.setItemDamage(0);
    }
    
    @Override
    public boolean onEntityItemUpdate(EntityItem ei)
    {
        this.updateEntityTorch(ei);
        ei.onEntityUpdate();
        
        if (ei.delayBeforeCanPickup > 0)
        {
            ei.delayBeforeCanPickup--;
        }
        
        ei.prevPosX = ei.posX;
        ei.prevPosY = ei.posY;
        ei.prevPosZ = ei.posZ;
        ei.motionY -= 0.03999999910593033D;
        ei.noClip = this.pushOutOfBlocks(ei);
        ei.moveEntity(ei.motionX, ei.motionY, ei.motionZ);
        
        boolean moved = (int)ei.prevPosX != (int)ei.posX || (int)ei.prevPosY != (int)ei.posY || (int)ei.prevPosZ != (int)ei.posZ;
        
        if (moved || ei.ticksExisted % 25 == 0)
        {
            int x = MathHelper.floor_double(ei.posX);
            int y = MathHelper.floor_double(ei.posY);
            int z = MathHelper.floor_double(ei.posZ);
            
            if (ei.worldObj.getBlockMaterial(x, y, z) == Material.lava)
            {
                ei.motionX = (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F;
                ei.motionY = 0.20000000298023224D;
                ei.motionZ = (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F;
                ei.playSound("random.fizz", 0.4F, 2F + itemRand.nextFloat() * 0.4F);
            }
            
            if (!ei.worldObj.isRemote)
            {
                for (Object o : ei.worldObj.getEntitiesWithinAABB(EntityItem.class, ei.boundingBox.expand(0.5D, 0.0D, 0.5D)))
                {
                    EntityItem ei2 = (EntityItem) o;
                    this.combineEntityItems(ei, ei2);
                }
            }
        }
        
        float velocity = 0.98F;
        
        if (ei.onGround)
        {
            velocity = 0.58800006F;
            
            int x = MathHelper.floor_double(ei.posX);
            int y = MathHelper.floor_double(ei.boundingBox.minY) - 1;
            int z = MathHelper.floor_double(ei.posZ);
            int id = ei.worldObj.getBlockId(x, y, z);
            
            if (id > 0)
            {
                velocity = Block.blocksList[id].slipperiness * 0.98F;
            }
        }
        
        ei.motionX *= velocity;
        ei.motionY *= 0.9800000190734863D;
        ei.motionZ *= velocity;
        
        if (ei.onGround)
        {
            ei.motionY *= -0.5D;
        }
        
        ei.age++;
        
        ItemStack ist = ei.getDataWatcher().getWatchableObjectItemStack(10);
        
        if (!ei.worldObj.isRemote && ei.age >= ei.lifespan)
        {
            if (ist != null)
            {
                int lifespan;
                
                if (ist.getItem() == null)
                {
                    lifespan = 6000;
                }
                else
                {
                    lifespan = ist.getItem().getEntityLifespan(ist, ei.worldObj);
                }
                
                ItemExpireEvent event = new ItemExpireEvent(ei, lifespan);
                
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    ei.lifespan += event.extraLife;
                }
                else
                {
                    ei.setDead();
                }
            }
            else
            {
                ei.setDead();
            }
        }
        
        if (ist != null && ist.stackSize <= 0)
        {
            ei.setDead();
        }
        
        return true;
    }
    
    private boolean pushOutOfBlocks(EntityItem ei)
    {
        World world = ei.worldObj;
        
        double ex = ei.posX;
        double ey = (ei.boundingBox.minY + ei.boundingBox.maxY) / 2D;
        double ez = ei.posZ;
        
        int fx = MathHelper.floor_double(ex);
        int fy = MathHelper.floor_double(ey);
        int fz = MathHelper.floor_double(ez);
        
        double dx = ex - fx;
        double dy = ey - fy;
        double dz = ez - fz;
        
        List list = world.getCollidingBlockBounds(ei.boundingBox);
        
        if (list.isEmpty() && !world.isBlockFullCube(fx, fy, fz))
        {
            return false;
        }
        else
        {
            boolean opent = !world.isBlockFullCube(fx, fy + 1, fz);
            boolean openn = !world.isBlockFullCube(fx, fy, fz - 1);
            boolean opens = !world.isBlockFullCube(fx, fy, fz + 1);
            boolean openw = !world.isBlockFullCube(fx - 1, fy, fz);
            boolean opene = !world.isBlockFullCube(fx + 1, fy, fz);
            
            byte b = 3;
            double d = 9999D;
            
            if (openw && dx < d)
            {
                d = dx;
                b = 0;
            }
            
            if (opene && 1D - dx < d)
            {
                d = 1D - dx;
                b = 1;
            }
            
            if (opent && 1D - dy < d)
            {
                d = 1D - dy;
                b = 3;
            }
            
            if (openn && dz < d)
            {
                d = dz;
                b = 4;
            }
            
            if (opens && 1D - dz < d)
            {
                d = 1D - dz;
                b = 5;
            }
            
            float f = itemRand.nextFloat() * 0.2F + 0.1F;
            
            switch (b)
            {
            case 0:
                f = -f;
            case 1:
                ei.motionX = f;
                break;
            case 2:
                f = -f;
            case 3:
                ei.motionY = f;
                break;
            case 4:
                f = -f;
            case 5:
                ei.motionZ = f;
                break;
            }
            
            return true;
        }
    }
    
    private void combineEntityItems(EntityItem e1, EntityItem e2)
    {
        if (e1.getEntityItem().itemID != 50 || e2.getEntityItem().itemID != 50)
        {
            return;
        }
        
        if (e1 == e2)
        {
            return;
        }
        else if (e1.isEntityAlive() && e2.isEntityAlive())
        {
            ItemStack ist1 = e1.getEntityItem();
            ItemStack ist2 = e2.getEntityItem();
            
            if (ist2.getItem() != ist1.getItem())
            {
                return;
            }
            else if (ist2.getItemDamage() == 0 ^ ist1.getItemDamage() == 0)
            {
                return;
            }
            else if (ist2.hasTagCompound() ^ ist1.hasTagCompound())
            {
                return;
            }
            else if (ist2.hasTagCompound() && !ist2.getTagCompound().equals(ist1.getTagCompound()))
            {
                return;
            }
            else if (ist2.stackSize < ist1.stackSize)
            {
                e2.combineItems(e1);
            }
            else if (ist2.stackSize + ist1.stackSize > ist2.getMaxStackSize())
            {
                return;
            }
            else
            {
                EntityItem ei = e2;
                
                int d1 = ist1.getItemDamage();
                int d2 = ist2.getItemDamage();
                
                ist2.stackSize += ist1.stackSize;
                ist2.setItemDamage(Math.min(d1, d2));
                
                ei.delayBeforeCanPickup = Math.max(ei.delayBeforeCanPickup, e1.delayBeforeCanPickup);
                ei.age = Math.min(ei.age, e1.age);
                ei.setEntityItemStack(ist2);
                
                e1.setDead();
            }
        }
    }
    
    private void updateEntityTorch(EntityItem ei)
    {
        if (!ei.worldObj.isRemote && !ConfigCommon.torchIsSimple)
        {
            ItemStack ist = ei.getEntityItem();
            int d = ist.getItemDamage();
            
            if (ist.itemID == this.itemID && d > 0)
            {
                if (ei.handleWaterMovement())
                {
                    this.killEntityTorch(ei, "random.fizz");
                    return;
                }
                
                if (d >= this.getMaxLifespan())
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        this.destroyEntityTorch(ei, false);
                    }
                    else
                    {
                        this.killEntityTorch(ei, "fire.fire");
                    }
                    
                    return;
                }
                
                if (d > ConfigCommon.torchLifespanMin && ConfigCommon.torchKillChance > 0 && itemRand.nextInt(ConfigCommon.torchKillChance) == 0)
                {
                    if (ConfigCommon.torchSingleUse)
                    {
                        this.destroyEntityTorch(ei, true);
                    }
                    else
                    {
                        this.killEntityTorch(ei, "fire.fire");
                    }
                    
                    return;
                }
                
                int x = MathHelper.floor_double(ei.posX);
                int y = MathHelper.floor_double(ei.posY);
                int z = MathHelper.floor_double(ei.posZ);
                
                if (ei.worldObj.canLightningStrikeAt(x, y, z) && itemRand.nextInt(30) == 0)
                {
                    this.killEntityTorch(ei, "random.fizz");
                    return;
                }
                
                if (TickHandler.updateAge == 0)
                {
                    ist.setItemDamage(d + 1);
                }
            }
        }
    }
    
    private void destroyEntityTorch(EntityItem ei, boolean random)
    {
        if (random)
        {
            ItemStack ist = ei.getEntityItem();
            
            int dec = ei.worldObj.rand.nextInt(ist.stackSize) + 1;
            ist.stackSize -= dec;
            
            if (ist.stackSize <= 0)
            {
                ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "fire.fire", 1F, 1F);
                ei.setDead();
            }
        }
        else
        {
            ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, "fire.fire", 1F, 1F);
            ei.setDead();
        }
    }
    
    private void killEntityTorch(EntityItem ei, String sound)
    {
        ei.worldObj.playSoundEffect(ei.posX, ei.posY, ei.posZ, sound, 0.6F, 1F);
        ei.getEntityItem().setItemDamage(0);
        PacketSender.sendEntityPacket(ei, (byte)6);
    }
    
    private int getMaxLifespan()
    {
        return ConfigCommon.torchLifespanMax;
    }
}
