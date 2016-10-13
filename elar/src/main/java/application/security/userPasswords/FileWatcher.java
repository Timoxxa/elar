package application.security.userPasswords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.*;
import java.util.concurrent.Executors;

/**
 * Created by Timofey on 13.10.2016.
 *
 * Наблюдает за файлом с паролями пользователей (путь к файлу указан в конфиге).
 * Работает в отдельном потоке.
 * Если файл изменён - инициализирует повторный процесс загрузки паролей в отдельном потоке.
 */
@Service
class FileWatcher {

    private static final Logger log = LoggerFactory.getLogger(FileWatcher.class);
    @Value("${passwordsFile}")
    private String filePath;
    private Path fileDirectory;
    @Autowired
    PasswordManager passwordManager;

    @PostConstruct
    private void createWatcher() {
        Executors.newSingleThreadExecutor().submit(this::prepareWatcherAndStart);
    }

    private void prepareWatcherAndStart() {
        try {
            File fileFromShortName = new File(filePath);
            File fileFromLongName = new File(fileFromShortName.getAbsolutePath());
            File directory = fileFromLongName.getParentFile();
            fileDirectory = directory.toPath();
            startWatch();
        } catch (Exception e) {
            log.error("Ошибка при подготовке наблюдателя за файлом с паролями. ", e);
        }
    }

    private void startWatch() {
        try {
            WatchService watcher = fileDirectory.getFileSystem().newWatchService();
            fileDirectory.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey watchKey = watcher.take();
            for (WatchEvent event : watchKey.pollEvents()) {
                Path changed = (Path) event.context();
                if (changed.endsWith(filePath)) {
                    log.info("Файл с паролями изменён. " +
                            "Файл: [" + fileDirectory + "\\" + filePath + "]. " +
                            "Новый список паролей будет загружен в память. ");
                    Executors.newSingleThreadExecutor().submit(() -> passwordManager.loadPasswords());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка во время наблюдения за файлом, содержащим пароли. " +
                    "Файл: [" + fileDirectory + "\\" + filePath + "]. ", e);
        }
        startWatch();
    }

}
