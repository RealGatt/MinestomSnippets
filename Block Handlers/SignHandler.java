import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;

public class SignHandler implements BlockHandler {

	@Override
	public @NotNull NamespaceID getNamespaceId() {
		return NamespaceID.from("minecraft", "sign");
	}

	public Collection<Tag<?>> getBlockEntityTags() {
		return Arrays.asList(
				Tag.Byte("GlowingText"),
				Tag.String("Color"),
				Tag.String("Text1"),
				Tag.String("Text2"),
				Tag.String("Text3"),
				Tag.String("Text4"));
	}

}
