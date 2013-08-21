package pelep.unlittorch.asm;

import java.io.IOException;

import pelep.unlittorch.handler.LogHandler;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class UTAccessTransformer extends AccessTransformer
{
    public UTAccessTransformer() throws IOException
    {
        super(mapfile());
    }
    
    private static String mapfile()
    {
        LogHandler.info("Inserting map file path");
        return "pelep/unlittorch/asm/ut_at.cfg";
    }
}
