package com.mysema.dropbook.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FacebookRealm extends AuthorizingRealm {

    private static final Properties props = new FacebookProperties().getProps();
    private static final String APP_SECRET = props.get("fbAppSecret").toString();
    private static final String APP_ID = props.get("fbAppId").toString();
    private static final String REDIRECT_URL = props.get("fbRedirectUrl").toString();

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof FacebookToken) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new FacebookAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        FacebookToken facebookToken = (FacebookToken) token;

        if (facebookToken.getCode() != null && facebookToken.getCode().trim().length() > 0) {
            URL authUrl;
            try {
                authUrl = new URL("https://graph.facebook.com/oauth/access_token?" + "client_id=" + APP_ID
                        + "&redirect_uri=" + REDIRECT_URL + "&client_secret=" + APP_SECRET + "&code="
                        + facebookToken.getCode());

                String authResponse = readURL(authUrl);
                System.out.println(authResponse);
                String accessToken = getPropsMap(authResponse).get("access_token");
                URL url = new URL("https://graph.facebook.com/me?access_token=" + accessToken);
                String fbResponse = readURL(url);
                //FacebookUserDetails fud = new FacebookUserDetails(fbResponse);
                //return new FacebookAuthenticationInfo(fud, this.getName());
                return null;
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                throw new AuthenticationException(e1);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new AuthenticationException(ioe);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

// ------------------------------------------------------------
// STUFF here should be in a more generic place TODO
// ------------------------------------------------------------

    private String readURL(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }

    private Map<String, String> getPropsMap(String someString) {
        String[] pairs = someString.split("&");
        Map<String, String> props = new HashMap<String, String>();
        for (String propPair : pairs) {
            String[] pair = propPair.split("=");
            props.put(pair[0], pair[1]);
        }
        return props;
    }
}

