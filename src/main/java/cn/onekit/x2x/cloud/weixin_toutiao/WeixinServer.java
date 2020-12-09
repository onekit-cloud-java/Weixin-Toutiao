package cn.onekit.x2x.cloud.weixin_toutiao;



import cn.onekit.thekit.FileDB;
import com.qq.weixin.api.WeixinAPI;
import com.qq.weixin.api.entity.*;
import com.toutiao.developer.ToutiaoSDK;
import com.toutiao.developer.ToutiaoSDK2;
import com.toutiao.developer.entity.*;
import com.toutiao.developer.entity.v2.*;


import java.util.*;

public abstract class WeixinServer implements WeixinAPI {

    private final String tt_appid;
    private final String tt_secret;
    private ToutiaoSDK toutiaoSDK = new ToutiaoSDK("https://developer.toutiao.com");
    private ToutiaoSDK2 toutiaoSDK2 = new ToutiaoSDK2("https://developer.toutiao.com");

    @SuppressWarnings("WeakerAccess")
    public WeixinServer(String tt_appid, String tt_secret) {
        this.tt_appid = tt_appid;
        this.tt_secret = tt_secret;
    }

    abstract protected void _code_openid(String tt_code, String tt_openid);
    abstract protected FileDB.Data _code_openid(String tt_code);
    abstract protected void _openid_sessionkey(String tt_openid, String tt_sessionkey);
    abstract protected FileDB.Data _openid_sessionkey(String tt_openid);

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
        snc__jscode2session_response wx_response = new snc__jscode2session_response();
        try {
            FileDB.Data tt_openid_data = _code_openid(wx_js_code);
            FileDB.Data tt_session_key_data = null;
            if(tt_openid_data!=null){
                tt_session_key_data = _openid_sessionkey(tt_openid_data.value);
                wx_response.setOpenid(tt_openid_data.value);
                wx_response.setSession_key(tt_session_key_data.value);
                return wx_response;
            }
            apps__jscode2session_response tt_response = toutiaoSDK.apps__jscode2session(tt_appid, tt_secret, wx_js_code, null);

            assert false;
            tt_openid_data.value = tt_response.getOpenid();
            tt_session_key_data.value = tt_response.getSession_key();
            _code_openid(wx_js_code,tt_openid_data.value);
            _openid_sessionkey(tt_openid_data.value,tt_session_key_data.value);

            wx_response.setOpenid(tt_openid_data.value);
            wx_response.setSession_key(tt_session_key_data.value);
            return wx_response;
        } catch (ToutiaoError toutiaoError) {
            toutiaoError.printStackTrace();
        }

       return wx_response;


    }



    @SuppressWarnings("DuplicatedCode")
    @Override
    public WeixinResponse wxa__img_sec_check(String wx_access_token, byte[] wx_body) {
        WeixinResponse wx_response = new WeixinResponse();
        try {
            tags__image_body tt_body = new tags__image_body();


            ArrayList<tags__image_body.Target> targets = new ArrayList<>();
            targets.add(tags__image_body.Target.ad);
            targets.add(tags__image_body.Target.porn);
            targets.add(tags__image_body.Target.politics);
            targets.add(tags__image_body.Target.disgusting);

            ArrayList<tags__image_body.Task> tasks = tt_body.getTasks();
            tags__image_body.Task task = new tags__image_body.Task();
            task.setImage_data(Arrays.toString(wx_body));
            tasks.add(task);
            tt_body.setTargets(targets);
            tt_body.setTasks(tasks);

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
            wx_response.setErrcode(toutiaoError2.getCode());
            wx_response.setErrmsg(toutiaoError2.getMessage());
            return wx_response;
        }

        return wx_response;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public wxa__media_check_async_response wxa__media_check_async(String wx_access_token, wxa__media_check_async_body wx_body) {

        wxa__media_check_async_response wx_response = new wxa__media_check_async_response();

        try {

            tags__image_body tt_body = new tags__image_body();

            ArrayList<tags__image_body.Target> targets = new ArrayList<>();
            targets.add(tags__image_body.Target.ad);
            targets.add(tags__image_body.Target.porn);
            targets.add(tags__image_body.Target.politics);
            targets.add(tags__image_body.Target.disgusting);

            ArrayList<tags__image_body.Task> tasks = tt_body.getTasks();
            tags__image_body.Task task = new tags__image_body.Task();
            task.setImage(wx_body.getMedia_url());
            tasks.add(task);
            tt_body.setTasks(tasks);
            tt_body.setTargets(targets);
            tags__image_response tt_response = toutiaoSDK2.tags__image(wx_access_token, tt_body);
            for (tags__image_response.Data data : tt_response.getData()) {
                wx_response.setTrace_id(data.getTask_id());
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
            wx_response.setErrcode(toutiaoError2.getCode());
            wx_response.setErrmsg(toutiaoError2.getMessage());
            return wx_response;
        }
        return wx_response;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public WeixinResponse wxa__msg_sec_check(String wx_access_token, wxa__msg_sec_check_body wx_body) {
        WeixinResponse wx_response = new WeixinResponse();
        try {
            tags__text__antidirt_body tt_body = new tags__text__antidirt_body();
            tags__text__antidirt_body.Task task = new tags__text__antidirt_body.Task();
            task.setContent(wx_body.getContent());
            ArrayList<tags__text__antidirt_body.Task> tasks = new ArrayList<>();
            tasks.add(task);
            tt_body.setTasks(tasks);

            tags__text__antidirt_response tt_response = toutiaoSDK2.tags__text__antidirt(wx_access_token, tt_body);
            for (tags__text__antidirt_response.Data data : tt_response.getData()) {
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
            wx_response.setErrcode(toutiaoError2.getCode());
            wx_response.setErrmsg(toutiaoError2.getMessage());
            return wx_response;
        }


        return wx_response;
    }

   /* @Override
    public WeixinResponse wxa__remove_user_storage(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, wxa__remove_user_storage_body wx_body) {
        WeixinResponse wx_response = new WeixinResponse();
        apps__remove_user_storage_body tt_body = new apps__remove_user_storage_body();
        ArrayList<String> keys = new ArrayList<>();
        for (String key : wx_body.getKey()) {
            keys.add(key);
        }
        tt_body.setKey(keys);
        String tt_session_key = _openid_sessionkey(wx_openid);
        String tt_signature = null;
        try {
           tt_signature = _signBody(tt_sig_method,tt_session_key, JSON.object2string(tt_body));
        } catch (Exception e) {
            e.printStackTrace();
        }
        apps__remove_user_storage_response tt_response = null;
        try {
            tt_response = toutiaoSDK.apps__remove_user_storage(wx_access_token, wx_openid, tt_signature, tt_sig_method, tt_body);
        } catch (ToutiaoError toutiaoError) {
            toutiaoError.printStackTrace();
        }
        if(tt_response.getError() != 0){
            ToutiaoError tt_error = new ToutiaoError();
            wx_response.setErrcode(-1);
            wx_response.setErrmsg(tt_error.getErrmsg());
        }else {
            wx_response.setErrcode((int) tt_response.getError());
        }

        return wx_response;

    }

    @Override
    public WeixinResponse wxa__setuserinteractivedata(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, wxa__setuserinteractivedata_body wx_body) {
        return null;
    }

    @Override
    public WeixinResponse wxa__set_user_storage(String wx_access_token, String wx_openid, String wx_signature, String wx_sig_method, wxa__set_user_storage_body wx_body) {
        WeixinResponse wx_response = new WeixinResponse();
        String tt_session_key = _openid_sessionkey(wx_openid);
        apps__set_user_storage_body tt_body = new apps__set_user_storage_body();
        ArrayList<com.toutiao.developer.entity.KV> list = new ArrayList<>();

        List<KV<String>> kv_list = wx_body.getKv_list();
        for (KV<String> kv : kv_list) {
            String key = kv.getKey();
            String value = kv.getValue();
            KV<String> kv1 = new KV<>(key,value);
            kv_list.add(kv1);
        }

        tt_body.setTt_kv_list(list);
        String tt_signature = null;
        try {
            tt_signature = _signBody(tt_sig_method,tt_session_key, JSON.object2string(tt_body));
        } catch (Exception e) {
            e.printStackTrace();
        }
        apps__set_user_storage_response tt_response = null;
        try {
            tt_response = toutiaoSDK.apps__set_user_storage(wx_access_token, tt_session_key, tt_signature, tt_sig_method, tt_body);
        } catch (ToutiaoError toutiaoError) {
            toutiaoError.printStackTrace();
        }
        if(tt_response.getError() != 0){
            ToutiaoError tt_error = new ToutiaoError();
            switch ((int) tt_response.getError()){
                case -1:
                    wx_response.setErrcode(-1);
                    wx_response.setErrmsg(tt_error.getErrmsg());
                    break;
                case 40009:
                    wx_response.setErrcode(87019);
                    wx_response.setErrmsg(tt_error.getErrmsg());
                    break;
                case 40010:
                    wx_response.setErrcode(87016);
                    wx_response.setErrmsg(tt_error.getErrmsg());
                    break;
                case 60001:
                    wx_response.setErrcode(87017);
                    wx_response.setErrmsg(tt_error.getErrmsg());
                    break;
                default:
                    wx_response.setErrcode(74077);
                    wx_response.setErrmsg(tt_error.getErrmsg());
            }
        }else {
            wx_response.setErrcode(0);

        }


        return wx_response;
    }

    @Override
    public cgi_bin__message__wxopen__activityid__create_response cgi_bin__message__wxopen__activityid__create(String wx_access_token, String unionid) {
        return null;
    }

    @Override
    public WeixinResponse cgi_bin__message__wxopen__updatablemsg__send(String wx_access_token, updatablemsg__send_body wx_body) {
        return null;
    }*/

    @SuppressWarnings("DuplicatedCode")
    @Override
    public byte[] cgi_bin__wxaapp__createwxaqrcode(String wx_access_token, wxaapp__createwxaqrcode_body wx_body) throws WeixinError {

        apps__qrcode_body tt_body = new apps__qrcode_body();

        tt_body.setAccess_token(wx_access_token);
        tt_body.setPath(wx_body.getPath());
        tt_body.setWidth(wx_body.getWidth());


        try {
            return toutiaoSDK.apps__qrcode(tt_body);
        } catch (ToutiaoError toutiaoError) {
            WeixinError wx_error = new WeixinError();
            wx_error.setErrcode(74077);
            wx_error.setErrmsg(toutiaoError.getErrmsg());
            throw wx_error;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public byte[] wxa__getwxacode(String wx_access_token, wxa__getwxacode_body wx_body) throws WeixinError {
        apps__qrcode_body tt_body = new apps__qrcode_body();

        tt_body.setAccess_token(wx_access_token);
        tt_body.setPath(wx_body.getPath());
        tt_body.setWidth(wx_body.getWidth());


        try {
            return toutiaoSDK.apps__qrcode(tt_body);
        } catch (ToutiaoError toutiaoError) {
            WeixinError wx_error = new WeixinError();
            wx_error.setErrcode(74077);
            wx_error.setErrmsg(toutiaoError.getErrmsg());
            throw wx_error;
        }
    }



    @Override
    public byte[] wxa__getwxacodeunlimit(String wx_access_token, wxa__getwxacodeunlimit_body wx_body) throws WeixinError {
        apps__qrcode_body tt_body = new apps__qrcode_body();

        tt_body.setAccess_token(wx_access_token);

        try {
            return toutiaoSDK.apps__qrcode(tt_body);
        } catch (ToutiaoError toutiaoError) {
            WeixinError wx_error = new WeixinError();
            wx_error.setErrcode(74077);
            wx_error.setErrmsg(toutiaoError.getErrmsg());
            throw wx_error;
        }
    }

    @Override
    public WeixinResponse cgi_bin__message__subscribe__send(String wx_access_token, cgi_bin__message__subscribe__send_body wx_body) {
        WeixinResponse wx_response = new WeixinResponse();
        try {
            Subscribe2Subscribe subscribe2subscribe=new Subscribe2Subscribe();
            apps__subscribe_notification__developer__notify_body tt_body = new apps__subscribe_notification__developer__notify_body();
            tt_body.setAccess_token(wx_access_token);
            tt_body.setApp_id(tt_appid);
            tt_body.setTpl_id(subscribe2subscribe.id2id(wx_body.getTemplate_id()));
            tt_body.setPage(wx_body.getPage());
            tt_body.setOpen_id(wx_body.getTouser());

            HashMap<String, String> key2key = subscribe2subscribe.id2keys(wx_body.getTemplate_id());
            HashMap<String, cgi_bin__message__subscribe__send_body.Data.DataValue> wx_data = wx_body.getData();
            for (Map.Entry<String, cgi_bin__message__subscribe__send_body.Data.DataValue> entry : wx_data.entrySet()) {
                String key = entry.getKey();
                cgi_bin__message__subscribe__send_body.Data.DataValue dataValue = entry.getValue();
                key2key.put(key,dataValue.getValue());
            }
            tt_body.setData(key2key);
            apps__subscribe_notification__developer__notify_response tt_response = toutiaoSDK.apps__subscribe_notification__developer__notify(tt_body);

            if(tt_response.getErr_no() != 0){
                switch (tt_response.getErr_no()){
                    case 40001:
                        wx_response.setErrcode(44002);
                        wx_response.setErrmsg(tt_response.getErr_tips());
                        break;
                    case 40014:
                        wx_response.setErrcode(41001);
                        wx_response.setErrmsg(tt_response.getErr_tips());
                        break;
                    case 40037:
                        wx_response.setErrcode(40037);
                        wx_response.setErrmsg(tt_response.getErr_tips());
                        break;
                    default:
                        wx_response.setErrcode(74077);
                        wx_response.setErrmsg(tt_response.getErr_tips());
                        break;
                }
            }else{
                wx_response.setErrcode(tt_response.getErr_no());
                wx_response.setErrmsg(tt_response.getErr_tips());
            }
        } catch (ToutiaoError toutiaoError) {
            toutiaoError.printStackTrace();
        }

        return wx_response;
    }
}
