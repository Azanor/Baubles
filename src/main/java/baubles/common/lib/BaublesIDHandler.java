package baubles.common.lib;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import baubles.common.Baubles;
import baubles.common.Config;

public class BaublesIDHandler implements IExtendedEntityProperties{

	public final static String EXT_PROP_NAME = "BaublesID";
	private final EntityPlayer player;
	private int baublesID;
	
	public BaublesIDHandler(EntityPlayer player){
		this.player = player;
		this.baublesID = Config.getNewBaublesID();
	}
	
	public static final BaublesIDHandler get(EntityPlayer player){
		return (BaublesIDHandler) player.getExtendedProperties(EXT_PROP_NAME);
	}
	
	public static final void register(EntityPlayer player){
		player.registerExtendedProperties(BaublesIDHandler.EXT_PROP_NAME, new BaublesIDHandler(player));
	}
	
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger("BaublesID", this.baublesID);
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		try
		{
			this.baublesID = properties.getInteger("BaublesID");
		}
		catch(Exception e)
		{
			Baubles.log.error("Could not load baubles inventory id, giving a new one.", e);
			saveNBTData(compound);
			loadNBTData(compound);
			Config.addBaublesID();
		}
		
	}

	@Override
	public void init(Entity entity, World world) {}

	public int getID(){
		return this.baublesID;
	}
}
