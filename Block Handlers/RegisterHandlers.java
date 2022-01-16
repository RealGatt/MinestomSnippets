public class RegisterHandlers {

	public static void initHandlers(){
		
		FireHandler fireHandler = new FireHandler();
		OpenableHandler openableHandler = new OpenableHandler();

		MinecraftServer.getBlockManager().registerHandler(NamespaceID.from("minecraft:sign"), SignHandler::new);
		MinecraftServer.getBlockManager().registerHandler(NamespaceID.from("minecraft:fire"), () -> fireHandler);
		MinecraftServer.getTagManager().getTag(BasicType.BLOCKS, "minecraft:wooden_trapdoors").getValues()
				.forEach(namespace -> {
					MinecraftServer.getBlockManager().registerHandler(namespace, () -> openableHandler);
				});

		MinecraftServer.getTagManager().getTag(BasicType.BLOCKS, "minecraft:fence_gates").getValues()
				.forEach(namespace -> {
					MinecraftServer.getBlockManager().registerHandler(namespace, () -> openableHandler);
				});
	}
}
