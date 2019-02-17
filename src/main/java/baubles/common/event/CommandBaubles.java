package baubles.common.event;

import baubles.api.cap.BaublesCapabilities;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.stream.IntStream;

public class CommandBaubles {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("baubles")
        .requires(s -> s.hasPermissionLevel(4))
        .then(Commands.literal("clear")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("slot", IntegerArgumentType.integer(0, 6))
                                .executes(ctx -> clear(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "slot"))))
                        .executes(ctx -> clear(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), -1))))
        .then(Commands.literal("view")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("slot", IntegerArgumentType.integer(0, 6))
                                .executes(ctx -> show(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "slot"))))
                        .executes(ctx -> show(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), -1)))));
    }

    private static int clear(CommandSource source, EntityPlayerMP target, int slot) {
        target.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES).ifPresent(b -> {
            int[] toClear = { slot };
            if (slot == -1) {
                toClear = IntStream.range(0, b.getSlots()).toArray();
            }

            for (int i : toClear) {
                b.setStackInSlot(i, ItemStack.EMPTY);
                source.sendFeedback(new TextComponentTranslation("command.baubles.clear.feedback", i, target.getDisplayName()), true);
                target.sendMessage(new TextComponentTranslation("command.baubles.clear.cleared", i, source.getDisplayName()));
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int show(CommandSource source, EntityPlayerMP target, int slot) {
        target.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES).ifPresent(b -> {
            int[] toShow = { slot };
            if (slot == -1) {
                toShow = IntStream.range(0, b.getSlots()).toArray();
            }

            for (int i : toShow) {
                source.sendFeedback(new TextComponentTranslation("command.baubles.show.result", target.getDisplayName(), i, b.getStackInSlot(i).getTextComponent()), true);
            }
        });
        return Command.SINGLE_SUCCESS;
    }
}
