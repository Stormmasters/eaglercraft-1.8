/*
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.EaglerXBungee;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.EaglerXBungeeAPIHelper;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftHandleAuthCookieEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftHandleAuthPasswordEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftIsAuthRequiredEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftRegisterCapeEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.api.event.EaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.auth.DefaultAuthSystem;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.command.CommandConfirmCode;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.config.EaglerAuthConfig;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.config.EaglerBungeeConfig;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.config.EaglerListenerConfig;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.config.EaglerRateLimiter;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.config.EaglerUpdateConfig;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.config.RateLimitStatus;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.server.EaglerInitialHandler.ClientCertificateHolder;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.server.protocol.GameProtocolMessageController;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.server.query.MOTDQueryHandler;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.server.query.QueryManager;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.skins.CapePackets;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.skins.SkinPackets;
import net.lax1dude.eaglercraft.v1_8.plugin.gateway_bungeecord.skins.SkinService;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.UpstreamBridge;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class HttpWebSocketHandler extends ChannelInboundHandlerAdapter {
	
	private final EaglerListenerConfig conf;

	private int clientLoginState = HandshakePacketTypes.STATE_OPENED;
	private int clientProtocolVersion = -1;
	private boolean isProtocolExchanged = false;
	private int gameProtocolVersion = -1;
	private String clientBrandString;
	private String clientVersionString;
	private String clientUsername;
	private UUID clientUUID;
	private UUID offlineUUID;
	private String clientRequestedServer;
	private boolean clientAuth;
	private byte[] clientAuthUsername;
	private byte[] clientAuthPassword;
	private boolean clientEnableCookie;
	private byte[] clientCookieData;
	private EaglercraftIsAuthRequiredEvent authRequireEvent;
	private final Map<String, byte[]> profileData = new HashMap<>();
	private boolean hasFirstPacket = false;
	private boolean hasBinaryConnection = false;
	private boolean connectionClosed = false;
	private InetAddress remoteAddress;
	private String localAddrString; 
	private Property texturesOverrideProperty;
	private boolean overrideEaglerToVanillaSkins;

	private static final Set<String> profileDataStandard = Sets.newHashSet(
			"skin_v1", "skin_v2", "cape_v1", "update_cert_v1", "brand_uuid_v1");

	public HttpWebSocketHandler(EaglerListenerConfig conf) {
		this.conf = conf;
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(msg instanceof WebSocketFrame) {
				if(msg instanceof BinaryWebSocketFrame) {
					handleBinary(ctx, ((BinaryWebSocketFrame)msg).content());
				}else if(msg instanceof TextWebSocketFrame) {
					handleText(ctx, ((TextWebSocketFrame)msg).text());
				}else if(msg instanceof PingWebSocketFrame) {
					ctx.writeAndFlush(new PongWebSocketFrame());
				}else if(msg instanceof CloseWebSocketFrame) {
					connectionClosed = true;
					ctx.close();
				}
			}else if(msg instanceof HAProxyMessage) {
				EaglerXBungee.logger().warning("[" + ctx.channel().remoteAddress() + "]: Ignoring HAProxyMessage because the WebSocket connection has already been established");
			}else {
				EaglerXBungee.logger().severe("Unexpected Packet: " + msg.getClass().getSimpleName());
			}
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel().isActive()) {
			EaglerXBungee.logger().warning("[Yee][" + ctx.channel().remoteAddress() + "]: Exception Caught: " + cause.toString());
		}
	}
	
	private void handleBinary(ChannelHandlerContext ctx, ByteBuf buffer) {
		if(connectionClosed) {
			return;
		}
		if(!hasFirstPacket) {
			if(buffer.readableBytes() >= 2) {
				if(buffer.getByte(0) == (byte)2 && buffer.getByte(1) == (byte)69) {
					handleLegacyClient(ctx, buffer);
					return;
				}
			}
			hasFirstPacket = true;
			hasBinaryConnection = true;
			
			BungeeCord bungus = BungeeCord.getInstance();
			int limit = bungus.config.getPlayerLimit();
			if (limit > 0 && bungus.getOnlineCount() >= limit) {
				sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, bungus.getTranslation("proxy_full"))
						.addListener(ChannelFutureListener.CLOSE);
				return;
			}
			
			if(conf.getMaxPlayer() > 0) {
				int i = 0;
				for(ProxiedPlayer p : bungus.getPlayers()) {
					if(p.getPendingConnection().getListener() == conf) {
						++i;
					}
				}
				
				if (i >= conf.getMaxPlayer()) {
					sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, bungus.getTranslation("proxy_full"))
							.addListener(ChannelFutureListener.CLOSE);
					return;
				}
			}
			
			SocketAddress localSocketAddr = ctx.channel().remoteAddress();
			InetAddress addr = ctx.channel().attr(EaglerPipeline.REAL_ADDRESS).get();
			
			String limiterAddress = null;
			RateLimitStatus loginRateLimit = RateLimitStatus.OK;
			if(addr != null) {
				remoteAddress = addr;
				limiterAddress = addr.getHostAddress();
			}else {
				if(localSocketAddr instanceof InetSocketAddress) {
					remoteAddress = ((InetSocketAddress)localSocketAddr).getAddress();
					limiterAddress = remoteAddress.getHostAddress();
				}else {
					remoteAddress = InetAddress.getLoopbackAddress();
				}
			}
			
			EaglerRateLimiter limiter = conf.getRatelimitLogin();
			if(limiterAddress != null && limiter != null) {
				loginRateLimit = limiter.rateLimit(limiterAddress);
			}
			
			if(loginRateLimit == RateLimitStatus.LOCKED_OUT) {
				ctx.close();
				connectionClosed = true;
				return;
			}
			
			if (loginRateLimit != RateLimitStatus.OK) {
				sendErrorCode(ctx,
						loginRateLimit == RateLimitStatus.LIMITED_NOW_LOCKED_OUT
								? HandshakePacketTypes.SERVER_ERROR_RATELIMIT_LOCKED
								: HandshakePacketTypes.SERVER_ERROR_RATELIMIT_BLOCKED,
						"Too many logins!").addListener(ChannelFutureListener.CLOSE);
				return;
			}
			
			localAddrString = localSocketAddr.toString();
			EaglerXBungee.logger().info("[" + localAddrString + "]: Connected via websocket");
			if(addr != null) {
				EaglerXBungee.logger().info("[" + localAddrString + "]: Real address is " + addr.getHostAddress());
			}
			String origin = ctx.channel().attr(EaglerPipeline.ORIGIN).get();
			if(origin != null) {
				EaglerXBungee.logger().info("[" + localAddrString + "]: Origin header is " + origin);
			}else {
				EaglerXBungee.logger().info("[" + localAddrString + "]: No origin header is present!");
			}
		}else if(!hasBinaryConnection) {
			connectionClosed = true;
			ctx.close();
			return;
		}
		int op = -1;
		try {
			op = buffer.readUnsignedByte();
			switch(op) {
			case HandshakePacketTypes.PROTOCOL_CLIENT_VERSION: {
				if(clientLoginState == HandshakePacketTypes.STATE_OPENED) {
					clientLoginState = HandshakePacketTypes.STATE_STALLING;
					EaglerXBungee eaglerXBungee = EaglerXBungee.getEagler();
					EaglerAuthConfig authConfig = eaglerXBungee.getConfig().getAuthConfig();
					
					int eaglerLegacyProtocolVersion = buffer.readUnsignedByte();
					
					if(eaglerLegacyProtocolVersion == 1) {
						if(authConfig.isEnableAuthentication()) {
							sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Please update your client to register on this server!")
									.addListener(ChannelFutureListener.CLOSE);
							return;
						}else if(buffer.readUnsignedByte() != 47 || 47 < conf.getMinMCProtocol()
								|| 47 > conf.getMaxMCProtocol() || !conf.isAllowV3()) {
							clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
							connectionClosed = true;
							ByteBuf buf = ctx.alloc().buffer();
							buf.writeByte(HandshakePacketTypes.PROTOCOL_VERSION_MISMATCH);
							buf.writeByte(1);
							buf.writeByte(1);
							buf.writeByte(eaglerLegacyProtocolVersion);
							String str = "Outdated Client";
							buf.writeByte(str.length());
							buf.writeCharSequence(str, StandardCharsets.US_ASCII);
							ctx.writeAndFlush(new BinaryWebSocketFrame(buf)).addListener(ChannelFutureListener.CLOSE);
							return;
						}
					}else if(eaglerLegacyProtocolVersion == 2) {
						
						//make sure to update VersionQueryHandler too
						int minServerSupported = conf.isAllowV3() ? 2 : 4;
						int maxServerSupported = conf.isAllowV4() ? 4 : 3;
						int minAvailableProtVers = Integer.MAX_VALUE;
						int maxAvailableProtVers = Integer.MIN_VALUE;
						int protVers = -1;
						
						int cnt = buffer.readUnsignedShort();
						for(int i = 0; i < cnt; ++i) {
							int j = buffer.readUnsignedShort();
							if(j > maxAvailableProtVers) {
								maxAvailableProtVers = j;
							}
							if(j < minAvailableProtVers) {
								minAvailableProtVers = j;
							}
							if(j >= minServerSupported && j <= maxServerSupported && j > protVers) {
								protVers = j;
							}
						}
						
						int minGameVers = conf.getMinMCProtocol();
						int maxGameVers = conf.getMaxMCProtocol();
						int minAvailableGameVers = Integer.MAX_VALUE;
						int maxAvailableGameVers = Integer.MIN_VALUE;
						int gameVers = -1;
						
						cnt = buffer.readUnsignedShort();
						for(int i = 0; i < cnt; ++i) {
							int j = buffer.readUnsignedShort();
							if(j > maxAvailableGameVers) {
								maxAvailableGameVers = j;
							}
							if(j < minAvailableGameVers) {
								minAvailableGameVers = j;
							}
							if(j >= minGameVers && j <= maxGameVers && j > gameVers) {
								gameVers = j;
							}
						}
						
						if(maxAvailableProtVers == Integer.MIN_VALUE || maxAvailableGameVers == Integer.MIN_VALUE) {
							throw new IOException();
						}
						
						boolean versMisMatch = false;
						boolean isServerProbablyOutdated = false;
						boolean isClientProbablyOutdated = false;
						if(protVers == -1) {
							clientProtocolVersion = maxAvailableProtVers < 3 ? 2 : 3;
							versMisMatch = true;
							isServerProbablyOutdated = minAvailableProtVers > maxServerSupported && maxAvailableProtVers > maxServerSupported;
							isClientProbablyOutdated = minAvailableProtVers < minServerSupported && maxAvailableProtVers < minServerSupported;
						}else {
							clientProtocolVersion = protVers;
							if(gameVers == -1) {
								versMisMatch = true;
								isServerProbablyOutdated = minAvailableGameVers > maxGameVers && maxAvailableGameVers > maxGameVers;
								isClientProbablyOutdated = minAvailableGameVers < minGameVers && maxAvailableGameVers < minGameVers;
							}else {
								gameProtocolVersion = gameVers;
							}
						}
						
						if(versMisMatch) {
							clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
							connectionClosed = true;
							ByteBuf buf = ctx.alloc().buffer();
							buf.writeByte(HandshakePacketTypes.PROTOCOL_VERSION_MISMATCH);
							
							buf.writeShort((conf.isAllowV3() ? 2 : 0) + (conf.isAllowV4() ? 1 : 0));
							if(conf.isAllowV3()) {
								buf.writeShort(2);
								buf.writeShort(3);
							}
							if(conf.isAllowV4()) {
								buf.writeShort(4);
							}
							
							buf.writeShort(2);
							buf.writeShort(minGameVers);
							buf.writeShort(maxGameVers);
							
							String str = isClientProbablyOutdated ? "Outdated Client" : (isServerProbablyOutdated ? "Outdated Server" : "Unsupported Client Version");
							buf.writeByte(str.length());
							buf.writeCharSequence(str, StandardCharsets.US_ASCII);
							ctx.writeAndFlush(new BinaryWebSocketFrame(buf)).addListener(ChannelFutureListener.CLOSE);
							return;
						}
					}else {
						sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Legacy protocol version should always be '2' on post-snapshot clients")
								.addListener(ChannelFutureListener.CLOSE);
						return;
					}
					
					int strlen = buffer.readUnsignedByte();
					String eaglerBrand = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
					strlen = buffer.readUnsignedByte();
					String eaglerVersionString = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
					
					if(eaglerLegacyProtocolVersion >= 2) {
						clientAuth = buffer.readBoolean();
						strlen = buffer.readUnsignedByte();
						clientAuthUsername = new byte[strlen];
						buffer.readBytes(clientAuthUsername);
					}
					if(buffer.isReadable()) {
						throw new IllegalArgumentException("Packet too long");
					}
					
					boolean useSnapshotFallbackProtocol = false;
					if(eaglerLegacyProtocolVersion == 1 && !authConfig.isEnableAuthentication()) {
						clientProtocolVersion = 2;
						gameProtocolVersion = 47;
						useSnapshotFallbackProtocol = true;
						clientAuth = false;
						clientAuthUsername = null;
					}
						
					InetAddress addr = ctx.channel().attr(EaglerPipeline.REAL_ADDRESS).get();
					if(addr == null) {
						SocketAddress remoteSocketAddr = ctx.channel().remoteAddress();
						if(remoteSocketAddr instanceof InetSocketAddress) {
							addr = ((InetSocketAddress)remoteSocketAddr).getAddress();
						}else {
							addr = InetAddress.getLoopbackAddress();
						}
					}
					
					EaglercraftClientBrandEvent brandEvent = new EaglercraftClientBrandEvent(eaglerBrand, eaglerVersionString,
							ctx.channel().attr(EaglerPipeline.ORIGIN).get(), clientProtocolVersion, addr);
					eaglerXBungee.getProxy().getPluginManager().callEvent(brandEvent);
					if(brandEvent.isCancelled()) {
						BaseComponent kickReason = brandEvent.getMessage();
						if(kickReason == null) {
							kickReason = new TextComponent("End of stream");
						}
						sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, kickReason)
								.addListener(ChannelFutureListener.CLOSE);
						return;
					}

					final boolean final_useSnapshotFallbackProtocol = useSnapshotFallbackProtocol;
					Runnable continueThread = () -> {
						clientLoginState = HandshakePacketTypes.STATE_CLIENT_VERSION;
						clientBrandString = eaglerBrand;
						clientVersionString = eaglerVersionString;
						
						ByteBuf buf = ctx.alloc().buffer();
						buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_VERSION);
						
						if(final_useSnapshotFallbackProtocol) {
							buf.writeByte(1);
						}else {
							buf.writeShort(clientProtocolVersion);
							buf.writeShort(gameProtocolVersion);
						}
						
						String brandStr = eaglerXBungee.getDescription().getName();
						buf.writeByte(brandStr.length());
						buf.writeCharSequence(brandStr, StandardCharsets.US_ASCII);
						
						String versStr = eaglerXBungee.getDescription().getVersion();
						buf.writeByte(versStr.length());
						buf.writeCharSequence(versStr, StandardCharsets.US_ASCII);

						if(!authConfig.isEnableAuthentication() || !clientAuth) {
							buf.writeByte(0);
							buf.writeShort(0);
						}else {
							int meth = getAuthMethodId(authRequireEvent.getUseAuthType());
							
							if(meth == -1) {
								buf.release();
								sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Unsupported authentication method resolved")
										.addListener(ChannelFutureListener.CLOSE);
								EaglerXBungee.logger().severe("[" + localAddrString + "]: Disconnecting, unsupported AuthMethod: " + authRequireEvent.getUseAuthType());
								return;
							}
							
							buf.writeByte(meth);
							
							byte[] saltingData = authRequireEvent.getSaltingData();
							if(saltingData != null) {
								buf.writeShort(saltingData.length);
								buf.writeBytes(saltingData);
							}else {
								buf.writeShort(0);
							}
						}
						
						ctx.writeAndFlush(new BinaryWebSocketFrame(buf));
						isProtocolExchanged = true;
					};
					
					authRequireEvent = null;
					if(authConfig.isEnableAuthentication()) {
						String origin = ctx.channel().attr(EaglerPipeline.ORIGIN).get();
						try {
							authRequireEvent = new EaglercraftIsAuthRequiredEvent(conf, remoteAddress, origin,
									clientAuth, clientAuthUsername, (reqAuthEvent) -> {
								if(authRequireEvent.shouldKickUser()) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, authRequireEvent.getKickMessage())
											.addListener(ChannelFutureListener.CLOSE);
									return;
								}
								
								EaglercraftIsAuthRequiredEvent.AuthResponse resp = authRequireEvent.getAuthRequired();
								if(resp == null) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "IsAuthRequiredEvent was not handled")
											.addListener(ChannelFutureListener.CLOSE);
									EaglerXBungee.logger().severe("[" + localAddrString + "]: Disconnecting, no installed authentication system handled: " + authRequireEvent.toString());
									return;
								}
								
								if(resp == EaglercraftIsAuthRequiredEvent.AuthResponse.DENY) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, authRequireEvent.getKickMessage())
											.addListener(ChannelFutureListener.CLOSE);
									return;
								}
								
								EaglercraftIsAuthRequiredEvent.AuthMethod type = authRequireEvent.getUseAuthType();
								if(type == null) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "IsAuthRequiredEvent was not fully handled")
											.addListener(ChannelFutureListener.CLOSE);
									EaglerXBungee.logger().severe("[" + localAddrString + "]: Disconnecting, no authentication method provided by handler");
									return;
								}
								
								int typeId = getAuthMethodId(type);
								if(typeId == -1) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Unsupported authentication method resolved")
											.addListener(ChannelFutureListener.CLOSE);
									EaglerXBungee.logger().severe("[" + localAddrString + "]: Disconnecting, unsupported AuthMethod: " + type);
									return;
								}
								
								if(!clientAuth && resp == EaglercraftIsAuthRequiredEvent.AuthResponse.REQUIRE) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_AUTHENTICATION_REQUIRED,
											HandshakePacketTypes.AUTHENTICATION_REQUIRED + " [" + typeId + "] " + authRequireEvent.getAuthMessage())
													.addListener(ChannelFutureListener.CLOSE);
									EaglerXBungee.logger().info("[" + localAddrString + "]: Displaying authentication screen");
									return;
								}else {
									if(authRequireEvent.getUseAuthType() == null) {
										sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "IsAuthRequiredEvent was not fully handled")
												.addListener(ChannelFutureListener.CLOSE);
										EaglerXBungee.logger().severe("[" + localAddrString + "]: Disconnecting, no authentication method provided by handler");
										return;
									}
								}
								continueThread.run();
							});
							
							if(authConfig.isUseBuiltInAuthentication()) {
								DefaultAuthSystem authSystem = eaglerXBungee.getAuthService();
								if(authSystem != null) {
									authSystem.handleIsAuthRequiredEvent(authRequireEvent);
								}
							}else {
								eaglerXBungee.getProxy().getPluginManager().callEvent(authRequireEvent);
							}
							
							if(!authRequireEvent.isAsyncContinue()) {
								authRequireEvent.doDirectContinue();
							}
						}catch(Throwable t) {
							throw new EventException(t);
						}
					}else {
						continueThread.run();
					}
				}else {
					clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
					sendErrorWrong(ctx, op, "STATE_OPENED").addListener(ChannelFutureListener.CLOSE);
				}
			}
			break;
			case HandshakePacketTypes.PROTOCOL_CLIENT_REQUEST_LOGIN: {
				if(clientLoginState == HandshakePacketTypes.STATE_CLIENT_VERSION) {
					clientLoginState = HandshakePacketTypes.STATE_STALLING;
					
					int strlen = buffer.readUnsignedByte();
					clientUsername = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
					
					if(!clientUsername.equals(clientUsername.replaceAll("[^A-Za-z0-9_]", "_"))) {
						sendLoginDenied(ctx, "Invalid characters in username")
							.addListener(ChannelFutureListener.CLOSE);
						return;
					}
					
					if(clientUsername.length() < 3) {
						sendLoginDenied(ctx, "Username must be at least 3 characters")
							.addListener(ChannelFutureListener.CLOSE);
						return;
					}
					
					if(clientUsername.length() > 16) {
						sendLoginDenied(ctx, "Username must be under 16 characters")
							.addListener(ChannelFutureListener.CLOSE);
						return;
					}
					
					if(clientAuthUsername == null) {
						clientAuthUsername = new byte[strlen];
						for(int i = 0; i < strlen; ++i) {
							clientAuthUsername[i] = (byte)clientUsername.charAt(i);
						}
					}
					
					clientUUID = offlineUUID = EaglerInitialHandler.generateOfflineUUID(clientAuthUsername);
					
					strlen = buffer.readUnsignedByte();
					clientRequestedServer = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
					strlen = buffer.readUnsignedByte();
					clientAuthPassword = new byte[strlen];
					buffer.readBytes(clientAuthPassword);
					
					if(clientProtocolVersion >= 4) {
						clientEnableCookie = buffer.readBoolean();
						strlen = buffer.readUnsignedByte();
						if(clientEnableCookie && strlen > 0) {
							clientCookieData = new byte[strlen];
							buffer.readBytes(clientCookieData);
						}else {
							if(strlen > 0) {
								throw new IllegalArgumentException("Unexpected cookie");
							}
							clientCookieData = null;
						}
					}else {
						clientEnableCookie = false;
						clientCookieData = null;
					}
					
					if(buffer.isReadable()) {
						throw new IllegalArgumentException("Packet too long");
					}

					Runnable continueThread = () -> {
						
						final BungeeCord bungee = BungeeCord.getInstance();
						final ProxiedPlayer oldName = bungee.getPlayer(clientUsername);
						if (oldName != null) {
							sendLoginDenied(ctx, bungee.getTranslation("already_connected_proxy")).addListener(ChannelFutureListener.CLOSE);
							return;
						}
						
						clientLoginState = HandshakePacketTypes.STATE_CLIENT_LOGIN;
						ByteBuf buf = ctx.alloc().buffer();
						buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ALLOW_LOGIN);
						buf.writeByte(clientUsername.length());
						buf.writeCharSequence(clientUsername, StandardCharsets.US_ASCII);
						buf.writeLong(clientUUID.getMostSignificantBits());
						buf.writeLong(clientUUID.getLeastSignificantBits());
						ctx.writeAndFlush(new BinaryWebSocketFrame(buf));
					};

					EaglerXBungee eaglerXBungee = EaglerXBungee.getEagler();
					EaglerAuthConfig authConfig = eaglerXBungee.getConfig().getAuthConfig();
					
					if(authConfig.isEnableAuthentication()) {
						if(clientAuth && clientAuthPassword.length > 0) {
							EaglercraftHandleAuthPasswordEvent handleEvent = new EaglercraftHandleAuthPasswordEvent(
									conf, remoteAddress, authRequireEvent.getOriginHeader(), clientAuthUsername,
									authRequireEvent.getSaltingData(), clientUsername, clientUUID,
									clientAuthPassword, clientEnableCookie, clientCookieData,
									authRequireEvent.getUseAuthType(), authRequireEvent.getAuthMessage(),
									(Object) authRequireEvent.getAuthAttachment(), clientRequestedServer,
									(handleAuthEvent) -> {
								
								if(handleAuthEvent.getLoginAllowed() != EaglercraftHandleAuthPasswordEvent.AuthResponse.ALLOW) {
									sendLoginDenied(ctx, handleAuthEvent.getLoginDeniedMessage()).addListener(ChannelFutureListener.CLOSE);
									return;
								}
								
								clientUsername = handleAuthEvent.getProfileUsername();
								clientUUID = handleAuthEvent.getProfileUUID();
								
								String texPropOverrideValue = handleAuthEvent.getApplyTexturesPropertyValue();
								if(texPropOverrideValue != null) {
									String texPropOverrideSig = handleAuthEvent.getApplyTexturesPropertySignature();
									texturesOverrideProperty = new Property("textures", texPropOverrideValue, texPropOverrideSig);
								}
								
								overrideEaglerToVanillaSkins = handleAuthEvent.isOverrideEaglerToVanillaSkins();
								
								continueThread.run();
							});
							
							if(authConfig.isUseBuiltInAuthentication()) {
								DefaultAuthSystem authSystem = eaglerXBungee.getAuthService();
								if(authSystem != null) {
									authSystem.handleAuthPasswordEvent(handleEvent);
								}
							}else {
								eaglerXBungee.getProxy().getPluginManager().callEvent(handleEvent);
							}
							
							if(!handleEvent.isAsyncContinue()) {
								handleEvent.doDirectContinue();
							}
						}else if(authRequireEvent.getEnableCookieAuth()) {
							EaglercraftHandleAuthCookieEvent handleEvent = new EaglercraftHandleAuthCookieEvent(
									conf, remoteAddress, authRequireEvent.getOriginHeader(), clientAuthUsername,
									clientUsername, clientUUID, clientEnableCookie, clientCookieData,
									authRequireEvent.getUseAuthType(), authRequireEvent.getAuthMessage(),
									(Object) authRequireEvent.getAuthAttachment(),
									clientRequestedServer, (handleAuthEvent) -> {
								
								EaglercraftHandleAuthCookieEvent.AuthResponse resp = handleAuthEvent.getLoginAllowed();
								
								if(resp == null) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "EaglercraftHandleAuthCookieEvent was not handled")
											.addListener(ChannelFutureListener.CLOSE);
									EaglerXBungee.logger().severe("[" + localAddrString + "]: Disconnecting, no installed authentication system handled: " + handleAuthEvent.toString());
									return;
								}
								
								if(resp == EaglercraftHandleAuthCookieEvent.AuthResponse.DENY) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, handleAuthEvent.getLoginDeniedMessage())
											.addListener(ChannelFutureListener.CLOSE);
									return;
								}
								
								clientUsername = handleAuthEvent.getProfileUsername();
								clientUUID = handleAuthEvent.getProfileUUID();
								
								String texPropOverrideValue = handleAuthEvent.getApplyTexturesPropertyValue();
								if(texPropOverrideValue != null) {
									String texPropOverrideSig = handleAuthEvent.getApplyTexturesPropertySignature();
									texturesOverrideProperty = new Property("textures", texPropOverrideValue, texPropOverrideSig);
								}
								
								overrideEaglerToVanillaSkins = handleAuthEvent.isOverrideEaglerToVanillaSkins();
								
								if(resp == EaglercraftHandleAuthCookieEvent.AuthResponse.ALLOW) {
									continueThread.run();
									return;
								}
								
								if(!clientAuth && resp == EaglercraftHandleAuthCookieEvent.AuthResponse.REQUIRE_AUTH) {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_AUTHENTICATION_REQUIRED, HandshakePacketTypes.AUTHENTICATION_REQUIRED
											+ " [" + getAuthMethodId(authRequireEvent.getUseAuthType()) + "] " + authRequireEvent.getAuthMessage())
												.addListener(ChannelFutureListener.CLOSE);
									EaglerXBungee.logger().info("[" + localAddrString + "]: Displaying authentication screen");
									return;
								}else {
									sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Failed to handle authentication!")
											.addListener(ChannelFutureListener.CLOSE);
									return;
								}
							});
							
							eaglerXBungee.getProxy().getPluginManager().callEvent(handleEvent);
							
							if(!handleEvent.isAsyncContinue()) {
								handleEvent.doDirectContinue();
							}
						}else {
							if(authRequireEvent.getAuthRequired() != EaglercraftIsAuthRequiredEvent.AuthResponse.SKIP) {
								sendLoginDenied(ctx, "Client provided no authentication code").addListener(ChannelFutureListener.CLOSE);
								return;
							}else {
								continueThread.run();
							}
						}
					}else {
						continueThread.run();
					}
				}else {
					clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
					sendErrorWrong(ctx, op, "STATE_CLIENT_VERSION").addListener(ChannelFutureListener.CLOSE);
				}
			}
			break;
			case HandshakePacketTypes.PROTOCOL_CLIENT_PROFILE_DATA: {
				if(clientLoginState == HandshakePacketTypes.STATE_CLIENT_LOGIN) {
					if(clientProtocolVersion <= 3) {
						if(profileData.size() >= 12) {
							sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_EXCESSIVE_PROFILE_DATA, "Too many profile data packets recieved")
									.addListener(ChannelFutureListener.CLOSE);
							return;
						}
						int strlen = buffer.readUnsignedByte();
						String dataType = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
						strlen = buffer.readUnsignedShort();
						byte[] readData = new byte[strlen];
						buffer.readBytes(readData);
						
						if(buffer.isReadable()) {
							throw new IllegalArgumentException("Packet too long");
						}
						
						if(!profileData.containsKey(dataType)) {
							profileData.put(dataType, readData);
						}else {
							sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_DUPLICATE_PROFILE_DATA, "Multiple profile data packets of the same type recieved")
									.addListener(ChannelFutureListener.CLOSE);
							return;
						}
					}else {
						int count = buffer.readUnsignedByte();
						if(profileData.size() + count > 12) {
							sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_EXCESSIVE_PROFILE_DATA, "Too many profile data packets recieved")
									.addListener(ChannelFutureListener.CLOSE);
							return;
						}
						for(int i = 0; i < count; ++i) {
							int strlen = buffer.readUnsignedByte();
							String dataType = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
							strlen = buffer.readUnsignedShort();
							byte[] readData = new byte[strlen];
							buffer.readBytes(readData);
							if(!profileData.containsKey(dataType)) {
								profileData.put(dataType, readData);
							}else {
								sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_DUPLICATE_PROFILE_DATA, "Multiple profile data packets of the same type recieved")
										.addListener(ChannelFutureListener.CLOSE);
								return;
							}
						}
						
						if(buffer.isReadable()) {
							throw new IllegalArgumentException("Packet too long");
						}
					}
				}else {
					clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
					sendErrorWrong(ctx, op, "STATE_CLIENT_LOGIN").addListener(ChannelFutureListener.CLOSE);
				}
			}
			break;
			case HandshakePacketTypes.PROTOCOL_CLIENT_FINISH_LOGIN: {
				if(clientLoginState == HandshakePacketTypes.STATE_CLIENT_LOGIN) {
					clientLoginState = HandshakePacketTypes.STATE_STALLING;
					if(buffer.isReadable()) {
						throw new IllegalArgumentException("Packet too long");
					}
					
					finish(ctx);
					
					clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				}else {
					sendErrorWrong(ctx, op, "STATE_CLIENT_LOGIN").addListener(ChannelFutureListener.CLOSE);
				}
			}
			break;
			default:
				clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_UNKNOWN_PACKET, "Unknown Packet #" + op)
						.addListener(ChannelFutureListener.CLOSE);
				break;
			}
		}catch(Throwable ex) {
			if(ex instanceof EventException) {
				EaglerXBungee.logger().log(Level.SEVERE, "[" + localAddrString + "]: Hanshake packet " + op + " caught an exception", ex.getCause());
			}
			clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
			sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_INVALID_PACKET, op == -1 ?
					"Invalid Packet" : "Invalid Packet #" + op)
				.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	private void finish(final ChannelHandlerContext ctx) {
		final BungeeCord bungee = BungeeCord.getInstance();
		
		// verify player counts a second time after handshake just to be safe
		int limit = bungee.config.getPlayerLimit();
		if (limit > 0 && bungee.getOnlineCount() >= limit) {
			sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, bungee.getTranslation("proxy_full"))
					.addListener(ChannelFutureListener.CLOSE);
			return;
		}
		
		if(conf.getMaxPlayer() > 0) {
			int i = 0;
			for(ProxiedPlayer p : bungee.getPlayers()) {
				if(p.getPendingConnection().getListener() == conf) {
					++i;
				}
			}
			
			if (i >= conf.getMaxPlayer()) {
				sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, bungee.getTranslation("proxy_full"))
						.addListener(ChannelFutureListener.CLOSE);
				return;
			}
		}
		
		final String usernameStr = clientUsername.toString();
		final ProxiedPlayer oldName = bungee.getPlayer(usernameStr);
		if (oldName != null) {
			sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
					bungee.getTranslation("already_connected_proxy")).addListener(ChannelFutureListener.CLOSE);
			return;
		}
		final EaglerChannelWrapper ch = new EaglerChannelWrapper(ctx);
		InetSocketAddress baseAddress = (InetSocketAddress)ctx.channel().remoteAddress();
		InetAddress addr = ctx.channel().attr(EaglerPipeline.REAL_ADDRESS).get();
		if(addr != null) {
			baseAddress = new InetSocketAddress(addr, baseAddress.getPort());
			ch.setRemoteAddress(baseAddress);
		}
		
		EaglerUpdateConfig updateconf = EaglerXBungee.getEagler().getConfig().getUpdateConfig();
		boolean blockUpdate = updateconf.isBlockAllClientUpdates();
		ClientCertificateHolder cert = null;
		if(!blockUpdate && !updateconf.isDiscardLoginPacketCerts()) {
			byte[] b = profileData.get("update_cert_v1");
			if(b != null && b.length < 32759) {
				EaglerUpdateSvc.sendCertificateToPlayers(cert = EaglerUpdateSvc.tryMakeHolder(b));
			}
		}
		UUID clientBrandUUID = null;
		String clientBrandAsString = clientBrandString.toString();
		byte[] brandUUIDBytes = profileData.get("brand_uuid_v1");
		if(brandUUIDBytes != null) {
			if(brandUUIDBytes.length == 16) {
				ByteBuf buf = Unpooled.wrappedBuffer(brandUUIDBytes);
				clientBrandUUID = new UUID(buf.readLong(), buf.readLong());
				if (clientBrandUUID.equals(EaglerXBungeeAPIHelper.BRAND_NULL_UUID)
						|| clientBrandUUID.equals(EaglerXBungeeAPIHelper.BRAND_PENDING_UUID)
						|| clientBrandUUID.equals(EaglerXBungeeAPIHelper.BRAND_VANILLA_UUID)) {
					clientBrandUUID = null;
				}
			}
		}else {
			clientBrandUUID = EaglerXBungeeAPIHelper.makeClientBrandUUIDLegacy(clientBrandAsString);
		}
		Map<String,byte[]> otherProfileData = new HashMap<>();
		for(Entry<String,byte[]> etr2 : profileData.entrySet()) {
			String str = etr2.getKey();
			if(!profileDataStandard.contains(str)) {
				otherProfileData.put(str, etr2.getValue());
			}
		}
		final EaglerInitialHandler initialHandler = new EaglerInitialHandler(bungee, conf, ch, clientProtocolVersion,
				gameProtocolVersion, clientBrandAsString, clientVersionString.toString(), clientBrandUUID,
				usernameStr, clientUUID, offlineUUID, baseAddress, ctx.channel().attr(EaglerPipeline.HOST).get(),
				ctx.channel().attr(EaglerPipeline.ORIGIN).get(), ctx.channel().attr(EaglerPipeline.USER_AGENT).get(),
				cert, clientEnableCookie, clientCookieData, otherProfileData);
		if(!blockUpdate) {
			List<ClientCertificateHolder> set = EaglerUpdateSvc.getCertList();
			synchronized(set) {
				initialHandler.certificatesToSend.addAll(set);
			}
			for(ProxiedPlayer p : bungee.getPlayers()) {
				if(p.getPendingConnection() instanceof EaglerInitialHandler) {
					EaglerInitialHandler pp = (EaglerInitialHandler)p.getPendingConnection();
					if(pp.clientCertificate != null && pp.clientCertificate != cert) {
						initialHandler.certificatesToSend.add(pp.clientCertificate);
					}
				}
			}
		}
		final Callback<LoginEvent> complete = (Callback<LoginEvent>) new Callback<LoginEvent>() {
			public void done(final LoginEvent result, final Throwable error) {
				if (result.isCancelled()) {
					final BaseComponent reason = result.getReason();
					sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
							reason != null ? reason : TextComponent.fromLegacy(bungee.getTranslation("kick_message")))
							.addListener(ChannelFutureListener.CLOSE);
					return;
				}
				if (!ctx.channel().isActive()) {
					return;
				}
				
				ByteBuf buf = ctx.alloc().buffer();
				buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_FINISH_LOGIN);
				ctx.writeAndFlush(new BinaryWebSocketFrame(buf)).addListener(new GenericFutureListener<Future<Void>>() {

					@Override
					public void operationComplete(Future<Void> var1) throws Exception {
						EaglerConnectionInstance eaglerCon = ctx.channel().attr(EaglerPipeline.CONNECTION_INSTANCE).get();
						
						EaglerXBungee.logger().info("[" + ctx.channel().remoteAddress() + "]: Logged in as '" + usernameStr + "'");
						
						final UserConnection userCon = new UserConnection(bungee, ch, usernameStr, initialHandler);
						userCon.setCompressionThreshold(-1);
						initialHandler.messageProtocolController = new GameProtocolMessageController(userCon,
								GamePluginMessageProtocol.getByVersion(clientProtocolVersion),
								GameProtocolMessageController.createServerHandler(clientProtocolVersion, userCon,
										EaglerXBungee.getEagler()), conf.getDefragSendDelay());
						try {
							if (!userCon.init()) {
								userCon.disconnect(bungee.getTranslation("already_connected_proxy"));
								EaglerPipeline.closeChannel(ctx.channel());
								return;
							}
						} catch (NoSuchMethodError e) {
							UserConnection.class.getDeclaredMethod("init").invoke(userCon);
						}
						
						ChannelPipeline pp = ctx.channel().pipeline();
						
						HandlerBoss handler = new HandlerBoss() {
							public void channelInactive(ChannelHandlerContext ctx) throws Exception {
								super.channelInactive(ctx);
								EaglerPipeline.closeChannel(ctx.channel());
							}
						};
						handler.setHandler(new UpstreamBridge(bungee, userCon));
						try {
							handler.channelActive(ctx);
						} catch (Exception e) {
						}
						
						pp.replace(HttpWebSocketHandler.this, "HandlerBoss", handler);
						
						pp.addBefore("HandlerBoss", "ReadTimeoutHandler", new ReadTimeoutHandler((BungeeCord.getInstance()).config.getTimeout(), TimeUnit.MILLISECONDS));
						
						pp.addBefore("HandlerBoss", "EaglerMinecraftDecoder", new EaglerMinecraftDecoder(Protocol.GAME, false, gameProtocolVersion));
						
						pp.addBefore("HandlerBoss", "EaglerMinecraftByteBufEncoder", new EaglerMinecraftByteBufEncoder());
						
						pp.addBefore("HandlerBoss", "EaglerMinecraftWrappedEncoder", new EaglerMinecraftWrappedEncoder());
						
						pp.addBefore("HandlerBoss", "EaglerMinecraftEncoder", new EaglerMinecraftEncoder(Protocol.GAME, true, gameProtocolVersion));
						
						boolean doRegisterSkins = true;
						boolean doForceSkins = false;
						
						EaglercraftRegisterSkinEvent registerSkinEvent = new EaglercraftRegisterSkinEvent(usernameStr, clientUUID, authRequireEvent != null ? authRequireEvent.getAuthAttachment() : null);
						
						bungee.getPluginManager().callEvent(registerSkinEvent);
						
						Property prop = registerSkinEvent.getForceUseMojangProfileProperty();
						boolean useExistingProp = registerSkinEvent.getForceUseLoginResultObjectTextures();
						if(prop != null) {
							texturesOverrideProperty = prop;
							overrideEaglerToVanillaSkins = true;
							if(clientProtocolVersion >= 4 && (EaglerXBungee.getEagler().getSkinService() instanceof SkinService)) {
								doForceSkins = true;
							}
						}else {
							if(useExistingProp) {
								overrideEaglerToVanillaSkins = true;
							}else {
								byte[] custom = registerSkinEvent.getForceSetUseCustomPacket();
								if(custom != null) {
									profileData.remove("skin_v2");
									profileData.put("skin_v1", custom);
									overrideEaglerToVanillaSkins = false;
									if(clientProtocolVersion >= 4) {
										doForceSkins = true;
									}
								}
							}
						}
						
						EaglerBungeeConfig eaglerConf = EaglerXBungee.getEagler().getConfig();
						
						if(texturesOverrideProperty != null) {
							LoginResult oldProfile = initialHandler.getLoginProfile();
							if(oldProfile == null) {
								oldProfile = new LoginResult(initialHandler.getUniqueId().toString(), initialHandler.getName(), null);
								initialHandler.setLoginProfile(oldProfile);
							}
							oldProfile.setProperties(eaglerConf.getEnableIsEaglerPlayerProperty() ? new Property[] { texturesOverrideProperty, EaglerBungeeConfig.isEaglerProperty } : new Property[] { texturesOverrideProperty });
						}else {
							if(!useExistingProp) {
								String vanillaSkin = eaglerConf.getEaglerPlayersVanillaSkin();
								if(vanillaSkin != null) {
									LoginResult oldProfile = initialHandler.getLoginProfile();
									if(oldProfile == null) {
										oldProfile = new LoginResult(initialHandler.getUniqueId().toString(), initialHandler.getName(), null);
										initialHandler.setLoginProfile(oldProfile);
									}
									oldProfile.setProperties(eaglerConf.getEaglerPlayersVanillaSkinProperties());
								}
							}
						}
						
						if(overrideEaglerToVanillaSkins) {
							LoginResult res = initialHandler.getLoginProfile();
							if(res != null) {
								Property[] props = res.getProperties();
								if(props != null) {
									for(int i = 0; i < props.length; ++i) {
										if("textures".equals(props[i].getName())) {
											try {
												String jsonStr = SkinPackets.bytesToAscii(Base64.decodeBase64(props[i].getValue()));
												JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
												JsonObject skinObj = json.getAsJsonObject("SKIN");
												if(skinObj != null) {
													JsonElement url = json.get("url");
													if(url != null) {
														String urlStr = SkinService.sanitizeTextureURL(url.getAsString());
														EaglerXBungee.getEagler().getSkinService().registerTextureToPlayerAssociation(urlStr, initialHandler.getUniqueId());
													}
												}
												doRegisterSkins = false;
												if(clientProtocolVersion >= 4) {
													doForceSkins = true;
												}
											}catch(Throwable t) {
											}
											break;
										}
									}
								}
							}
						}
						
						if(doRegisterSkins) {
							if(clientProtocolVersion >= 4 && profileData.containsKey("skin_v2")) {
								try {
									SkinPackets.registerEaglerPlayer(clientUUID, profileData.get("skin_v2"),
											EaglerXBungee.getEagler().getSkinService(), 4);
								} catch (Throwable ex) {
									SkinPackets.registerEaglerPlayerFallback(clientUUID, EaglerXBungee.getEagler().getSkinService());
									EaglerXBungee.logger().info("[" + ctx.channel().remoteAddress() + "]: Invalid skin packet: " + ex.toString());
								}
							}else if(profileData.containsKey("skin_v1")) {
								try {
									SkinPackets.registerEaglerPlayer(clientUUID, profileData.get("skin_v1"),
											EaglerXBungee.getEagler().getSkinService(), 3);
								} catch (Throwable ex) {
									SkinPackets.registerEaglerPlayerFallback(clientUUID, EaglerXBungee.getEagler().getSkinService());
									EaglerXBungee.logger().info("[" + ctx.channel().remoteAddress() + "]: Invalid skin packet: " + ex.toString());
								}
							}else {
								SkinPackets.registerEaglerPlayerFallback(clientUUID, EaglerXBungee.getEagler().getSkinService());
							}
						}
						if(doForceSkins) {
							EaglerXBungee.getEagler().getSkinService().processForceSkin(clientUUID, initialHandler);
						}
						
						EaglercraftRegisterCapeEvent registerCapeEvent = new EaglercraftRegisterCapeEvent(usernameStr, clientUUID, authRequireEvent != null ? authRequireEvent.getAuthAttachment() : null);
						
						bungee.getPluginManager().callEvent(registerCapeEvent);
						
						byte[] forceCape = registerCapeEvent.getForceSetUseCustomPacket();
						if(forceCape != null) {
							profileData.put("cape_v1", forceCape);
						}
						
						if(profileData.containsKey("cape_v1")) {
							try {
								CapePackets.registerEaglerPlayer(clientUUID, profileData.get("cape_v1"),
										EaglerXBungee.getEagler().getCapeService());
							} catch (Throwable ex) {
								CapePackets.registerEaglerPlayerFallback(clientUUID, EaglerXBungee.getEagler().getCapeService());
								EaglerXBungee.logger().info("[" + ctx.channel().remoteAddress() + "]: Invalid cape packet: " + ex.toString());
							}
						}else {
							CapePackets.registerEaglerPlayerFallback(clientUUID, EaglerXBungee.getEagler().getCapeService());
						}
						if(forceCape != null && clientProtocolVersion >= 4) {
							EaglerXBungee.getEagler().getCapeService().processForceCape(clientUUID, initialHandler);
						}
						
						if(conf.getEnableVoiceChat()) {
							EaglerXBungee.getEagler().getVoiceService().handlePlayerLoggedIn(userCon);
						}
						
						if(clientProtocolVersion >= 4) {
							GameMessagePacket pauseMenuPkt = EaglerXBungee.getEagler().getConfig().getPauseMenuConf().getPacket();
							if(pauseMenuPkt != null) {
								initialHandler.sendEaglerMessage(pauseMenuPkt);
							}
						}
						
						ServerInfo server;
						if (bungee.getReconnectHandler() != null) {
							server = bungee.getReconnectHandler().getServer((ProxiedPlayer) userCon);
						} else {
							server = AbstractReconnectHandler.getForcedHost(initialHandler);
						}
						
						if (server == null) {
							server = bungee.getServerInfo(conf.getDefaultServer());
						}
						
						final ServerInfo server2 = server;
						Callback<PostLoginEvent> complete = new Callback<PostLoginEvent>() {
							@Override
							public void done(PostLoginEvent result, Throwable error) {
								eaglerCon.userConnection = userCon;
								eaglerCon.hasBeenForwarded = true;
								if (ch.isClosed()) {
									return;
								}
								userCon.connect(server2, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
							}
						};
						
						try {
							bungee.getPluginManager().callEvent(new PostLoginEvent(userCon, server, complete));
						}catch(NoSuchMethodError err) {
							PostLoginEvent login = PostLoginEvent.class.getDeclaredConstructor(ProxiedPlayer.class).newInstance(userCon);
							bungee.getPluginManager().callEvent(login);
							complete.done(login, null);
						}
					}
					
				});
			}
		};
		final Callback<PreLoginEvent> completePre = new Callback<PreLoginEvent>() {
			public void done(PreLoginEvent var1, Throwable var2) {
				if (var1.isCancelled()) {
					final BaseComponent reason = var1.getReason();
					sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
							reason != null ? reason : TextComponent.fromLegacy(bungee.getTranslation("kick_message")))
							.addListener(ChannelFutureListener.CLOSE);
				}else {
					bungee.getPluginManager().callEvent(new LoginEvent(initialHandler, complete));
				}
			}
		};
		bungee.getPluginManager().callEvent(new PreLoginEvent(initialHandler, completePre));
	}

	private static class EventException extends RuntimeException {
		public EventException(Throwable t) {
			super(t.toString(), t);
		}
	}

	private void handleText(ChannelHandlerContext ctx, String str) {
		if(connectionClosed) {
			return;
		}
		if (!hasFirstPacket && (conf.isAllowMOTD() || conf.isAllowQuery()) && str.length() < 128
				&& (str = str.toLowerCase()).startsWith("accept:")) {
			str = str.substring(7).trim();
			hasFirstPacket = true;
			hasBinaryConnection = false;
			
			if(CommandConfirmCode.confirmHash != null && str.equalsIgnoreCase(CommandConfirmCode.confirmHash)) {
				connectionClosed = true;
				ctx.writeAndFlush(new TextWebSocketFrame("OK")).addListener(ChannelFutureListener.CLOSE);
				CommandConfirmCode.confirmHash = null;
				return;
			}
			
			boolean isMOTD = str.startsWith("motd");
			
			SocketAddress localSocketAddr = ctx.channel().remoteAddress();
			InetAddress addr = ctx.channel().attr(EaglerPipeline.REAL_ADDRESS).get();
			
			String limiterAddress = null;
			RateLimitStatus queryRateLimit = RateLimitStatus.OK;
			if(addr != null) {
				limiterAddress = addr.getHostAddress();
			}else {
				if(localSocketAddr instanceof InetSocketAddress) {
					limiterAddress = ((InetSocketAddress)localSocketAddr).getAddress().getHostAddress();
				}
			}
			
			EaglerRateLimiter limiter = isMOTD ? conf.getRatelimitMOTD() : conf.getRatelimitQuery();
			if(limiterAddress != null && limiter != null) {
				queryRateLimit = limiter.rateLimit(limiterAddress);
			}
			
			if(queryRateLimit == RateLimitStatus.LOCKED_OUT) {
				connectionClosed = true;
				ctx.close();
				return;
			}
			
			if(queryRateLimit != RateLimitStatus.OK) {
				connectionClosed = true;
				final RateLimitStatus rateLimitTypeFinal = queryRateLimit;
				ctx.writeAndFlush(new TextWebSocketFrame(
						rateLimitTypeFinal == RateLimitStatus.LIMITED_NOW_LOCKED_OUT ? "{\"type\":\"locked\"}" : "{\"type\":\"blocked\"}"))
						.addListener(ChannelFutureListener.CLOSE);
				return;
			}
			
			HttpServerQueryHandler handler = null;
			if(isMOTD) {
				if(conf.isAllowMOTD()) {
					handler = new MOTDQueryHandler();
				}
			}else if(conf.isAllowQuery()) {
				handler = QueryManager.createQueryHandler(str);
			}
			if(handler != null) {
				ctx.pipeline().replace(HttpWebSocketHandler.this, "HttpServerQueryHandler", handler);
				ctx.pipeline().addBefore("HttpServerQueryHandler", "WriteTimeoutHandler", new WriteTimeoutHandler(5l, TimeUnit.SECONDS));
				ctx.channel().attr(EaglerPipeline.CONNECTION_INSTANCE).get().hasBeenForwarded = true;
				handler.beginHandleQuery(conf, ctx, str);
				if(handler instanceof MOTDQueryHandler) {
					EaglercraftMOTDEvent evt = new EaglercraftMOTDEvent((MOTDQueryHandler)handler);
					BungeeCord.getInstance().getPluginManager().callEvent(evt);
					if(!handler.isClosed()) {
						((MOTDQueryHandler)handler).sendToUser();
					}
				}
				if(!handler.isClosed() && !handler.shouldKeepAlive()) {
					connectionClosed = true;
					handler.close();
				}else {
					ctx.channel().attr(EaglerPipeline.CONNECTION_INSTANCE).get().queryHandler = handler;
				}
			}else {
				connectionClosed = true;
				ctx.close();
			}
		}else {
			connectionClosed = true;
			ctx.close();
			return;
		}
	}
	
	private int getAuthMethodId(EaglercraftIsAuthRequiredEvent.AuthMethod meth) {
		switch(meth) {
		case PLAINTEXT:
			return 255; // plaintext authentication
		case EAGLER_SHA256:
			return 1; // eagler_sha256 authentication
		case AUTHME_SHA256:
			return 2; // authme_sha256 authentication
		default:
			return -1;
		}
	}
	
	private ChannelFuture sendLoginDenied(ChannelHandlerContext ctx, String reason) {
		if((!isProtocolExchanged || clientProtocolVersion == 2) && reason.length() > 255) {
			reason = reason.substring(0, 255);
		}else if(reason.length() > 65535) {
			reason = reason.substring(0, 65535);
		}
		clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		connectionClosed = true;
		ByteBuf buf = ctx.alloc().buffer();
		buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_DENY_LOGIN);
		byte[] msg = reason.getBytes(StandardCharsets.UTF_8);
		if(!isProtocolExchanged || clientProtocolVersion == 2) {
			buf.writeByte(msg.length);
		}else {
			buf.writeShort(msg.length);
		}
		buf.writeBytes(msg);
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buf));
	}

	private ChannelFuture sendErrorWrong(ChannelHandlerContext ctx, int op, String state) {
		return sendErrorCode(ctx, HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET, "Wrong Packet #" + op + " in state '" + state + "'");
	}

	private ChannelFuture sendErrorCode(ChannelHandlerContext ctx, int code, BaseComponent comp) {
		if((!isProtocolExchanged || clientProtocolVersion == 2)) {
			return sendErrorCode(ctx, code, ComponentSerializer.toString(comp));
		}else {
			return sendErrorCode(ctx, code, comp.toLegacyText());
		}
	}

	private ChannelFuture sendErrorCode(ChannelHandlerContext ctx, int code, String str) {
		if((!isProtocolExchanged || clientProtocolVersion == 2) && str.length() > 255) {
			str = str.substring(0, 255);
		}else if(str.length() > 65535) {
			str = str.substring(0, 65535);
		}
		clientLoginState = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		connectionClosed = true;
		ByteBuf buf = ctx.alloc().buffer();
		buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ERROR);
		buf.writeByte(code);
		byte[] msg = str.getBytes(StandardCharsets.UTF_8);
		if(!isProtocolExchanged || clientProtocolVersion == 2) {
			buf.writeByte(msg.length);
		}else {
			buf.writeShort(msg.length);
		}
		buf.writeBytes(msg);
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buf));
	}
	
	public void channelInactive(ChannelHandlerContext ctx) {
		connectionClosed = true;
		EaglerPipeline.closeChannel(ctx.channel());
	}
	
	private void handleLegacyClient(ChannelHandlerContext ctx, ByteBuf buffer) {
		connectionClosed = true;
		ByteBuf kickMsg = ctx.alloc().buffer();
		final String redir = conf.redirectLegacyClientsTo();
		if(redir != null) {
			writeLegacyRedirect(kickMsg, redir);
		}else {
			writeLegacyKick(kickMsg, "This is an EaglercraftX 1.8 server, it is not compatible with 1.5.2!");
		}
		ctx.writeAndFlush(new BinaryWebSocketFrame(kickMsg)).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture var1) throws Exception {
				ctx.channel().eventLoop().schedule(new Runnable() {
					@Override
					public void run() {
						ctx.close();
					}
				}, redir != null ? 100l : 500l, TimeUnit.MILLISECONDS);
			}
		});
	}

	public static void writeLegacyKick(ByteBuf buffer, String message) {
		buffer.writeByte(0xFF);
		buffer.writeShort(message.length());
		for(int i = 0, l = message.length(), j; i < l; ++i) {
			j = message.charAt(i);
			buffer.writeByte((j >>> 8) & 0xFF);
			buffer.writeByte(j & 0xFF);
		}
	}

	public static void writeLegacyRedirect(ByteBuf buffer, String redirect) {
		buffer.writeBytes(legacyRedirectHeader);
		byte[] redirect_ = redirect.getBytes(StandardCharsets.UTF_8);
		buffer.writeByte((redirect_.length >>> 8) & 0xFF);
		buffer.writeByte(redirect_.length & 0xFF);
		buffer.writeBytes(redirect_);
	}

	private static final byte[] legacyRedirectHeader;

	static {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bao);
		try {
			// Packet1Login
			dos.writeByte(0x01);
			dos.writeInt(0);
			dos.writeShort(0);
			dos.writeByte(0);
			dos.writeByte(0);
			dos.writeByte(0xFF);
			dos.writeByte(0);
			dos.writeByte(0);
			// Packet250CustomPayload
			dos.writeByte(0xFA);
			String channel = "EAG|Reconnect";
			int cl = channel.length();
			dos.writeShort(cl);
			for(int i = 0; i < cl; ++i) {
				dos.writeChar(channel.charAt(i));
			}
		}catch(IOException ex) {
			throw new ExceptionInInitializerError(ex);
		}
		legacyRedirectHeader = bao.toByteArray();
	}

}