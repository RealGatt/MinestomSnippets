import org.jetbrains.annotations.NotNull;

import net.gravitas.aussiemcc.blockhandlers.ChestHandler;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class ChestFirstOpenEvent implements BlockEvent, PlayerEvent {

	public ChestFirstOpenEvent(Player opener, Block chest, Point position, Instance instance) {
		this.opener = opener;
		this.chest = chest;
		this.position = position;
		this.instance = instance;
	}

	private Player opener;
	private Block chest;
	private Point position;
	private Instance instance;
	

	public Point getPosition() {
		return position;
	}

	public Instance getInstance() {
		return instance;
	}

	@Override
	public @NotNull Player getPlayer() {
		return opener;
	}

	@Override
	public @NotNull Block getBlock() {
		return chest;
	}

	public ChestHandler getChestHandler() {
		return (ChestHandler) chest.handler();
	}

}
