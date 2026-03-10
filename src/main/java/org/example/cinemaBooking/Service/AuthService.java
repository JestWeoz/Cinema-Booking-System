package org.example.cinemaBooking.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Entity.UserEntity;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Model.Request.IntrospectReq;
import org.example.cinemaBooking.Model.Request.LoginRequest;
import org.example.cinemaBooking.Model.Request.RegisterRequest;
import org.example.cinemaBooking.Model.Response.LoginResponse;
import org.example.cinemaBooking.Model.Response.RegisterResponse;
import org.example.cinemaBooking.Model.Response.UserInfoResponse;
import org.example.cinemaBooking.Repository.UserRepository;
import org.example.cinemaBooking.Shared.response.IntrospectResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.StringJoiner;

@Service
@Slf4j
@RequiredArgsConstructor

public class AuthService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepo;
    @Value("${jwt.SignerKey}")
    String signerKey;
    private final PasswordEncoder passwordEncoder;

    // Đăng kí tài khoản
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        UserEntity userEntity;
        userEntity = modelMapper.map(registerRequest, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepo.save(userEntity);
        return RegisterResponse
                .builder()
                .userInfoResponse(modelMapper.map(userEntity, UserInfoResponse.class))
                .token(generateToken(userEntity))
                .build();
    }
    // Đăng nhập
    public LoginResponse loginUser(LoginRequest loginRequest) {
        UserEntity userEntity = userRepo.findUserEntityByUsername(loginRequest.getUsername());
        if (userEntity == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        UserInfoResponse userInfoResponse = modelMapper.map(userEntity, UserInfoResponse.class);
        return LoginResponse.builder()
                .success(true)
                .token(generateToken(userEntity))
                .userInfoResponse(userInfoResponse)
                .build();
    }
    private byte[] getSignerKey() {
        return Base64.getDecoder().decode(signerKey);
    }
    public IntrospectResponse introspect(IntrospectReq introspectReq)
            throws JOSEException, ParseException {

        String token = introspectReq.getToken();

        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(getSignerKey());

        boolean verified = signedJWT.verify(verifier);

        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        return IntrospectResponse.builder()
                .valid(verified && expiration.after(new Date()))
                .build();
    }


    String generateToken(UserEntity user) {

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Cinema Booking Service")
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plus(1, ChronoUnit.HOURS)))
                .claim("scope", buildScope(user))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS512),
                claims
        );

        try {
            signedJWT.sign(new MACSigner(getSignerKey()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private  String  buildScope(UserEntity user) {
        StringJoiner scopeJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                scopeJoiner.add("ROLE_" + role.getName());
            });
        }
        return scopeJoiner.toString();
    }

}
