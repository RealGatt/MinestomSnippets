import java.util.HashMap;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;

import net.gravitas.aussiemcc.events.ChestFirstOpenEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;

public class ChestHandler implements BlockHandler {
	private Inventory inventory = null;
	private boolean lastOpenState = false, openedPreviously = false;
	private int lastViewCount = 0;

	private static HashMap<BlockFace, BlockFace> LEFT_CHEST_MAPPING = new HashMap<>();
	private static HashMap<BlockFace, BlockFace> RIGHT_CHEST_MAPPING = new HashMap<>();
	static {
		LEFT_CHEST_MAPPING.put(BlockFace.NORTH, BlockFace.EAST);
		LEFT_CHEST_MAPPING.put(BlockFace.EAST, BlockFace.SOUTH);
		LEFT_CHEST_MAPPING.put(BlockFace.SOUTH, BlockFace.WEST);
		LEFT_CHEST_MAPPING.put(BlockFace.WEST, BlockFace.NORTH);

		RIGHT_CHEST_MAPPING.put(BlockFace.EAST, BlockFace.NORTH);
		RIGHT_CHEST_MAPPING.put(BlockFace.SOUTH, BlockFace.EAST);
		RIGHT_CHEST_MAPPING.put(BlockFace.WEST, BlockFace.SOUTH);
		RIGHT_CHEST_MAPPING.put(BlockFace.NORTH, BlockFace.WEST);
	}

	@Override
	public NamespaceID getNamespaceId() {
		return NamespaceID.from("minecraft", "chest");
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public boolean isTickable() {
		return true;
	}

	@Override
	public void tick(Tick tick) {
		this.updateVisual(tick.getBlockPosition(), tick.getInstance());
	}

	// this is scuffed.
	private void updateVisual(Point blockPosition, Instance instance) {
		if (getInventory() == null) {
			BlockActionPacket blockPacket = new BlockActionPacket(blockPosition, (byte) 1,
					(byte) 0,
					instance.getBlock(blockPosition));
			instance.sendGroupedPacket(blockPacket);
			return;
		}

		if (lastViewCount == 0 && getInventory().getViewers().size() == 1) {
			BlockActionPacket blockPacket = new BlockActionPacket(blockPosition, (byte) 1,
					(byte) 2,
					instance.getBlock(blockPosition));
			instance.sendGroupedPacket(blockPacket);
		} else {
			BlockActionPacket blockPacket = new BlockActionPacket(blockPosition, (byte) 1,
					(byte) getInventory().getViewers().size(),
					instance.getBlock(blockPosition));
			instance.sendGroupedPacket(blockPacket);
		}

		lastViewCount = getInventory().getViewers().size();

		boolean originalState = lastOpenState;
		if (getInventory().getViewers().size() > 0)
			lastOpenState = true;
		else
			lastOpenState = false;

		if (originalState != lastOpenState) { // theres been a change monkas

			if (!lastOpenState) { // play the close chest sound
				instance.playSound(Sound.sound(SoundEvent.BLOCK_CHEST_CLOSE.key(), Source.BLOCK, 1, 1),
						(double) blockPosition.x(),
						(double) blockPosition.y(),
						(double) blockPosition.z());
			} else { // play the open chest sound
				instance.playSound(Sound.sound(SoundEvent.BLOCK_CHEST_OPEN.key(), Source.BLOCK, 1, 1),
						blockPosition.x(),
						blockPosition.y(),
						blockPosition.z());
			}
		}
	}

	@Override
	public boolean onInteract(Interaction interaction) {

		String type = interaction.getBlock().getProperty("type");
		String facing = interaction.getBlock().getProperty("facing");
		BlockFace facingBF = BlockFace.valueOf(facing.toUpperCase());
		if (type.equalsIgnoreCase("left")) {
			// be an end-user... let the left chest always handle things :)
			Point leftBlockPoint = interaction.getBlockPosition().relative(LEFT_CHEST_MAPPING.get(facingBF));
			Block leftBlock = interaction.getInstance().getBlock(leftBlockPoint);
			if (leftBlock.namespace() == interaction.getBlock().namespace()) { // check to see that they're the same
																				// type of chest. lets this also work
																				// for trapped chests
				Interaction newInteraction = new Interaction(leftBlock,
						interaction.getInstance(),
						leftBlockPoint,
						interaction.getPlayer(),
						interaction.getHand());
				return leftBlock.handler().onInteract(newInteraction);
			}
			type = "single"; // handle this chest like a single chest. its bugged or was placed incorrectly
		}

		if (getInventory() == null) { // do a check here to see if its a double chest or not
			if (type.equalsIgnoreCase("single")) {
				inventory = new Inventory(InventoryType.CHEST_3_ROW, Component.translatable("container.chest"));
				var itemsTag = Tag.NBT("Items");
				if (interaction.getBlock().hasTag(itemsTag)) {
					var originalChestItems = (NBTList<?>) interaction.getBlock().getTag(itemsTag);
					originalChestItems.forEach(obj -> {
						NBTCompound nbtComp = (NBTCompound) obj;
						ParsedItemSlot slot = parseNBTSlot(nbtComp);
						inventory.setItemStack(slot.getSlot(), slot.buildItem());
					});
				}
			} else if (type.equalsIgnoreCase("right")) {
				inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.translatable("container.chestDouble"));

				Point rightBlockPoint = interaction.getBlockPosition().relative(RIGHT_CHEST_MAPPING.get(facingBF));
				Block rightBlock = interaction.getInstance().getBlock(rightBlockPoint);

				var itemsTag = Tag.NBT("Items");
				if (interaction.getBlock().hasTag(itemsTag)) {
					var originalChestItems = (NBTList<?>) interaction.getBlock().getTag(itemsTag);
					originalChestItems.forEach(obj -> {
						NBTCompound nbtComp = (NBTCompound) obj;
						ParsedItemSlot slot = parseNBTSlot(nbtComp);
						inventory.setItemStack(slot.getSlot(), slot.buildItem());
					});
				}
				if (rightBlock.hasTag(itemsTag)) {
					var originalChestItems = (NBTList<?>) rightBlock.getTag(itemsTag);
					originalChestItems.forEach(obj -> {
						NBTCompound nbtComp = (NBTCompound) obj;
						ParsedItemSlot slot = parseNBTSlot(nbtComp);
						inventory.setItemStack(26 + slot.getSlot(), slot.buildItem());
					});
				}
			}
		}

		if (interaction.getPlayer().getGameMode() == GameMode.SPECTATOR) {
			// clone the inventory so theres absolutely 0 waya spectator can interact with
			// the original chest, also stops the updateVisual function from making the
			// chest look open
			Inventory clonedInventory = new Inventory(getInventory().getInventoryType(), getInventory().getTitle());
			clonedInventory.copyContents(getInventory().getItemStacks());
			interaction.getPlayer().openInventory(clonedInventory);
			return false;
		}

		if (interaction.getPlayer().isSneaking())
			return true;

		if (!openedPreviously) { // custom stuff for my things.
			ChestFirstOpenEvent firstOpenEvent = new ChestFirstOpenEvent(interaction.getPlayer(),
					interaction.getBlock(), interaction.getBlockPosition(), interaction.getInstance());
			EventDispatcher.call(firstOpenEvent);
			openedPreviously = true;
		}
		interaction.getPlayer().openInventory(getInventory());
		updateVisual(interaction.getBlockPosition(), interaction.getInstance());

		return false;
	}

	private ParsedItemSlot parseNBTSlot(NBTCompound comp) {
		return new ParsedItemSlot(comp.getAsInt("Count"), comp.getAsInt("Slot"), comp.getString("id"));
	}

	private class ParsedItemSlot {

		public ParsedItemSlot(int count, int slot, String id, NBTCompound tag) {
			this.Count = count;
			this.Slot = slot;
			this.id = id;
			this.tag = tag;
			System.out.println("creating new parsed item slot w/ an NBT tag");
			System.out.println(tag);
		}

		public ParsedItemSlot(int count, int slot, String id) {
			this.Count = count;
			this.Slot = slot;
			this.id = id;
		}

		private int Count, Slot;
		private String id;
		private NBTCompound tag = null;

		public int getCount() {
			return Count;
		}

		public int getSlot() {
			return Slot;
		}

		public String getId() {
			return id;
		}

		public ItemStack buildItem() {
			ItemStack is = ItemStack.builder(Material.fromNamespaceId(id)).amount(Count).meta((itemMetaConsumer) -> {
				if (tag != null) {
					ItemMetaBuilder.resetMeta(itemMetaConsumer, tag);
					if (itemMetaConsumer instanceof PotionMeta.Builder potionMetaBuilder) {
						System.out.println("Got Potion Meta Builder" + potionMetaBuilder + ". Potion: "
								+ potionMetaBuilder.build().getPotionType());
					}
				}
				return itemMetaConsumer;
			}).build();
			return is;
		}

	}

}
