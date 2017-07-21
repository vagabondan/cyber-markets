package fund.cyber.markets.exchanges.poloniex

import fund.cyber.markets.model.poloniex
import fund.cyber.markets.storage.RethinkDbService
import fund.cyber.markets.webscoket.WebSocketContinuousConnectionManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession


@Component
open class PoloniexWebSocketConnectionManager(
        private val rethinkDbService: RethinkDbService
) : WebSocketContinuousConnectionManager<PoloniexMetadata>(poloniex) {

    override fun setupWebSocketHandler(metadata: PoloniexMetadata): WebSocketHandler {
        val messageParser = PoloniexMessageParser(metadata)
        return PoloniexWebSocketHandler(messageParser, rethinkDbService)
    }

    override fun subscribeChannels(session: WebSocketSession, metadata: PoloniexMetadata) {
        metadata.channelIdForTokensPairs.keys.forEach { channelId -> session.subscribeChannel(channelId) }
    }
}

/**
 * Warning! Sends messages on session.
 */
fun WebSocketSession.subscribeChannel(channelId: Int) {
    sendMessage(TextMessage("{\"command\":\"subscribe\",\"channel\":\"$channelId\"}"))
}