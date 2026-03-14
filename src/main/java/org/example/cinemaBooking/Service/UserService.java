package org.example.cinemaBooking.Service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.example.cinemaBooking.Entity.UserEntity;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.UserMapper;
import org.example.cinemaBooking.Model.Request.ChangePasswordRequest;
import org.example.cinemaBooking.Model.Request.UpdateProfileRequest;
import org.example.cinemaBooking.Model.Response.UserResponse;
import org.example.cinemaBooking.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(
        level = lombok.AccessLevel.PRIVATE,
        makeFinal = true
)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse getMyInfo(){
        var userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository
                .findUserEntityByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        log.info("[USER SERVICE] Get user info for user: {}", userName);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateMyInfo(UpdateProfileRequest request){
        var userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository
                .findUserEntityByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(request.getEmail() != null &&
                userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        userMapper.updateUser(request, user);

        userRepository.save(user);
        log.info("[USER SERVICE] Update user info for user: {}", userName);
        return userMapper.toUserResponse(user);
    }

    public  void changePassword(ChangePasswordRequest request){
        var userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository
                .findUserEntityByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("[USER SERVICE] Change password for user: {}", userName);
    }
}
