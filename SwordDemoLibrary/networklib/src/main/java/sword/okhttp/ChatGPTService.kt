package sword.okhttp

import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy

class ChatGPTService {
    val okHttpClient = OkHttpClient.Builder()
        .proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 1086))) //设置代理

}