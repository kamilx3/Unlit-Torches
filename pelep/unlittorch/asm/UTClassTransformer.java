package pelep.unlittorch.asm;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.tree.AbstractInsnNode.*;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pelep.unlittorch.handler.LogHandler;
import net.minecraft.launchwrapper.IClassTransformer;

public class UTClassTransformer implements IClassTransformer
{
    private static ClassMap class_ItemStack = new ClassMap("yd");

    //World
    private static ClassMap class_World = new ClassMap("abv");
    private static ClassMap class_EnumSkyBlock = new ClassMap("acg");
    private static MethodMap method_computeLight = new MethodMap(class_World, "a", "(III" + class_EnumSkyBlock.dname + ")I");
    
    //ItemRenderer
    private static ClassMap class_ItemRenderer = new ClassMap("bfg");
    private static ClassMap class_Minecraft = new ClassMap("ats");
    private static ClassMap class_EntityCPMP = new ClassMap("bdf");
    private static MethodMap method_updateEquip = new MethodMap(class_ItemRenderer, "a", "()V");
    private static MethodMap method_getEquipped = new MethodMap(class_EntityCPMP, "bx", "()" + class_ItemStack.dname);
    private static FieldMap field_equipItemSlot = new FieldMap(class_ItemRenderer, "j", "I");
    private static FieldMap field_itemToRender = new FieldMap(class_ItemRenderer, "f", class_ItemStack.dname);
    private static FieldMap field_minecraft = new FieldMap(class_ItemRenderer, "e", class_Minecraft.dname);
    private static FieldMap field_theplayer = new FieldMap(class_Minecraft, "h", class_EntityCPMP.dname);
    private static FieldMap field_equipProg = new FieldMap(class_ItemRenderer, "g", "F");
    private static FieldMap field_prevEquipProg = new FieldMap(class_ItemRenderer, "h", "F");

    //Container
    private static ClassMap class_Container = new ClassMap("ux");
    private static MethodMap method_mergeStack = new MethodMap(class_Container, "a", "(" + class_ItemStack.dname + "IIZ)Z");
    private static FieldMap field_invSlots = new FieldMap(class_Container, "c", "Ljava/util/List;");
    
    //InventoryPlayer
    private static ClassMap class_InvPlayer = new ClassMap("uc");
    private static ClassMap class_EntityPlayer = new ClassMap("ue");
    private static MethodMap method_addItemStack = new MethodMap(class_InvPlayer, "a", "(" + class_ItemStack.dname + ")Z");
    private static FieldMap field_player = new FieldMap(class_InvPlayer, "d", class_EntityPlayer.dname);

    //PlayerControllerMP
    private static ClassMap class_PlayerController = new ClassMap("bcz");
    private static MethodMap method_sameToolAndBlock = new MethodMap(class_PlayerController, "a", "(III)Z");
    private static FieldMap field_85183_f = new FieldMap(class_PlayerController, "f", class_ItemStack.dname);
    
    //Inject
    private static ClassMap class_UTInjected = new ClassMap("pelep.unlittorch.UnlitTorchInjected");
    private static ClassMap class_LightingHandler = new ClassMap("pelep.unlittorch.handler.LightingHandler");
    
    private static MethodMap method_lightLevel = new MethodMap(class_LightingHandler, "lightLevel", "(" + class_World.dname + "IIII)I");
    private static MethodMap method_cancelRenderUpdate = new MethodMap(class_UTInjected, "cancelRenderUpdate", "(I" + class_ItemStack.dname + class_EntityCPMP.dname + ")Z");
    private static MethodMap method_combineStacks = new MethodMap(class_UTInjected, "combineStacks", "(Ljava/util/List;" + class_ItemStack.dname + "IIZ)Z");
    private static MethodMap method_addStack = new MethodMap(class_UTInjected, "addStack", "(" + class_ItemStack.dname + class_InvPlayer.dname + class_EntityPlayer.dname + ")Z");
    private static MethodMap method_sameItemHeld = new MethodMap(class_UTInjected, "sameItemHeld", "(Z" + class_ItemStack.dname + class_ItemStack.dname + ")Z");

    public static void setDeobf()
    {
        LogHandler.info("Setting mappings for a deobfuscated environment");
        
        class_ItemStack = new ClassMap("net.minecraft.item.ItemStack");
        
        //World
        class_World = new ClassMap("net.minecraft.world.World");
        class_EnumSkyBlock = new ClassMap("net.minecraft.world.EnumSkyBlock");
        method_computeLight = new MethodMap(class_World, "computeLightValue", "(III" + class_EnumSkyBlock.dname + ")I");
        
        //ItemRenderer
        class_ItemRenderer = new ClassMap("net.minecraft.client.renderer.ItemRenderer");
        class_Minecraft = new ClassMap("net.minecraft.client.Minecraft");
        class_EntityCPMP = new ClassMap("net.minecraft.client.entity.EntityClientPlayerMP");
        method_updateEquip = new MethodMap(class_ItemRenderer, "updateEquippedItem", "()V");
        method_getEquipped = new MethodMap(class_EntityCPMP, "getCurrentEquippedItem", "()" + class_ItemStack.dname);
        field_equipItemSlot = new FieldMap(class_ItemRenderer, "equippedItemSlot", "I");
        field_itemToRender = new FieldMap(class_ItemRenderer, "itemToRender", class_ItemStack.dname);
        field_minecraft = new FieldMap(class_ItemRenderer, "mc", class_Minecraft.dname);
        field_theplayer = new FieldMap(class_Minecraft, "thePlayer", class_EntityCPMP.dname);
        field_equipProg = new FieldMap(class_ItemRenderer, "equippedProgress", "F");
        field_prevEquipProg = new FieldMap(class_ItemRenderer, "prevEquippedProgress", "F");

        //Container
        class_Container = new ClassMap("net.minecraft.inventory.Container");
        method_mergeStack = new MethodMap(class_Container, "mergeItemStack", "(" + class_ItemStack.dname + "IIZ)Z");
        field_invSlots = new FieldMap(class_Container, "inventorySlots", "Ljava/util/List;");
        
        //InventoryPlayer
        class_InvPlayer = new ClassMap("net.minecraft.entity.player.InventoryPlayer");
        class_EntityPlayer = new ClassMap("net.minecraft.entity.player.EntityPlayer");
        method_addItemStack = new MethodMap(class_InvPlayer, "addItemStackToInventory", "(" + class_ItemStack.dname + ")Z");
        field_player = new FieldMap(class_InvPlayer, "player", class_EntityPlayer.dname);

        //PlayerControllerMP
        class_PlayerController = new ClassMap("net.minecraft.client.multiplayer.PlayerControllerMP");
        method_sameToolAndBlock = new MethodMap(class_PlayerController, "sameToolAndBlock", "(III)Z");
        field_85183_f = new FieldMap(class_PlayerController, "field_85183_f", class_ItemStack.dname);
        
        //Inject
        method_lightLevel = new MethodMap(class_LightingHandler, "lightLevel", "(" + class_World.dname + "IIII)I");
        method_cancelRenderUpdate = new MethodMap(class_UTInjected, "cancelRenderUpdate", "(I" + class_ItemStack.dname + class_EntityCPMP.dname + ")Z");
        method_combineStacks = new MethodMap(class_UTInjected, "combineStacks", "(Ljava/util/List;" + class_ItemStack.dname + "IIZ)Z");
        method_addStack = new MethodMap(class_UTInjected, "addStack", "(" + class_ItemStack.dname + class_InvPlayer.dname + class_EntityPlayer.dname + ")Z");
        method_sameItemHeld = new MethodMap(class_UTInjected, "sameItemHeld", "(Z" + class_ItemStack.dname + class_ItemStack.dname + ")Z");
    }
    
    @Override
    public byte[] transform(String name, String tname, byte[] b)
    {
        try
        {
            if (class_World.matches(name) && FMLLaunchHandler.side().isClient())
            {
                b = this.modifyWorld(b);
            }
            else if (class_ItemRenderer.matches(name))
            {
                b = this.modifyItemRenderer(b);
            }
            else if (class_Container.matches(name))
            {
                b = this.modifyContainer(b);
            }
            else if (class_InvPlayer.matches(name))
            {
                b = this.modifyInventoryPlayer(b);
            }
            else if (class_PlayerController.matches(name))
            {
                b = this.modifyPlayerController(b);
            }
        }
        catch (Exception e)
        {
            LogHandler.severe("Failed to modify!");
            throw new RuntimeException("UnlitTorch encountered a problem while trying to modify " + name + ".class", e);
        }
        
        return b;
    }

    @SideOnly(Side.CLIENT)
    private byte[] modifyWorld(byte[] b)
    {
        boolean modified = false;
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(b);
        cr.accept(cn, 0);
        
        LogHandler.info("Inside %s.class", class_World.name);
        
        modify:
        for (Object o : cn.methods)
        {
            MethodNode mn = (MethodNode) o;
            
            if (method_computeLight.matches(mn))
            {
                LogHandler.info("Inside method '%s'", method_computeLight.name);
                
                Iterator<AbstractInsnNode> insns = mn.instructions.iterator();
                
                while (insns.hasNext())
                {
                    AbstractInsnNode insn = insns.next();
                    
                    if (insn.getType() == VAR_INSN)
                    {
                        VarInsnNode vi = (VarInsnNode) insn;
                        
                        if (vi.var == 7 && vi.getOpcode() == ISTORE)
                        {
                            InsnList list = new InsnList();

                            list.add(new LabelNode());
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(ILOAD, 7));
                            list.add(new VarInsnNode(ILOAD, 1));
                            list.add(new VarInsnNode(ILOAD, 2));
                            list.add(new VarInsnNode(ILOAD, 3));
                            list.add(method_lightLevel.toInsn(INVOKESTATIC));
                            list.add(new VarInsnNode(ISTORE, 7));
                            
                            LogHandler.info("Inserting additional instructions");
                            
                            mn.instructions.insert(insn, list);
                            modified = true;
                            break modify;
                        }
                    }
                }
            }
        }

        if (modified)
        {
            LogHandler.info("Writing class bytes...");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            b = cw.toByteArray();
            LogHandler.info("Done!");
        }
        else
        {
            LogHandler.severe("Failed to modify!");
        }
        
        return b;
    }
    
    private byte[] modifyItemRenderer(byte[] b)
    {
        boolean modified = false;
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(b);
        cr.accept(cn, 0);
        
        LogHandler.info("Inside %s.class", class_ItemRenderer.name);
        
        modify:
        for (Object o : cn.methods)
        {
            MethodNode mn = (MethodNode) o;
            
            if (method_updateEquip.matches(mn))
            {
                LogHandler.info("Inside method '%s'", method_updateEquip.name);

                InsnList list = new InsnList();
                JumpInsnNode ji = new JumpInsnNode(IFEQ, null);
                
                list.add(new LabelNode());
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_equipItemSlot.toInsn(GETFIELD));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_itemToRender.toInsn(GETFIELD));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_minecraft.toInsn(GETFIELD));
                list.add(field_theplayer.toInsn(GETFIELD));
                list.add(method_cancelRenderUpdate.toInsn(INVOKESTATIC));
                list.add(ji);
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_minecraft.toInsn(GETFIELD));
                list.add(field_theplayer.toInsn(GETFIELD));
                list.add(method_getEquipped.toInsn(INVOKEVIRTUAL));
                list.add(field_itemToRender.toInsn(PUTFIELD));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_equipProg.toInsn(GETFIELD));
                list.add(field_prevEquipProg.toInsn(PUTFIELD));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new InsnNode(FCONST_1));
                list.add(field_equipProg.toInsn(PUTFIELD));
                list.add(new InsnNode(RETURN));
                
                Iterator<AbstractInsnNode> insns = mn.instructions.iterator();
                
                while (insns.hasNext())
                {
                    AbstractInsnNode insn = insns.next();
                    
                    if (insn.getType() == LABEL)
                    {
                        ji.label = (LabelNode) insn;
                        FrameNode frame = new FrameNode(F_SAME, 0, null, 0, null);
                        LogHandler.info("Inserting additional instructions");
                        
                        if (insn.getNext() != null && insn.getNext().getType() == LINE)
                        {
                            mn.instructions.insert(insn.getNext(), frame);
                        }
                        else
                        {
                            mn.instructions.insert(insn, frame);
                        }
                        
                        mn.instructions.insertBefore(mn.instructions.getFirst(), list);
                        modified = true;
                        break modify;
                    }
                }
            }
        }
        
        if (modified)
        {
            LogHandler.info("Writing class bytes...");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            b = cw.toByteArray();
            LogHandler.info("Done!");
        }
        else
        {
            LogHandler.severe("Failed to modify!");
        }
        
        return b;
    }
    
    private byte[] modifyContainer(byte[] b)
    {
        boolean modified = false;
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(b);
        cr.accept(cn, 0);
        
        LogHandler.info("Inside %s.class", class_Container.name);
        
        modify:
        for (Object o : cn.methods)
        {
            MethodNode mn = (MethodNode) o;
            
            if (method_mergeStack.matches(mn))
            {
                LogHandler.info("Inside method '%s'", method_mergeStack.name);
                
                Iterator<AbstractInsnNode> insns = mn.instructions.iterator();
                InsnList list = new InsnList();
                JumpInsnNode ji = new JumpInsnNode(IFEQ, null);
                
                list.add(new LabelNode());
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_invSlots.toInsn(GETFIELD));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(method_combineStacks.toInsn(INVOKESTATIC));
                list.add(ji);
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IRETURN));
                
                while (insns.hasNext())
                {
                    AbstractInsnNode insn = insns.next();
                    
                    if (insn.getType() == LABEL)
                    {
                        ji.label = (LabelNode) insn;
                        FrameNode frame = new FrameNode(F_SAME, 0, null, 0, null);
                        LogHandler.info("Inserting additional instructions");
                        
                        if (insn.getNext() != null && insn.getNext().getType() == LINE)
                        {
                            mn.instructions.insert(insn.getNext(), frame);
                        }
                        else
                        {
                            mn.instructions.insert(insn, frame);
                        }
                        
                        mn.instructions.insertBefore(mn.instructions.getFirst(), list);
                        modified = true;
                        break modify;
                    }
                }
            }
        }

        if (modified)
        {
            LogHandler.info("Writing class bytes...");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            b = cw.toByteArray();
            LogHandler.info("Done!");
        }
        else
        {
            LogHandler.severe("Failed to modify!");
        }
        
        return b;
    }
    
    private byte[] modifyInventoryPlayer(byte[] b)
    {
        boolean modified = false;
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(b);
        cr.accept(cn, 0);
        
        LogHandler.info("Inside %s.class", class_InvPlayer.name);
        
        modify:
        for (Object o : cn.methods)
        {
            MethodNode mn = (MethodNode) o;
            
            if (method_addItemStack.matches(mn))
            {
                LogHandler.info("Inside method '%s'", method_addItemStack.name);
                
                Iterator<AbstractInsnNode> insns = mn.instructions.iterator();
                InsnList list = new InsnList();
                JumpInsnNode ji = new JumpInsnNode(IFEQ, null);
                
                list.add(new LabelNode());
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(field_player.toInsn(GETFIELD));
                list.add(method_addStack.toInsn(INVOKESTATIC));
                list.add(ji);
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IRETURN));
                
                while (insns.hasNext())
                {
                    AbstractInsnNode insn = insns.next();
                    
                    if (insn.getType() == LABEL)
                    {
                        ji.label = (LabelNode) insn;
                        FrameNode frame = new FrameNode(F_SAME, 0, null, 0, null);
                        LogHandler.info("Inserting additional instructions");
                        
                        if (insn.getNext() != null && insn.getNext().getType() == LINE)
                        {
                            mn.instructions.insert(insn.getNext(), frame);
                        }
                        else
                        {
                            mn.instructions.insert(insn, frame);
                        }
                        
                        mn.instructions.insertBefore(mn.instructions.getFirst(), list);
                        modified = true;
                        break modify;
                    }
                }
            }
        }

        if (modified)
        {
            LogHandler.info("Writing class bytes...");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            b = cw.toByteArray();
            LogHandler.info("Done!");
        }
        else
        {
            LogHandler.severe("Failed to modify!");
        }
        
        return b;
    }

    private byte[] modifyPlayerController(byte[] b)
    {
        boolean modified = false;
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(b);
        cr.accept(cn, 0);

        LogHandler.info("Inside %s.class", class_PlayerController.name);

        modify:
        for (Object o : cn.methods)
        {
            MethodNode mn = (MethodNode) o;

            if (method_sameToolAndBlock.matches(mn))
            {
                LogHandler.info("Inside method '%s'", method_sameToolAndBlock.name);

                Iterator<AbstractInsnNode> insns = mn.instructions.iterator();
                boolean first = true;

                while (insns.hasNext())
                {
                    AbstractInsnNode insn = insns.next();

                    if (insn.getType() == VAR_INSN)
                    {
                        VarInsnNode var = (VarInsnNode) insn;

                        if (var.getOpcode() == ISTORE && var.var == 5)
                        {
                            if (first)
                            {
                                first = false;
                            }
                            else
                            {
                                LogHandler.info("Inserting additional instructions");
                                InsnList list = new InsnList();

                                list.add(new LabelNode());
                                list.add(new VarInsnNode(ILOAD, 5));
                                list.add(new VarInsnNode(ALOAD, 4));
                                list.add(new VarInsnNode(ALOAD, 0));
                                list.add(field_85183_f.toInsn(GETFIELD));
                                list.add(method_sameItemHeld.toInsn(INVOKESTATIC));
                                list.add(new VarInsnNode(ISTORE, 5));

                                mn.instructions.insert(insn, list);
                                modified = true;
                                break modify;
                            }
                        }
                    }
                }
            }
        }

        if (modified)
        {
            LogHandler.info("Writing class bytes...");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            b = cw.toByteArray();
            LogHandler.info("Done!");
        }
        else
        {
            LogHandler.severe("Failed to modify!");
        }

        return b;
    }
    
    private static class ClassMap
    {
        private final String name;
        private final String iname;
        private final String dname;
        
        private ClassMap(String name)
        {
            this.name = name;
            this.iname = name.replace(".", "/");
            this.dname = "L" + this.iname + ";";
        }
        
        private boolean matches(String name)
        {
            return this.name.equals(name) || this.iname.equals(name);
        }
    }
    
    private static class MethodMap
    {
        private final String owner;
        private final String name;
        private final String desc;
        
        private MethodMap(ClassMap owner, String name, String desc)
        {
            this.owner = owner.iname;
            this.name = name;
            this.desc = desc;
        }
        
        private MethodMap(String owner, String name, String desc)
        {
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }
        
        private boolean matches(MethodNode mn)
        {
            return mn.name.equals(this.name) && mn.desc.equals(this.desc);
        }

        private boolean matches(MethodInsnNode mi)
        {
            return mi.owner.equals(this.owner) && mi.name.equals(this.name) && mi.desc.equals(this.desc);
        }
        
        private MethodInsnNode toInsn(int opcode)
        {
            return new MethodInsnNode(opcode, this.owner, this.name, this.desc);
        }
    }

    private static class FieldMap
    {
        private final String owner;
        private final String name;
        private final String desc;
        
        private FieldMap(ClassMap owner, String name, String desc)
        {
            this.owner = owner.iname;
            this.name = name;
            this.desc = desc;
        }
        
        private FieldMap(String owner, String name, String desc)
        {
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }
        
        private FieldInsnNode toInsn(int opcode)
        {
            return new FieldInsnNode(opcode, this.owner, this.name, this.desc);
        }
    }
}
