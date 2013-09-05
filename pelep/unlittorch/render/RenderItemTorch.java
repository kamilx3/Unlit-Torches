package pelep.unlittorch.render;

import org.lwjgl.opengl.GL11;

import pelep.unlittorch.config.ConfigCommon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class RenderItemTorch implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack ist, ItemRenderType type)
    {
        return ist.itemID == 50 && type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack ist, ItemRendererHelper helper)
    {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack ist, Object... data)
    {
        int d = ist.getItemDamage();
        Icon icon = ist.getItem().getIconFromDamage(d);

        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();

        Tessellator t = Tessellator.instance;

        t.startDrawingQuads();
        t.addVertexWithUV(0, 16, 0, minU, maxV);
        t.addVertexWithUV(16, 16, 0, maxU, maxV);
        t.addVertexWithUV(16, 0, 0, maxU, minV);
        t.addVertexWithUV(0, 0, 0, minU, minV);
        t.draw();

        if (d > 1)
        {
            int max = ConfigCommon.torchLifespanMax;
            d = Math.min(d, max);
            int dw = (int) Math.round(13D - d * 13D / max);
            int i = (int) Math.round(255D - d * 255D / max);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            int c1 = (255 - i) / 4 << 16 | 16128;
            int c2 = 255 - i << 16 | i << 8;

            this.renderDamage(t, 2, 2, 13, 2, 0);
            this.renderDamage(t, 2, 2, 12, 1, c1);
            this.renderDamage(t, 2, 2, dw, 1, c2);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }

    private void renderDamage(Tessellator t, int x, int y, int w, int h, int c)
    {
        t.startDrawingQuads();
        t.setColorOpaque_I(c);
        t.addVertex(x + 0, y + h, 0D);
        t.addVertex(x + w, y + h, 0D);
        t.addVertex(x + w, y + 0, 0D);
        t.addVertex(x + 0, y + 0, 0D);
        t.draw();
    }
}
