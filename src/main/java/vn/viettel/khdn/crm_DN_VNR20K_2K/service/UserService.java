package vn.viettel.khdn.crm_DN_VNR20K_2K.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.User;
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

    public User createUser(User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new EntityExistsException("Email đã tồn tại: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại: " + email));
    }

    @Transactional(readOnly = true)
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<User> searchUsers(vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum role, String keyword,
            org.springframework.data.domain.Pageable pageable) {
        return userRepository.searchUsers(role, keyword, pageable);
    }

    public User updateUser(Long id, User updateData) {
        User user = getUserById(id);
        if (updateData.getFullName() != null) {
            user.setFullName(updateData.getFullName());
        }
        if (updateData.getPhone() != null) {
            user.setPhone(updateData.getPhone());
        }
        if (updateData.getGender() != null) {
            user.setGender(updateData.getGender());
        }
        if (updateData.getDateOfBirth() != null) {
            user.setDateOfBirth(updateData.getDateOfBirth());
        }

        if (updateData.getStatus() != null) {
            user.setStatus(updateData.getStatus());
        }
        if (updateData.getRole() != null) {
            user.setRole(updateData.getRole());
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
