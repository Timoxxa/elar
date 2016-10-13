package application.security.userPasswords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Timofey on 12.10.2016.
 *
 * Загружает пароли пользователей в память из файла, указанного в конфиге
 */
@Service
class PasswordLoader {

    private static final Logger log = LoggerFactory.getLogger(PasswordLoader.class);
    @Autowired
    private Pattern pattern;
    @Value("${passwordsFile}")
    private String filePath;

    Map<String,String> loadPasswordsFromFile() {
        Map<String,String> passwords = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    Map.Entry<String,String> userAndPassword = parseLine(line);
                    passwords.put(userAndPassword.getKey(), userAndPassword.getValue());
                } catch (Exception e) {
                    log.warn("Ошибка при парсинге строки из файла с паролями пользователей. " +
                            "Расположение файла: [" + new File(filePath).getAbsolutePath() + "]. " +
                            "Строка: [" + line + "]. ", e);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при чтении файла с паролями пользователей. " +
                    "Расположение файла: [" + new File(filePath).getAbsolutePath() + "]. ", e);
        }
        return passwords;
    }

    private Map.Entry<String,String> parseLine(String line) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            throw new RuntimeException("Строка не соответствует регулярному выражению. " +
                    "Регулярное выражение: " + pattern.pattern());
        }
        String stringId = matcher.group(1);
//        Long id = Long.valueOf(stringId);
        String password = matcher.group(2);
        return new AbstractMap.SimpleEntry<>(stringId, password);
    }

}
