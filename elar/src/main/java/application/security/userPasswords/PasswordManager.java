package application.security.userPasswords;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by Timofey on 12.10.2016.
 *
 * Хранит данные из файла с паролями (путь указан в конфиге) в виде карты: ключ - имя пользователя, значение - пароль.
 */
@Service
public class PasswordManager {

    private static final Logger log = LoggerFactory.getLogger(PasswordManager.class);
    @Autowired
    private PasswordLoader passwordLoader;
    private Map<String,String> passwordsCache;

    @PostConstruct
    public synchronized void loadPasswords() {
        passwordsCache = passwordLoader.loadPasswordsFromFile();
    }

    public synchronized boolean isUserExist(String username) {
        return passwordsCache.containsKey(username);
    }

    public synchronized boolean isPasswordCorrect(String username, String password) {
        try {
            String passwordHash = DigestUtils.md5Hex(password);
            return passwordHash.equals(passwordsCache.get(username));
        } catch (Exception e) {
            log.error("Ошибка при проверке пароля. ", e);
            return false;
        }
    }

}
