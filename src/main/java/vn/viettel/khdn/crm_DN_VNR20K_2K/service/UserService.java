package vn.viettel.khdn.crm_DN_VNR20K_2K.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.User;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqUserCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqUserUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResUserDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User handleGetUserByUsername(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }

    public ResUserDTO createUser(ReqUserCreateDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRole(dto.getRole() != null ? dto.getRole()
                : vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum.CONSULTANT);
        user.setRegion(dto.getRegion());
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);
        return convertToResUserDTO(saved);
    }

    @Transactional(readOnly = true)
    public ResUserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + id));
        return convertToResUserDTO(user);
    }

    @Transactional(readOnly = true)
    public ResUserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + email));
        return convertToResUserDTO(user);
    }

    @Transactional(readOnly = true)
    public java.util.List<ResUserDTO> findAll() {
        return userRepository.findAll().stream().map(this::convertToResUserDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ResUserDTO> searchUsers(
            vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum role, String keyword,
            org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<User> page =
                userRepository.searchUsers(role, keyword, pageable);
        return page.map(this::convertToResUserDTO);
    }

    public ResUserDTO updateUser(Long id, ReqUserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + id));

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getRegion() != null) {
            user.setRegion(dto.getRegion());
        }

        User saved = userRepository.save(user);
        return convertToResUserDTO(saved);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + id));
        userRepository.delete(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + userId));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO dto = new ResUserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
