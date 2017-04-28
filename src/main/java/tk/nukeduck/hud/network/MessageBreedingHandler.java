package tk.nukeduck.hud.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBreedingHandler implements IMessageHandler<MessageBreeding, IMessage> {
	@Override
	public IMessage onMessage(MessageBreeding message, MessageContext ctx) {
		System.out.println("RECEIVED: entity id " + message.entityId + ", love " + message.inLove);
		//BetterHud.breedNotifier.loveMap.put(message.entityId, message.inLove);
		return null;
	}
}
