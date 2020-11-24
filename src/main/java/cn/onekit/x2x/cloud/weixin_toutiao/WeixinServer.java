package cn.onekit.x2x.cloud.weixin_toutiao;

import cn.onekit.thekit.AJAX;
import com.qq.weixin.api.WeixinAPI;
import com.qq.weixin.api.entity.*;
import com.toutiao.developer.ToutiaoSDK;
import com.toutiao.developer.ToutiaoSDK2;
import com.toutiao.developer.entity.Predict;
import com.toutiao.developer.entity.ToutiaoError;
import com.toutiao.developer.entity.apps__jscode2session_response;
import com.toutiao.developer.entity.apps__token_response;
import com.toutiao.developer.entity.v2.*;
import org.bouncycastle.util.encoders.Base64;

import java.util.Arrays;

public abstract class WeixinServer implements WeixinAPI {

    private final String tt_appid;
    private final String tt_secret;
    private ToutiaoSDK toutiaoSDK = new ToutiaoSDK("https://developer.toutiao.com");
    private ToutiaoSDK2 toutiaoSDK2 = new ToutiaoSDK2("https://developer.toutiao.com");

    public WeixinServer(String tt_appid, String tt_secret) {
        this.tt_appid = tt_appid;
        this.tt_secret = tt_secret;
    }

    abstract protected void _code_openid(String tt_code, String tt_openid);
    abstract protected String _code_openid(String tt_code);
    abstract protected void _openid_sessionkey(String tt_openid, String tt_sessionkey);
    abstract protected String _openid_sessionkey(String tt_openid);

    @Override
    public cgi_bin__token_response cgi_bin__token(String wx_grant_type, String wx_appid, String wx_secret) throws WeixinError {

        try {

            final String tt_grant_type = "client_credential";
            apps__token_response tt_response = toutiaoSDK.apps__token(tt_appid,tt_secret,tt_grant_type);
            /////////////////////
            cgi_bin__token_response wx_response = new cgi_bin__token_response();
            wx_response.setAccess_token(tt_response.getAccess_token());
            wx_response.setExpires_in((int)tt_response.getExpires_in());
            return wx_response;
        } catch (ToutiaoError e) {
            WeixinError wx_error = new WeixinError();
            wx_error.setErrcode(74077);
            wx_error.setErrmsg(e.getErrmsg());
            throw wx_error;
        }

    }

    @Override
    public WeixinResponse wxa__checksession(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, String wx_body) {
        return null;
    }

    @Override
    public snc__jscode2session_response snc__jscode2session(String wx_appid, String wx_secret, String wx_js_code, String wx_grant_type) {
        try {
            String tt_openid = _code_openid(wx_js_code);
            String tt_session_key;
            if(tt_openid!=null){
                tt_session_key = _openid_sessionkey(tt_openid);
                snc__jscode2session_response wx_response = new snc__jscode2session_response();
                wx_response.setOpenid(tt_openid);
                wx_response.setSession_key(tt_session_key);
                return wx_response;
            }
            apps__jscode2session_response tt_response = toutiaoSDK.apps__jscode2session(tt_appid, tt_secret, wx_js_code, null);

            tt_openid = tt_response.getOpenid();
            tt_session_key = tt_response.getSession_key();
            _code_openid(wx_js_code,tt_openid);
            _openid_sessionkey(tt_openid,tt_session_key);

            snc__jscode2session_response wx_response = new snc__jscode2session_response();
            wx_response.setOpenid(tt_openid);
            wx_response.setSession_key(tt_session_key);
            return wx_response;
        } catch (ToutiaoError toutiaoError) {
            toutiaoError.printStackTrace();
        }

       return new snc__jscode2session_response();


    }



    @Override
    public WeixinResponse wxa__img_sec_check(String wx_access_token, byte[] wx_body) {
        try {
            tags__image_body tt_body = null;
            byte[] de_body = Base64.decode(wx_body);

            for (tags__image_body.Task task : tt_body.getTasks()) {
                task.setImage_data(Arrays.toString(de_body));
            }
            WeixinResponse wx_response = new WeixinResponse();
            tags__image_response tt_response = toutiaoSDK2.tags__image(wx_access_token, tt_body);
            for (tags__image_response.Data data : tt_response.getData()) {
                for (Predict predict : data.getPredicts()) {
                    if(predict.getProb() == 0 && !predict.isHit()){
                        wx_response.setErrcode(0);
                        wx_response.setErrmsg("ok");
                    }else {
                        wx_response.setErrcode(87014);
                        wx_response.setErrmsg("risky content");
                    }

                }
            }
        } catch (ToutiaoError2 toutiaoError2) {
            WeixinResponse wx_response = new WeixinResponse();
            wx_response.setErrcode(toutiaoError2.getCode());
            wx_response.setErrmsg(toutiaoError2.getMessage());
            return wx_response;
        }

        return null;
    }

    @Override
    public wxa__media_check_async_response wxa__media_check_async(String wx_access_token, wxa__media_check_async_body wx_body) {
        return null;
    }

    @Override
    public WeixinResponse wxa__msg_sec_check(String wx_access_token, wxa__msg_sec_check_body wx_body) {

        try {
            tags__text__antidirt_body tt_body = null;
            for (tags__text__antidirt_body.Task task : tt_body.tasks) {
                task.setContent(wx_body.getContent());
            }
            tags__text__antidirt_response tt_response = toutiaoSDK2.tags__text__antidirt(wx_access_token, tt_body);
        } catch (ToutiaoError2 toutiaoError2) {
            WeixinResponse wx_response = new WeixinResponse();
            wx_response.setErrcode(toutiaoError2.getCode());
            wx_response.setErrmsg(toutiaoError2.getMessage());
            return wx_response;
        }


        return null;
    }

    @Override
    public WeixinResponse wxa__remove_user_storage(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, wxa__remove_user_storage_body wx_body) {
        return null;
    }

    @Override
    public WeixinResponse wxa__setuserinteractivedata(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, wxa__setuserinteractivedata_body wx_body) {
        return null;
    }

    @Override
    public WeixinResponse wxa__set_user_storage(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, wxa__set_user_storage_body wx_body) {
        return null;
    }

    @Override
    public cgi_bin__message__wxopen__activityid__create_response cgi_bin__message__wxopen__activityid__create(String wx_access_token, String unionid) {
        return null;
    }

    @Override
    public WeixinResponse cgi_bin__message__wxopen__updatablemsg__send(String wx_access_token, updatablemsg__send_body wx_body) {
        return null;
    }

    @Override
    public byte[] cgi_bin__wxaapp__createwxaqrcode(String wx_access_token, wxaapp__createwxaqrcode_body wx_body) throws WeixinError {
        return new byte[0];
    }

    @Override
    public byte[] wxa__getwxacode(String wx_access_token, wxa__getwxacode_body wx_body) throws WeixinError {
        return new byte[0];
    }

    @Override
    public byte[] wxa__getwxacodeunlimit(String wx_access_token, wxa__getwxacodeunlimit_body wx_body) throws WeixinError {
        return new byte[0];
    }

    @Override
    public WeixinResponse cgi_bin__message__subscribe__send(String wx_access_token, subscribe__send_body wx_body) {
        return null;
    }
}
