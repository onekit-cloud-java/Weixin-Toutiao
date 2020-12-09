package cn.onekit.x2x.cloud.toutiao_weixin.web;


import cn.onekit.thekit.FileDB;
import cn.onekit.thekit.JSON;
import cn.onekit.x2x.cloud.weixin_toutiao.WeixinServer;
import com.qq.weixin.api.entity.*;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/")
public class WeixinServerWeb {

private WeixinServer _weixinServer;
    private WeixinServer weixinServer() {
        if (_weixinServer == null) {
            _weixinServer = new WeixinServer(ToutiaoAccount.tt_appid, ToutiaoAccount.tt_secret) {
                @Override
                protected void _code_openid(String tt_code, String tt_openid) {
                    FileDB.set("[weixin-toutiao] code_openid", tt_code, tt_openid);
                }

                @Override
                protected FileDB.Data _code_openid(String tt_code) {
                    return FileDB.get("[weixin-toutiao] code_openid", tt_code);
                }

                @Override
                protected void _openid_sessionkey(String tt_openid, String tt_sessionkey) {
                    FileDB.set("[weixin-toutiao] openid_sessionkey", tt_openid, tt_sessionkey);
                }

                @Override
                protected FileDB.Data _openid_sessionkey(String tt_openid) {
                    return FileDB.get("[weixin-toutiao] openid_sessionkey", tt_openid);
                }
            };
        }
        return _weixinServer;
    }

        @RequestMapping(method = RequestMethod.GET, value = "/cgi-bin/token")
        public String getAccessToken(
                @RequestParam String appid,
                @RequestParam String secret,
                @RequestParam String grant_type
        )  {
            try {
                return JSON.object2string(weixinServer().cgi_bin__token(grant_type,appid,secret));
            } catch (WeixinError weixinError) {
                return JSON.object2string(weixinError);
            }catch (Exception errror){
                WeixinError weixinError = new WeixinError();
                weixinError.setErrcode(500);
                weixinError.setErrmsg(errror.getMessage());
                return JSON.object2string(weixinError);
            }
        }
        @RequestMapping(method = RequestMethod.GET,value = "/sns/jscode2session")
        public String code2Session(
                @RequestParam String appid,
                @RequestParam String secret,
                @RequestParam String js_code,
                @RequestParam String grant_type){
            try {
                return JSON.object2string(weixinServer().snc__jscode2session(appid,secret,js_code,grant_type));
            }catch (Exception errror){
                WeixinError weixinError = new WeixinError();
                weixinError.setErrcode(500);
                weixinError.setErrmsg(errror.getMessage());
                return JSON.object2string(weixinError);
            }
        }

        @RequestMapping(method = RequestMethod.POST,value = "/wxa/img_sec_check")
        public String imgSecCheck(
            @RequestParam String access_token,
            @RequestBody byte[] wx_body
        ){
            try {
               return JSON.object2string(weixinServer().wxa__img_sec_check(access_token,wx_body));
            }catch (Exception errror){
               WeixinError weixinError = new WeixinError();
               weixinError.setErrcode(500);
               weixinError.setErrmsg(errror.getMessage());
               return JSON.object2string(weixinError);
            }
        }
    @RequestMapping(method = RequestMethod.POST,value = "/wxa/media_check_async")
    public String mediaCheckAsync(
            @RequestParam String access_token,
            @RequestBody String wx_body
    ){
        try {
            return JSON.object2string(weixinServer().wxa__media_check_async(access_token,JSON.string2object(wx_body,wxa__media_check_async_body.class)));
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return JSON.object2string(weixinError);
        }
    }
    @RequestMapping(method = RequestMethod.POST,value = "/wxa/msg_sec_check")
    public String msgSecCheck(
            @RequestParam String access_token,
            @RequestBody String wx_body){
        try {
              return JSON.object2string(weixinServer().wxa__msg_sec_check(access_token,JSON.string2object(wx_body, wxa__msg_sec_check_body.class)));
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return JSON.object2string(weixinError);
        }
    }
    /*@RequestMapping(method =RequestMethod.POST,value = "/wxa/remove_user_storage")
    public String removeUserStorage(
            HttpServletRequest request,
            @RequestBody String wx_body
            ){
        try {
            String sig_method = request.getParameter("sig_method");
            String access_token = request.getParameter("access_token");
            String openid = request.getParameter("openid");
            String signature = request.getParameter("signature");
            return JSON.object2string(weixinServer().wxa__remove_user_storage(access_token,openid,signature,sig_method,JSON.string2object(wx_body, wxa__remove_user_storage_body.class)));
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return JSON.object2string(weixinError);
        }
    }
    @RequestMapping(method =RequestMethod.POST,value = "/wxa/set_user_storage")
    public String setUserStorage(
           HttpServletRequest request,
           @RequestBody String wx_body
    ){
        try {
            String sig_method = request.getParameter("sig_method");
            String access_token = request.getParameter("access_token");
            String openid = request.getParameter("openid");
            String signature = request.getParameter("signature");
            return JSON.object2string(weixinServer().wxa__set_user_storage(access_token,openid,signature,sig_method,JSON.string2object(wx_body, wxa__set_user_storage_body.class)));
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return JSON.object2string(weixinError);
        }
    }*/
    @RequestMapping(method =RequestMethod.POST,value = "/cgi-bin/wxaapp/createwxaqrcode")
    public byte[] createQRCode(
            @RequestParam String access_token,
            @RequestBody String wx_body){
        try {
            return weixinServer().cgi_bin__wxaapp__createwxaqrcode(access_token,JSON.string2object(wx_body, wxaapp__createwxaqrcode_body.class));
        } catch (WeixinError weixinError) {
            return null;
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return null;
        }
    }
    @RequestMapping(method =RequestMethod.POST,value = "/wxa/getwxacode")
    public byte[] get(
            @RequestParam String access_token,
            @RequestBody String wx_body){
        try {
            return weixinServer().wxa__getwxacode(access_token,JSON.string2object(wx_body, wxa__getwxacode_body.class));
        } catch (WeixinError weixinError) {
            return null;
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return null;
        }
    }
    @RequestMapping(method =RequestMethod.POST,value = "/wxa/getwxacodeunlimit")
    public byte[] getUnlimited(
            @RequestParam String access_token,
            @RequestBody String wx_body){
        try {
            return weixinServer().wxa__getwxacodeunlimit(access_token,JSON.string2object(wx_body, wxa__getwxacodeunlimit_body.class));
        } catch (WeixinError weixinError) {
            return null;
        }catch (Exception errror){
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return null;
        }
    }
    @RequestMapping(method =RequestMethod.POST,value = "/cgi-bin/message/subscribe/send")
    public String send(
            @RequestParam String access_token,
            @RequestBody String wx_body
    ) {
        try {
            return JSON.object2string(weixinServer().cgi_bin__message__subscribe__send(access_token, JSON.string2object(wx_body, cgi_bin__message__subscribe__send_body.class)));
        } catch (Exception errror) {
            WeixinError weixinError = new WeixinError();
            weixinError.setErrcode(500);
            weixinError.setErrmsg(errror.getMessage());
            return JSON.object2string(weixinError);
        }
    }




}