package am.martirosyan.mydeliver.service;

import am.martirosyan.mydeliver.model.User;
import am.martirosyan.mydeliver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isRegistered(long chatId) {
        return userRepository.existsById(chatId);
    }

    public User register(long chatId, String name, String phone, String birthDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            Date date = formatter.parse(birthDate);
            User user = new User(
                    chatId,
                    name,
                    phone,
                    date,
                    0
            );
            return userRepository.save(user);
        } catch (ParseException e) {
            return null;
        }
    }
}
