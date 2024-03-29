package sword.okhttp.library

import java.io.IOException

interface Callback {
  fun onFailure(call: SwordRealCall, e: IOException)
  fun onResponse(call: SwordRealCall, response: SwordResponse)
}