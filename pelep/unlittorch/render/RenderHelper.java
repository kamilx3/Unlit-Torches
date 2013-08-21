package pelep.unlittorch.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderHelper
{
    public static final byte TOP = 0;
    public static final byte BOT = 1;
    public static final byte NTH = 2;
    public static final byte STH = 3;
    public static final byte EST = 4;
    public static final byte WST = 5;
    
    public static class Face
    {
        public byte side;
        public byte rotation;
        public double x;
        public double y;
        public double z;
        public double length;
        public double width;
        public double minU;
        public double maxU;
        public double minV;
        public double maxV;

        public Face() {}
        
        public Face(byte side, int rotation, double x, double y, double z, double length, double width)
        {
            this.setValues(side, rotation, x, y, z, length, width);
        }
        
        public void setValues(byte side, int rotation, double x, double y, double z, double length, double width)
        {
            this.side = side;
            this.rotation = (byte) rotation;
            this.x = x;
            this.y = y;
            this.z = z;
            this.length = length / 2;
            this.width = width / 2;
        }
        
        public void setTextureBounds(double minU, double minV, double maxU, double maxV)
        {
            this.minU = minU;
            this.minV = minV;
            this.maxU = maxU;
            this.maxV = maxV;
        }
        
        public void addFace()
        {
            double lru = this.minU;
            double lrv = this.minV;
            double llu = this.maxU;
            double llv = this.minV;
            double ulu = this.maxU;
            double ulv = this.maxV;
            double uru = this.minU;
            double urv = this.maxV;
            
            switch (this.rotation)
            {
            case 1:
                lru = this.maxU;
                lrv = this.minV;
                llu = this.maxU;
                llv = this.maxV;
                ulu = this.minU;
                ulv = this.maxV;
                uru = this.minU;
                urv = this.minV;
                break;
            case 2:
                lru = this.maxU;
                lrv = this.maxV;
                llu = this.minU;
                llv = this.maxV;
                ulu = this.minU;
                ulv = this.minV;
                uru = this.maxU;
                urv = this.minV;
                break;
            case 3:
                lru = this.minU;
                lrv = this.maxV;
                llu = this.minU;
                llv = this.minV;
                ulu = this.maxU;
                ulv = this.minV;
                uru = this.maxU;
                urv = this.maxV;
            }
            
            Tessellator t = Tessellator.instance;
            
            switch (this.side)
            {
            case TOP:
                t.addVertexWithUV(this.x + this.width, this.y, this.z + this.length, llu, llv);
                t.addVertexWithUV(this.x + this.width, this.y, this.z - this.length, lru, lrv);
                t.addVertexWithUV(this.x - this.width, this.y, this.z - this.length, uru, urv);
                t.addVertexWithUV(this.x - this.width, this.y, this.z + this.length, ulu, ulv);
                break;
            case BOT:
                t.addVertexWithUV(this.x - this.width, this.y, this.z + this.length, llu, llv);
                t.addVertexWithUV(this.x - this.width, this.y, this.z - this.length, lru, lrv);
                t.addVertexWithUV(this.x + this.width, this.y, this.z - this.length, uru, urv);
                t.addVertexWithUV(this.x + this.width, this.y, this.z + this.length, ulu, ulv);
                break;
            case NTH:
                t.addVertexWithUV(this.x + this.width, this.y - this.length, this.z, llu, llv);
                t.addVertexWithUV(this.x - this.width, this.y - this.length, this.z, lru, lrv);
                t.addVertexWithUV(this.x - this.width, this.y + this.length, this.z, uru, urv);
                t.addVertexWithUV(this.x + this.width, this.y + this.length, this.z, ulu, ulv);
                break;
            case STH:
                t.addVertexWithUV(this.x - this.width, this.y - this.length, this.z, llu, llv);
                t.addVertexWithUV(this.x + this.width, this.y - this.length, this.z, lru, lrv);
                t.addVertexWithUV(this.x + this.width, this.y + this.length, this.z, uru, urv);
                t.addVertexWithUV(this.x - this.width, this.y + this.length, this.z, ulu, ulv);
                break;
            case EST:
                t.addVertexWithUV(this.x, this.y - this.length, this.z + this.width, llu, llv);
                t.addVertexWithUV(this.x, this.y - this.length, this.z - this.width, lru, lrv);
                t.addVertexWithUV(this.x, this.y + this.length, this.z - this.width, uru, urv);
                t.addVertexWithUV(this.x, this.y + this.length, this.z + this.width, ulu, ulv);
                break;
            default:
                t.addVertexWithUV(this.x, this.y - this.length, this.z - this.width, llu, llv);
                t.addVertexWithUV(this.x, this.y - this.length, this.z + this.width, lru, lrv);
                t.addVertexWithUV(this.x, this.y + this.length, this.z + this.width, uru, urv);
                t.addVertexWithUV(this.x, this.y + this.length, this.z - this.width, ulu, ulv);
            }
        }

        public void addFaceWithColorMultiplier(RenderBlocks rb, Block block, IBlockAccess world, int x, int y, int z, float rm, float gm, float bm)
        {
            rb.enableAO = false;
            float color;
            
            switch (this.side)
            {
            case TOP:
                color = 1F;
                y++;
                break;
            case BOT:
                color = 0.5F;
                y--;
                break;
            case NTH:
                color = 0.8F;
                z--;
                break;
            case STH:
                color = 0.8F;
                z++;
                break;
            case EST:
                color = 0.6F;
                x++;
                break;
            default:
                color = 0.6F;
                x--;
            }
            
            float r = color * rm;
            float g = color * gm;
            float b = color * bm;
            
            Tessellator t = Tessellator.instance;
            t.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
            t.setColorOpaque_F(r, g, b);
            
            this.addFace();
        }
    }
}
