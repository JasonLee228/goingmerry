package com.example.demo.config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.utils.CookieUtils;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	private static final Logger logger = LogManager.getLogger(CustomLogoutSuccessHandler.class);

	@Autowired
	private Environment env;
	
	@Autowired
	private CookieUtils cookieUtils;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		logger.debug("##logoutSuccess method all debug##" + this.getClass());
		
		String frontendAppEntryPage = env.getProperty("frontend-app.entry");
		//DefaultAuthorizationCodeTokenResponseClient


		System.out.println("logout cookieUtils");
		if (logger.isDebugEnabled()) {
			logger.debug(cookieUtils);
		}

		response.addCookie(cookieUtils.generateRemoveJwtCookie(env.getProperty("jwt.token-name"), ""));
		response.addCookie(cookieUtils.generateRemoveJwtCookie(env.getProperty("jwt.token-name") + "-flag", ""));

		getRedirectStrategy().sendRedirect(request, response, frontendAppEntryPage);



		//logout success service made

		if (logger.isDebugEnabled()) {
			logger.debug("화이팅");
		}


	}

	public void logout(String access_Token, String registrationId) {
		if(registrationId.equals("kakao")){
			String reqURL = "https://kapi.kakao.com/v1/user/logout";
			try {
				URL url = new URL(reqURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Authorization", "Bearer " + access_Token);
				int responseCode = conn.getResponseCode();
				System.out.println("responseCode : " + responseCode);
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String result = "";
				String line = "";
				while ((line = br.readLine()) != null) {
					result += line;
				}
				logger.debug(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(registrationId.equals("google")){

		}else if(registrationId.equals("naver")){

		}
	}



}
