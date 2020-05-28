package me.violinsolo.boman.activity.prerequisite.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jaeger.library.StatusBarUtil;

import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityLoginPortalBinding;
import me.violinsolo.boman.util.StatusBarUtilNEW;

public class LoginPortalActivity extends BaseActivity<ActivityLoginPortalBinding> {
    public static final String TAG = LoginPortalActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityLoginPortalBinding onBind() {
        return ActivityLoginPortalBinding.inflate(getLayoutInflater());
    }

    /**
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    @Override
    protected void initData() {

    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {
        // set current status bar alpha 0 and set the title font color to dark(to suit the light mode)
//        StatusBarUtil.setLightMode(this); //exist bug in fullscreen mode...
        StatusBarUtilNEW.setLightMode(this);
        StatusBarUtil.setColor(this, getResources().getColor(android.R.color.white), 0);
//        StatusBarUtil.setTransparent(this);


        mBinder.btnWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "wechat login");
            }
        });
        mBinder.btnPhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "phone login");
                finish();
            }
        });
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
