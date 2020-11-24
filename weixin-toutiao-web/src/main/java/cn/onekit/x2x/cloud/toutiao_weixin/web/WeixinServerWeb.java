package cn.onekit.x2x.cloud.toutiao_weixin.web;

import cn.onekit.thekit.DB;
import cn.onekit.thekit.JSON;
import cn.onekit.x2x.cloud.weixin_toutiao.WeixinServer;
import com.qq.weixin.api.entity.WeixinError;
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
                    DB.set("[weixin-toutiao] code_openid", tt_code, tt_openid);
                }

                @Override
                protected String _code_openid(String tt_code) {
                    return DB.get("[weixin-toutiao] code_openid", tt_code);
                }

                @Override
                protected void _openid_sessionkey(String tt_openid, String tt_sessionkey) {
                    DB.set("[weixin-toutiao] openid_sessionkey", tt_openid, tt_sessionkey);
                }

                @Override
                protected String _openid_sessionkey(String tt_openid) {
                    return DB.get("[weixin-toutiao] openid_sessionkey", tt_openid);
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




}