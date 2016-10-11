package baubles.common.event;

import java.util.ArrayList;
import java.util.List;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandBaubles extends CommandBase {
	private List aliases;

	public CommandBaubles() {
		this.aliases = new ArrayList();
		this.aliases.add("baub");
		this.aliases.add("bau");
	}	
		
	@Override
	public String getCommandName() {
		return "baubles";
	}
	
	@Override
	public List<String> getCommandAliases() {
		return aliases;
    }

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/baubles <action> [<player> [<params>]]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return i == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
			sender.addChatMessage(new TextComponentTranslation("\u00a73You can also use /baub or /bau instead of /baubles."));
			sender.addChatMessage(new TextComponentTranslation("\u00a73Use this to view the baubles inventory of a player."));
			sender.addChatMessage(new TextComponentTranslation("  /baubles view <player>"));
			sender.addChatMessage(new TextComponentTranslation("\u00a73Use this to clear a players baubles inventory. Default is everything or you can give a slot number"));
			sender.addChatMessage(new TextComponentTranslation("  /baubles clear <player> [<slot>]"));
		} else 
		if (args.length >= 2) {
			EntityPlayerMP entityplayermp = getPlayer(server,sender,args[1]);
			
			if (entityplayermp==null) {
				sender.addChatMessage(new TextComponentTranslation("\u00a7c"+args[1]+" not found"));
				return;
			}
			
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(entityplayermp);
			
			if (args[0].equalsIgnoreCase("view")) {				
				sender.addChatMessage(new TextComponentTranslation("\u00a73Showing baubles for "+entityplayermp.getName()));
				for (int a = 0; a<baubles.getSlots();a++) {
					ItemStack st = baubles.getStackInSlot(a);
					if (st!=null && st.getItem() instanceof IBauble) {
						IBauble bauble=(IBauble)st.getItem();
						BaubleType bt = bauble.getBaubleType(st);
						sender.addChatMessage(new TextComponentTranslation("\u00a73 [Slot "+a+"] "+bt+" "+st.getDisplayName()));
					}
				}
			} else 
			if (args[0].equalsIgnoreCase("clear")) {				
				if (args.length>=3) {
					int slot = -1;
					try { slot = Integer.parseInt(args[2]); } catch (Exception e) {}
					if (slot<0 || slot >= baubles.getSlots()) {
						sender.addChatMessage(new TextComponentTranslation("\u00a7cInvalid arguments"));
						sender.addChatMessage(new TextComponentTranslation("\u00a7cUse /baubles help to get help"));
					} else {
						baubles.setStackInSlot(slot, null);
						sender.addChatMessage(new TextComponentTranslation("\u00a73Cleared baubles slot "+slot+" for "+entityplayermp.getName()));
						entityplayermp.addChatMessage(new TextComponentTranslation("\u00a74Your baubles slot "+slot+" has been cleared by admin "+sender.getName()));
					}
				} else {
					for (int a = 0; a<baubles.getSlots();a++) {
						baubles.setStackInSlot(a, null);						
					}
					sender.addChatMessage(new TextComponentTranslation("\u00a73Cleared all baubles slots for "+entityplayermp.getName()));
					entityplayermp.addChatMessage(new TextComponentTranslation("\u00a74All your baubles slots have been cleared by admin "+sender.getName()));					
				}				
			} else {
				sender.addChatMessage(new TextComponentTranslation("\u00a7cInvalid arguments"));
				sender.addChatMessage(new TextComponentTranslation("\u00a7cUse /baubles help to get help"));
			}

		} else {
			sender.addChatMessage(new TextComponentTranslation("\u00a7cInvalid arguments"));
			sender.addChatMessage(new TextComponentTranslation("\u00a7cUse /baubles help to get help"));
		}

	}

}
