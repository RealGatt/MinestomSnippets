import org.jetbrains.annotations.NotNull;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;

public class OpenableHandler implements BlockHandler {

	@Override
	public @NotNull NamespaceID getNamespaceId() {
		return NamespaceID.from("minecraft", "trapdoor");
	}

	@Override
	public boolean onInteract(@NotNull Interaction interaction) {
		if (interaction.getPlayer().isSneaking())
			return false;
		boolean currentState = interaction.getBlock().getProperty("open") == "true" ? true : false;
		if (currentState)
			interaction.getInstance().setBlock(interaction.getBlockPosition(),
					interaction.getBlock().withProperty("open", "false"));
		else
			interaction.getInstance().setBlock(interaction.getBlockPosition(),
					interaction.getBlock().withProperty("open", "true"));
		return true;
	}
}
