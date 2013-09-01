package pelep.unlittorch;

import java.util.List;

import pelep.unlittorch.config.ConfigCommon;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UnlitTorchInjected
{
    @SideOnly(Side.CLIENT)
    public static boolean sameItemHeld(boolean b, ItemStack ist1, ItemStack ist2)
    {
        return (ist1.itemID != 50 || ist2.itemID != 50) ? b : !(ist1.getItemDamage() == 0 ^ ist2.getItemDamage() == 0);
    }

    @SideOnly(Side.CLIENT)
    public static boolean cancelRenderUpdate(int slot, ItemStack ist2, EntityClientPlayerMP p)
    {
        ItemStack ist1 = p.getCurrentEquippedItem();
        
        if (ist1 == null || ist2 == null || slot != p.inventory.currentItem)
        {
            return false;
        }
        else if (ist1.itemID == ist2.itemID && (ist1.itemID == 50 || ist1.itemID == ConfigCommon.blockIdLanternLit))
        {
            return true;
        }
        else if (ist1.itemID == ConfigCommon.blockIdLanternUnlit && ist2.itemID == ConfigCommon.blockIdLanternLit)
        {
            return true;
        }
        else
        {
            return ist1.itemID == ConfigCommon.blockIdLanternLit && ist2.itemID == ConfigCommon.blockIdLanternUnlit;
        }
    }
    
    public static boolean combineStacks(List inv, ItemStack ist1, int start, int size, boolean fromInv)
    {
        if (ist1 == null || ist1.itemID != 50)
        {
            return false;
		}
        
        boolean change = false;
        int i = fromInv ? (size - 1) : start;
        
        Slot slot;
        ItemStack ist2;
        
        while (ist1.stackSize > 0 && (!fromInv && i < size || fromInv && i >= start))
        {
            slot = (Slot) inv.get(i);
            ist2 = slot.getStack();
            
            if (ist2 != null && combineStacks(ist1, ist2) && ItemStack.areItemStackTagsEqual(ist1, ist2))
            {
                int nsize = ist2.stackSize + ist1.stackSize;
                
                if (nsize <= ist1.getMaxStackSize())
                {
                    ist1.stackSize = 0;
                    ist2.stackSize = nsize;
                    slot.onSlotChanged();
                    change = true;
				}
                else if (ist2.stackSize < ist1.getMaxStackSize())
                {
                    ist1.stackSize -= ist1.getMaxStackSize() - ist2.stackSize;
                    ist2.stackSize = ist1.getMaxStackSize();
                    slot.onSlotChanged();
                    change = true;
                }
            }
            
            if (fromInv)
            {
                i--;
            }
            else
            {
                i++;
            }
        }
        
        if (ist1.stackSize > 0)
        {
            i = fromInv ? (size - 1) : start;
            
            while (!fromInv && i < size || fromInv && i >= start)
            {
                slot = (Slot) inv.get(i);
                ist2 = slot.getStack();
                
                if (ist2 == null)
                {
                    slot.putStack(ist1.copy());
                    slot.onSlotChanged();
                    ist1.stackSize = 0;
                    change = true;
                    break;
                }
                
                if (fromInv)
                {
                    i--;
                }
                else
                {
                    i++;
                }
            }
        }
        
        return change;	
    }

    private static boolean combineStacks(ItemStack ist1, ItemStack ist2)
    {
        if (ist1.itemID != 50 || ist2.itemID != 50)
        {
            return false;
        }

        int d1 = ist1.getItemDamage();
        int d2 = ist2.getItemDamage();
        
        if (d1 == d2)
        {
            return true;
        }
        else if (d1 == 0 ^ d2 == 0)
        {
            return false;
        }
        else
        {
            int nd = Math.min(d1, d2);
            ist1.setItemDamage(nd);
            ist2.setItemDamage(nd);
            return true;
        }
    }
    
    public static boolean addStack(ItemStack ist, InventoryPlayer invp, EntityPlayer p)
    {
        if (ist == null || ist.stackSize == 0 || ist.itemID != 50)
        {
            return false;
        }
        
        try
        {
            int i;
            
            do
            {
                i = ist.stackSize;
                ist.stackSize = storePartial(ist, invp);
            }
            while (ist.stackSize > 0 && ist.stackSize < i);
            
            if (ist.stackSize == i && p.capabilities.isCreativeMode)
            {
                ist.stackSize = 0;
                return true;
            }
            else
            {
                return ist.stackSize < i;
            }
        }
        catch (Throwable t)
        {
            CrashReport cr = CrashReport.makeCrashReport(t, "Adding item to inventory");
            CrashReportCategory crc = cr.makeCategory("Item being added");
            crc.addCrashSection("Item ID", Integer.valueOf(ist.itemID));
            crc.addCrashSection("Item data", Integer.valueOf(ist.getItemDamage()));
            throw new ReportedException(cr);
        }
    }
    
    private static int storePartial(ItemStack ist, InventoryPlayer invp)
    {
        int id = ist.itemID;
        int size = ist.stackSize;
        int slot;
        
        slot = storePartial(ist, invp.mainInventory, invp.getInventoryStackLimit());
        
        if (slot < 0)
        {
            slot = invp.getFirstEmptyStack();
        }
        
        if (slot < 0)
        {
            return size;
        }
        else
        {
            if (invp.mainInventory[slot] == null)
            {
                invp.mainInventory[slot] = new ItemStack(id, 0, ist.getItemDamage());
                
                if (ist.hasTagCompound())
                {
                    invp.mainInventory[slot].setTagCompound((NBTTagCompound)ist.getTagCompound().copy());
                }
            }
            
            int space = size;
            
            if (size > invp.mainInventory[slot].getMaxStackSize() - invp.mainInventory[slot].stackSize)
            {
                space = invp.mainInventory[slot].getMaxStackSize() - invp.mainInventory[slot].stackSize;
            }
            
            if (space > invp.getInventoryStackLimit() - invp.mainInventory[slot].stackSize)
            {
                space = invp.getInventoryStackLimit() - invp.mainInventory[slot].stackSize;
            }
            
            if (space == 0)
            {
                return size;
            }
            else
            {
                size -= space;
                invp.mainInventory[slot].stackSize += space;
                invp.mainInventory[slot].animationsToGo = 5;
                return size;
            }
        }
    }
    
    private static int storePartial(ItemStack ist, ItemStack[] inv, int limit)
    {
        for (int i = 0; i < inv.length; i++)
        {
            if (inv[i] != null &&
                combineStacks(inv[i], ist) &&
                inv[i].stackSize < inv[i].getMaxStackSize() &&
                inv[i].stackSize < limit &&
                ItemStack.areItemStackTagsEqual(inv[i], ist))
            {
                return i;
            }
        }
        
        return -1;
    }
}
