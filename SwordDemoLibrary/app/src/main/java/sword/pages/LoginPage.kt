package sword.pages

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import sword.logic.LoginLogic
import sword.dp
import sword.theme.Theme
import sword.logger.SwordLog

class LoginPage(val context: Context, private val loginListener: LoginListener? = null) : Page {
  private val tag = "LoginPageView"
  private val marginHorizontal = 10.dp
  private val loginLogic = LoginLogic()


  override val rootView by lazy {
    createView()
  }

  private fun createView(): View {
    SwordLog.debug(tag, "createView")
    return LinearLayout(context).apply {
      orientation = LinearLayout.VERTICAL

      val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      ).apply {
        gravity = Gravity.CENTER_HORIZONTAL
        leftMargin = marginHorizontal
        rightMargin = marginHorizontal
      }


      val userNameEditText = Theme.createEditText(context).apply {
        hint = "请输入手机号/用户名"
        contentDescription = "账号"
      }
      addView(userNameEditText, layoutParams)

      val passwordEditText = Theme.createEditText(context).apply {
        hint = "请输入登录密码"
        contentDescription = "密码"
      }
      addView(passwordEditText, layoutParams)

      val loginButton = Theme.createCircleButton(context).apply {
        text = "登录"
        contentDescription = "登录"
        setOnClickListener {
          val username = userNameEditText.text.toString()
          if (loginLogic.login(
              context,
              username,
              passwordEditText.text.toString()
            )
          ) {
            loginListener?.loginSuccess(username)
          } else {
            loginListener?.loginFailed(username, "登录失败")
          }
        }
      }
      addView(loginButton)
    }

  }

  interface LoginListener {
    fun loginSuccess(username: String) {}
    fun loginFailed(username: String, msg: String) {}
  }

}