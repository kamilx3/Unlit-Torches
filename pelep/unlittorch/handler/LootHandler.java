package pelep.unlittorch.handler;

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;
import static net.minecraftforge.common.ChestGenHooks.MINESHAFT_CORRIDOR;
import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_CORRIDOR;
import static net.minecraftforge.common.ChestGenHooks.VILLAGE_BLACKSMITH;
import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import pelep.unlittorch.config.ConfigCommon;

public class LootHandler
{
    private static void addChestLoot(String category, int id, int md, int min, int max, int weight)
    {
        addChestLoot(category, new ItemStack(id, 1, md), min, max, weight);
    }
    
    private static void addChestLoot(String category, ItemStack ist, int min, int max, int weight)
    {
        addChestLoot(category, new WeightedRandomChestContent(ist, min, max, weight));
    }
    
    private static void addChestLoot(String category, WeightedRandomChestContent item)
    {
        ChestGenHooks.addItem(category, item);
    }
    
    public static void registerChestLoot()
    {
        ItemStack lantern = new ItemStack (ConfigCommon.blockIdLanternUnlit, 1, ConfigCommon.lanternLifespanMax / 2);
        NBTTagCompound tag = new NBTTagCompound();
        
        tag.setBoolean("handle", true);
        lantern.setTagCompound(tag);
        
        addChestLoot(DUNGEON_CHEST, lantern.copy(), 1, 1, 50);
        addChestLoot(DUNGEON_CHEST, ConfigCommon.itemIdTinderboxFS, 32, 1, 1, 60);
        addChestLoot(DUNGEON_CHEST, ConfigCommon.itemIdTinderbox, 0, 1, 1, 70);
        addChestLoot(DUNGEON_CHEST, ConfigCommon.itemIdLanternFuel, 2, 2, 4, 100);
        
        lantern.setItemDamage(0);
        
        addChestLoot(STRONGHOLD_CORRIDOR, lantern.copy(), 1, 1, 5);
        addChestLoot(STRONGHOLD_CORRIDOR, ConfigCommon.itemIdTinderboxFS, 0, 1, 1, 7);
        addChestLoot(STRONGHOLD_CORRIDOR, ConfigCommon.itemIdTinderbox, 0, 1, 1, 8);
        addChestLoot(STRONGHOLD_CORRIDOR, ConfigCommon.itemIdLanternFuel, 2, 3, 6, 10);
        
        lantern.setItemDamage((ConfigCommon.lanternLifespanMax / 3) * 2);
        lantern.stackTagCompound.setBoolean("handle", false);
        
        addChestLoot(MINESHAFT_CORRIDOR, lantern.copy(), 1, 1, 5);
        addChestLoot(MINESHAFT_CORRIDOR, ConfigCommon.itemIdTinderboxFS, 48, 1, 1, 10);
        addChestLoot(MINESHAFT_CORRIDOR, ConfigCommon.itemIdTinderbox, 1, 1, 1, 12);
        addChestLoot(MINESHAFT_CORRIDOR, ConfigCommon.itemIdLanternFuel, 2, 1, 3, 15);
        addChestLoot(MINESHAFT_CORRIDOR, ConfigCommon.itemIdLanternFuel, 3, 1, 2, 15);
        
        addChestLoot(VILLAGE_BLACKSMITH, ConfigCommon.itemIdTinderboxFS, 64, 1, 1, 4);
        addChestLoot(VILLAGE_BLACKSMITH, ConfigCommon.itemIdTinderbox, 40, 1, 1, 5);
        addChestLoot(VILLAGE_BLACKSMITH, ConfigCommon.itemIdLanternFuel, 1, 1, 2, 5);
        addChestLoot(VILLAGE_BLACKSMITH, ConfigCommon.itemIdLanternFuel, 3, 1, 3, 10);
        
        addChestLoot(BONUS_CHEST, ConfigCommon.itemIdLanternFuel, 2, 1, 2, 1);
        addChestLoot(BONUS_CHEST, ConfigCommon.itemIdLanternFuel, 3, 1, 1, 5);
    }
}
