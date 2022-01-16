import org.jetbrains.annotations.NotNull;

import io.github.bloepiloepi.pvp.damage.CustomDamageType;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.NamespaceID;

public class FireHandler implements BlockHandler {
	@Override
	public @NotNull NamespaceID getNamespaceId() {
		return NamespaceID.from("minecraft", "fire");
	}

	@Override
	public void onTouch(@NotNull Touch touch) {
		if (touch.getTouching() instanceof LivingEntity) {
			LivingEntity fire = (LivingEntity) touch.getTouching();
			fire.setFireForDuration((int) fire.getFireDamagePeriod() + 2);
			if (fire.getAliveTicks() % 10 == 0)
				fire.damage(CustomDamageType.IN_FIRE, 1f);
		}
		if (touch.getTouching() instanceof ItemEntity) {
			ItemEntity fire = (ItemEntity) touch.getTouching();
			if (!fire.getItemStack().getMaterial().namespace().asString().contains("netherite")) {
				fire.setOnFire(true);
				if (fire.getAliveTicks() % 5 == 0) {
					fire.remove();
					touch.getInstance().playSound(
							Sound.sound(SoundEvent.BLOCK_FIRE_EXTINGUISH.namespace(), Source.BLOCK, 0.4f, 1),
							touch.getBlockPosition().x(), touch.getBlockPosition().y(), touch.getBlockPosition().z());
				}
			}
		}
	}

}
