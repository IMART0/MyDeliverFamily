package am.martirosyan.mydeliver.service;

import am.martirosyan.mydeliver.model.User;
import am.martirosyan.mydeliver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isUserRegistered(long chatId) {
        return userRepository.existsById(chatId);
    }

    public void registerUser(long chatId, String name, String phone) {
        User user = new User(
                chatId,
                name,
                phone,
                0
        );
        userRepository.save(user);
    }
}
