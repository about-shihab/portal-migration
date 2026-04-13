package com.iict.buet.customer_portal.jwtconfig;

import com.iict.buet.customer_portal.dto.UserPrincipal;
import com.iict.buet.customer_portal.util.DateUtils;
import com.iict.buet.customer_portal.util.IpService;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class.getName());

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.expire.hours}")
    private Long expireHours;
    private final IpService ipService;

    public JwtTokenProvider(IpService ipService) {
        this.ipService = ipService;
    }

    public String generateToken(Authentication authentication, HttpServletRequest request) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        /*Calendar date = Calendar.getInstance();
        long timeInMillis= date.getTimeInMillis();
        Date expiryDate=new Date(timeInMillis + (10 * jwtExpirationInMs));*/

        return Jwts.builder().setId(UUID.randomUUID().toString())
                .claim("customerCode", userPrincipal.getCustomerCode())
                .claim("username", userPrincipal.getUsername())
                .claim("ip", ipService.getClientIpAddress(request))
                .setSubject(String.valueOf(userPrincipal.getId()))
                .setIssuedAt(now).setExpiration(DateUtils.getExpirationTime(expireHours))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private String getUserIp(String token){
        return (String) getClaims(token).get("ip");
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromJWT(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public boolean validateToken(String authToken, HttpServletRequest request) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            if(ipService.getClientIpAddress(request) ==null){
                return false;
            }

            //Will be enable in live
//            if(ipService.getClientIpAddress(request).equals("0:0:0:0:0:0:0:1")){
//                return false;
//            }

            if(!getUserIp(authToken).equals(ipService.getClientIpAddress(request))){
                return false;
            }
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}