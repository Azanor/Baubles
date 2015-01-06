package baubles.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import baubles.common.Config;

public final class CreativeTabBaubles extends CreativeTabs
{
	public CreativeTabBaubles(int par1, String par2Str)
    {
        super(par1, par2Str);
    }

    /**
     * the item to be displayed on the tab
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Item getTabIconItem()
    {
        return Config.itemRing;
    }

}
