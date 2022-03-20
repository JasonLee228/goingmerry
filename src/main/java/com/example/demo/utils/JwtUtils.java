package com.example.demo.utils;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {
	
	private static final Logger logger = LogManager.getLogger(JwtUtils.class);
	
	@Autowired
	Environment env;//Environment = property, profiles 설정에 접근할 수 있는 스프링 환경 인터페이스.
	
	
    //토큰 유효성 검사
    public Boolean isValidateToken(String token) {
    	try {
	        
    		final String subject = (String) getBobyFromToken(token).get("sub");
	        return !subject.isEmpty();
	        
    	} catch (Exception e) {
			return false;
		}

    }

    // 토큰 만료 검사 -> 만료 기간 얼마 안 남았거나 만료됐을 시 재발급해주는 과정이 필요. 이는 access와 refresh의 필요성 또한 말해준다.
	//토큰 만료 검사와 refresh를 위해서 유저생성 또는 로그인 시에 토큰을 발급하는 과정에서 유저 정보에 refresh토큰을 저장하고, access토큰을 주 토큰으로 사용할 예정
	//해당 코드를 통해 만료 검사에서 탈락한 토큰은 재발급해주는 코드가 필요
    public boolean isTokenExpired(String token) {
    	try {
	    	long exp = (Long) getBobyFromToken(token).get("exp");
	        final Date expiration = new Date(exp);

			logger.debug("토큰 만료 검사");
			logger.debug(expiration);

	        return expiration.before(new Date());//-> ?
	        
    	}catch (Exception e) {
			return false;
		}
    }
    
    // 토큰 발급, 아래 토큰 내용들.
    public <T> String generateAccessToken(T userDetails) {
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug(userDetails);
    	}    	
    	
    	Map<String,Object> claim = new HashMap<>();//토큰 내용
    	
    	if (userDetails instanceof DefaultOAuth2User) {
    		    		
    		claim.put("iss", env.getProperty("jwt.token-issuer"));  // 발급자. yml 파일에 접근하여 가져온다. 아래도 다 같음.
    		claim.put("sub",  ((DefaultOAuth2User) userDetails).getName()); // subject 인증 대상(고유 ID)
    		
    		claim.put("email", ((DefaultOAuth2User) userDetails).getAttributes().get("userEmail"));
    		claim.put("nickname", ((DefaultOAuth2User) userDetails).getAttributes().get("userName"));
			//claim.put("", ((DefaultOAuth2User) userDetails).getAttributes().get("userName"));
    		//관련 정보 찾아본 뒤, refresh토큰 재발급과 관련된거로 추가할 것.
    	}

    	//TODO 다른 타입의 사용자 정보의 경우는 나중에 생각해보자.
    	// else if () {}
    	
        /*

         */
    	String secret = env.getProperty("jwt.secret");//현재는 시크릿 키를 굉장히 간단하게 했지만,
		//https://ayoteralab.tistory.com/entry/Spring-Boot-29-Google-OAuth-with-JWT-1?category=860804
		//이 링크 참조해서 복잡한 키를 생성할 수 있도록 할 것.
    	int exp = Integer.valueOf(env.getProperty("jwt.expire-time"));
    	
        claim.put("iat", new Date(System.currentTimeMillis()));//토큰 생성 시간
        claim.put("exp", new Date(System.currentTimeMillis() + (1000 * exp))); // 토큰 만료 시간
        
        return Jwts.builder()
				.setHeaderParam("typ","JWT")
        			  .setClaims(claim)
        			  .signWith(SignatureAlgorithm.HS512, secret)//인코딩 알고리즘.
        			  .compact();//compact()는 최종적인 토큰의 직렬화, string 으로의 변환을 담당한다.
    }
    
    public Map<String,Object> getBobyFromToken(String token){
		String secret = env.getProperty("jwt.secret");//이 시크릿 키가 있어야지 토큰의 값을 정상적으로 디코딩해 읽을 수 있음.
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    
    

}

