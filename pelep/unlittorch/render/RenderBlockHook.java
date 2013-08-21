package pelep.unlittorch.render;

import static pelep.unlittorch.render.RenderHelper.*;
import pelep.unlittorch.block.BlockLanternHook;
import pelep.unlittorch.proxy.ProxyCommon;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockHook implements ISimpleBlockRenderingHandler
{
    private static Face[] back = {new Face(), new Face(), new Face(), new Face(), new Face()};
    private static Face[] bend = {new Face(), new Face(), new Face()};
    private static Face[] shank = {new Face(), new Face(), new Face(), new Face()};
    
    @Override
    public void renderInventoryBlock(Block block, int md, int rId, RenderBlocks rb)
    {
    }
    
    @Override
    public boolean shouldRender3DInInventory()
    {
        return false;
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int fx, int fy, int fz, Block block, int rId, RenderBlocks rb)
    {
        int md = world.getBlockMetadata(fx, fy, fz);
        
        if (md > 7)
        {
            return false;
        }
        
        int m = block.colorMultiplier(world, fx, fy, fz);
        float rm = (m >> 16 & 255) / 255F;
        float gm = (m >> 8 & 255) / 255F;
        float bm = (m & 255) / 255F;
        
        if (EntityRenderer.anaglyphEnable)
        {
            float r = (rm * 30F + gm * 59F + bm * 11F) / 100F;
            float g = (rm * 30F + gm * 70F) / 100F;
            float b = (rm * 30F + bm * 70F) / 100F;
            rm = r;
            gm = g;
            bm = b;
        }
        
        Icon icon = BlockLanternHook.icon_hook;
        
        float x = fx + 0.5F;
        float y = fy + 0.5F;
        float z = fz + 0.5F;
        
        Tessellator.instance.addTranslation(x, y, z);
        
        this.setUpBack(icon, md);
        this.setUpShank(icon, md);
        this.setUpBend(icon, md);
        
        for (Face face : back)
        {
            face.addFaceWithColorMultiplier(rb, block, world, fx, fy, fz, rm, gm, bm);
        }
        
        for (Face face : shank)
        {
            face.addFaceWithColorMultiplier(rb, block, world, fx, fy, fz, rm, gm, bm);
        }
        
        for (Face face : bend)
        {
            face.addFaceWithColorMultiplier(rb, block, world, fx, fy, fz, rm, gm, bm);
        }
        
        Tessellator.instance.addTranslation(-x, -y, -z);
        
        return true;
    }
    
    private void setUpBack(Icon icon, int md)
    {
        switch (md)
        {
        case 0:
            back[0].setValues(TOP, 2, -0.484375D, 0.3500D, 0D, 0.1875D, 0.03125D);
            back[1].setValues(BOT, 2, -0.484375D, 0.1625D, 0D, 0.1875D, 0.03125D);
            back[2].setValues(STH, 2, -0.484375D, 0.25625D, 0.09375D, 0.1875D, 0.03125D);
            back[3].setValues(NTH, 2, -0.484375D, 0.25625D, -0.09375D, 0.1875D, 0.03125D);
            back[4].setValues(EST, 2, -0.46875D, 0.25625D, 0D, 0.1875D, 0.1875D);
            break;
        case 1:
            back[0].setValues(TOP, 0, 0.484375D, 0.3500D, 0D, 0.1875D, 0.03125D);
            back[1].setValues(BOT, 0, 0.484375D, 0.1625D, 0D, 0.1875D, 0.03125D);
            back[2].setValues(NTH, 2, 0.484375D, 0.25625D, -0.09375D, 0.1875D, 0.03125D);
            back[3].setValues(STH, 2, 0.484375D, 0.25625D, 0.09375D, 0.1875D, 0.03125D);
            back[4].setValues(WST, 2, 0.46875D, 0.25625D, 0D, 0.1875D, 0.1875D);
            break;
        case 2:
            back[0].setValues(TOP, 1, 0D, 0.3500D, -0.484375D, 0.03125D, 0.1875D);
            back[1].setValues(BOT, 3, 0D, 0.1625D, -0.484375D, 0.03125D, 0.1875D);
            back[2].setValues(WST, 2, -0.09375D, 0.25625D, -0.484375D, 0.1875D, 0.03125D);
            back[3].setValues(EST, 2, 0.09375D, 0.25625D, -0.484375D, 0.1875D, 0.03125D);
            back[4].setValues(STH, 2, 0D, 0.25625D, -0.46875D, 0.1875D, 0.1875D);
            break;
        case 3:
            back[0].setValues(TOP, 3, 0D, 0.3500D, 0.484375D, 0.03125D, 0.1875D);
            back[1].setValues(BOT, 1, 0D, 0.1625D, 0.484375D, 0.03125D, 0.1875D);
            back[2].setValues(EST, 2, 0.09375D, 0.25625D, 0.484375D, 0.1875D, 0.03125D);
            back[3].setValues(WST, 2, -0.09375D, 0.25625D, 0.484375D, 0.1875D, 0.03125D);
            back[4].setValues(NTH, 2, 0D, 0.25625D, 0.46875D, 0.1875D, 0.1875D);
            break;
        case 4:
            back[0].setValues(EST, 2, 0.09375 - 0.015625D, 0.484375D, 0D, 0.03125D, 0.1875D);
            back[1].setValues(WST, 0, -0.09375 - 0.015625D, 0.484375D, 0D, 0.03125D, 0.1875D);
            back[2].setValues(STH, 1, -0.015625D, 0.484375D, 0.09375D, 0.03125D, 0.1875D);
            back[3].setValues(NTH, 3, -0.015625D, 0.484375D, -0.09375D, 0.03125D, 0.1875D);
            back[4].setValues(BOT, 2, -0.015625D, 0.468750D, 0D, 0.1875D, 0.1875D);
            break;
        case 5:
            back[0].setValues(WST, 2, -0.09375 + 0.015625D, 0.484375D, 0D, 0.03125D, 0.1875D);
            back[1].setValues(EST, 0, 0.09375 + 0.015625D, 0.484375D, 0D, 0.03125D, 0.1875D);
            back[2].setValues(NTH, 1, 0.015625D, 0.484375D, -0.09375D, 0.03125D, 0.1875D);
            back[3].setValues(STH, 3, 0.015625D, 0.484375D, 0.09375D, 0.03125D, 0.1875D);
            back[4].setValues(BOT, 0, 0.015625D, 0.468750D, 0D, 0.1875D, 0.1875D);
            break;
        case 6:
            back[0].setValues(STH, 2, 0D, 0.484375D, 0.09375 - 0.015625D, 0.03125D, 0.1875D);
            back[1].setValues(NTH, 0, 0D, 0.484375D, -0.09375 - 0.015625D, 0.03125D, 0.1875D);
            back[2].setValues(WST, 1, -0.09375D, 0.484375D, -0.015625D, 0.03125D, 0.1875D);
            back[3].setValues(EST, 3, 0.09375D, 0.484375D, -0.015625D, 0.03125D, 0.1875D);
            back[4].setValues(BOT, 3, 0D, 0.468750D, -0.015625D, 0.1875D, 0.1875D);
            break;
        default:
            back[0].setValues(NTH, 2, 0D, 0.484375D, -0.09375 + 0.015625D, 0.03125D, 0.1875D);
            back[1].setValues(STH, 0, 0D, 0.484375D, 0.09375 + 0.015625D, 0.03125D, 0.1875D);
            back[2].setValues(EST, 1, 0.09375D, 0.484375D, 0.015625D, 0.03125D, 0.1875D);
            back[3].setValues(WST, 3, -0.09375D, 0.484375D, 0.015625D, 0.03125D, 0.1875D);
            back[4].setValues(BOT, 1, 0D, 0.468750D, 0.015625D, 0.1875D, 0.1875D);
        }
        
        double u3 = icon.getInterpolatedU(3D);
        double u4 = icon.getInterpolatedU(4D);
        double u5 = icon.getInterpolatedU(5D);
        double u11 = icon.getInterpolatedU(11D);
        double u12 = icon.getInterpolatedU(12D);
        double u13 = icon.getInterpolatedU(13D);
        
        double v0 = icon.getInterpolatedV(0D);
        double v1 = icon.getInterpolatedV(1D);
        double v2 = icon.getInterpolatedV(2D);
        double v8 = icon.getInterpolatedV(8D);
        double v9 = icon.getInterpolatedV(9D);
        double v10 = icon.getInterpolatedV(10D);
        
        back[0].setTextureBounds(u5, v0, u11, v1);
        back[1].setTextureBounds(u5, v9, u11, v10);
        back[2].setTextureBounds(u3, v2, u4, v8);
        back[3].setTextureBounds(u12, v2, u13, v8);
        back[4].setTextureBounds(u5, v2, u11, v8);
    }
    
    private void setUpShank(Icon icon, int md)
    {
        switch (md)
        {
        case 0:
            shank[0].setValues(TOP, 2, -0.421875D, 0.25625D, 0D, 0.03125D, 0.09375D);
            shank[1].setValues(BOT, 2, -0.406250D, 0.22500D, 0D, 0.03125D, 0.12500D);
            shank[2].setValues(STH, 2, -0.406250D, 0.271875D, 0.015625D, 0.09375D, 0.125D);
            shank[3].setValues(NTH, 2, -0.406250D, 0.271875D, -0.015625D, 0.09375D, 0.125D);
            break;
        case 1:
            shank[0].setValues(TOP, 0, 0.421875D, 0.25625D, 0D, 0.03125D, 0.09375D);
            shank[1].setValues(BOT, 0, 0.406250D, 0.22500D, 0D, 0.03125D, 0.12500D);
            shank[2].setValues(NTH, 2, 0.406250D, 0.271875D, -0.015625D, 0.09375D, 0.125D);
            shank[3].setValues(STH, 2, 0.406250D, 0.271875D, 0.015625D, 0.09375D, 0.125D);
            break;
        case 2:
            shank[0].setValues(TOP, 1, 0D, 0.25625D, -0.421875D, 0.09375D, 0.03125D);
            shank[1].setValues(BOT, 3, 0D, 0.22500D, -0.406250D, 0.12500D, 0.03125D);
            shank[2].setValues(WST, 2, -0.015625D, 0.271875D, -0.406250D, 0.09375D, 0.125D);
            shank[3].setValues(EST, 2, 0.015625D, 0.271875D, -0.406250D, 0.09375D, 0.125D);
            break;
        case 3:
            shank[0].setValues(TOP, 3, 0D, 0.25625D, 0.421875D, 0.09375D, 0.03125D);
            shank[1].setValues(BOT, 1, 0D, 0.22500D, 0.406250D, 0.12500D, 0.03125D);
            shank[2].setValues(EST, 2, 0.015625D, 0.271875D, 0.406250D, 0.09375D, 0.125D);
            shank[3].setValues(WST, 2, -0.015625D, 0.271875D, 0.406250D, 0.09375D, 0.125D);
            break;
        case 4:
            shank[0].setValues(EST, 2, 0.00000D - 0.015625D, 0.421875D, 0D, 0.09375D, 0.03125D);
            shank[1].setValues(WST, 0, -0.03125D - 0.015625D, 0.406250D, 0D, 0.12500D, 0.03125D);
            shank[2].setValues(STH, 1, 0D, 0.40625D, 0.015625D, 0.125D, 0.09375D);
            shank[3].setValues(NTH, 3, 0D, 0.40625D, -0.015625D, 0.125D, 0.09375D);
            break;
        case 5:
            shank[0].setValues(WST, 2, -0.00000D + 0.015625D, 0.421875D, 0D, 0.09375D, 0.03125D);
            shank[1].setValues(EST, 0, 0.03125D + 0.015625D, 0.406250D, 0D, 0.12500D, 0.03125D);
            shank[2].setValues(NTH, 1, 0D, 0.40625D, -0.015625D, 0.125D, 0.09375D);
            shank[3].setValues(STH, 3, 0D, 0.40625D, 0.015625D, 0.125D, 0.09375D);
            break;
        case 6:
            shank[0].setValues(STH, 2, 0D, 0.421875D, 0.00000D - 0.015625D, 0.09375D, 0.03125D);
            shank[1].setValues(NTH, 0, 0D, 0.406250D, -0.03125D - 0.015625D, 0.12500D, 0.03125D);
            shank[2].setValues(WST, 1, -0.015625D, 0.40625D, 0D, 0.125D, 0.09375D);
            shank[3].setValues(EST, 3, 0.015625D, 0.40625D, 0D, 0.125D, 0.09375D);
            break;
        default:
            shank[0].setValues(NTH, 2, 0D, 0.421875D, -0.00000D + 0.015625D, 0.09375D, 0.03125D);
            shank[1].setValues(STH, 0, 0D, 0.406250D, 0.03125D + 0.015625D, 0.12500D, 0.03125D);
            shank[2].setValues(EST, 1, 0.015625D, 0.40625D, 0D, 0.125D, 0.09375D);
            shank[3].setValues(WST, 3, -0.015625D, 0.40625D, 0D, 0.125D, 0.09375D);
		}
        
        double u0 = icon.getInterpolatedU(0D);
        double u4 = icon.getInterpolatedU(4D);
        double u9 = icon.getInterpolatedU(9D);
        double u13 = icon.getInterpolatedU(13D);
        double u14 = icon.getInterpolatedU(14D);
        double u15 = icon.getInterpolatedU(15D);
        double u16 = icon.getInterpolatedU(16D);
        
        double v12 = icon.getInterpolatedV(12D);
        double v13 = icon.getInterpolatedV(13D);
        double v16 = icon.getInterpolatedV(16D);
        
        shank[0].setTextureBounds(u15, v13, u16, v16);
        shank[1].setTextureBounds(u13, v12, u14, v16);
        shank[2].setTextureBounds(u0, v13, u4, v16);
        shank[3].setTextureBounds(u9, v13, u13, v16);
    }
    
    private void setUpBend(Icon icon, int md)
    {
        switch (md)
        {
        case 0:
            bend[0].setValues(TOP, 2, -0.359375D, 0.31875D, 0D, 0.03125D, 0.03125D);
            bend[1].setValues(EST, 2, -0.34375D, 0.271875D, 0D, 0.09375D, 0.03125D);
            bend[2].setValues(WST, 2, -0.375D, 0.2875D, 0D, 0.0625D, 0.03125D);
            break;
        case 1:
            bend[0].setValues(TOP, 0, 0.359375D, 0.31875D, 0D, 0.03125D, 0.03125D);
            bend[1].setValues(WST, 2, 0.34375D, 0.271875D, 0D, 0.09375D, 0.03125D);
            bend[2].setValues(EST, 2, 0.375D, 0.2875D, 0D, 0.0625D, 0.03125D);
            break;
        case 2:
            bend[0].setValues(TOP, 3, 0D, 0.31875D, -0.359375D, 0.03125D, 0.03125D);
            bend[1].setValues(STH, 2, 0D, 0.271875D, -0.34375D, 0.09375D, 0.03125D);
            bend[2].setValues(NTH, 2, 0D, 0.2875D, -0.375D, 0.0625D, 0.03125D);
            break;
        case 3:
            bend[0].setValues(TOP, 1, 0D, 0.31875D, 0.359375D, 0.03125D, 0.03125D);
            bend[1].setValues(NTH, 2, 0D, 0.271875D, 0.34375D, 0.09375D, 0.03125D);
            bend[2].setValues(STH, 2, 0D, 0.2875D, 0.375D, 0.0625D, 0.03125D);
            break;
        case 4:
            bend[0].setValues(EST, 2, 0.0625 - 0.015625D, 0.359375D, 0D, 0.03125D, 0.03125D);
            bend[1].setValues(BOT, 2, 0D, 0.34375D, 0D, 0.03125D, 0.09375D);
            bend[2].setValues(TOP, 0, 0.015625D, 0.375D, 0D, 0.03125D, 0.0625D);
            break;
        case 5:
            bend[0].setValues(WST, 2, -0.0625 + 0.015625D, 0.359375D, 0D, 0.03125D, 0.03125D);
            bend[1].setValues(BOT, 0, 0D, 0.34375D, 0D, 0.03125D, 0.09375D);
            bend[2].setValues(TOP, 2, -0.015625D, 0.375D, 0D, 0.03125D, 0.0625D);
            break;
        case 6:
            bend[0].setValues(STH, 2, 0D, 0.359375D, 0.0625 - 0.015625D, 0.03125D, 0.03125D);
            bend[1].setValues(BOT, 3, 0D, 0.34375D, 0D, 0.09375D, 0.03125D);
            bend[2].setValues(TOP, 1, 0D, 0.375D, 0.015625D, 0.0625D, 0.03125D);
            break;
        default:
            bend[0].setValues(NTH, 2, 0D, 0.359375D, -0.0625 + 0.015625D, 0.03125D, 0.03125D);
            bend[1].setValues(BOT, 1, 0D, 0.34375D, 0D, 0.09375D, 0.03125D);
            bend[2].setValues(TOP, 3, 0D, 0.375D, -0.015625D, 0.0625D, 0.03125D);
        }
        
        double u7 = icon.getInterpolatedU(7D);
        double u8 = icon.getInterpolatedU(8D);
        double u15 = icon.getInterpolatedU(15D);
        double u16 = icon.getInterpolatedU(16D);
        
        double v11 = icon.getInterpolatedV(11D);
        double v12 = icon.getInterpolatedV(12D);
        double v13 = icon.getInterpolatedV(13D);
        double v14 = icon.getInterpolatedV(14D);
        double v16 = icon.getInterpolatedV(16D);
        
        bend[0].setTextureBounds(u15, v11, u16, v12);
        bend[1].setTextureBounds(u15, v13, u16, v16);
        bend[2].setTextureBounds(u7, v14, u8, v16);
    }
    
    @Override
    public int getRenderId()
    {
        return ProxyCommon.RID_HOOK;
    }
}
