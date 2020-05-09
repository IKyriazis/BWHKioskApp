package edu.wpi.cs3733.d20.teamA;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.util.impl.ServiceFactory;
import java.io.File;
import java.util.Optional;

public class Main {

  public static void main(String[] args) {
    // Uncomment if screen is messed up
    // System.setProperty("prism.allowhidpi", "false");

    // Gluon Maps setup
    System.setProperty("javafx.platform", "Desktop");
    System.setProperty("http.agent", "Gluon Mobile/1.0.3  /" + System.getProperty("http.agent"));

    StorageService storageService =
        new StorageService() {
          @Override
          public Optional<File> getPrivateStorage() {
            // user home app config location (linux: /home/[yourname]/.gluonmaps)
            return Optional.of(new File(System.getProperty("user.home")));
          }

          @Override
          public Optional<File> getPublicStorage(String subdirectory) {
            // this should work on desktop systems because home path is public
            return getPrivateStorage();
          }

          @Override
          public boolean isExternalStorageWritable() {
            //noinspection ConstantConditions
            return getPrivateStorage().get().canWrite();
          }

          @Override
          public boolean isExternalStorageReadable() {
            //noinspection ConstantConditions
            return getPrivateStorage().get().canRead();
          }
        };

    // define service factory for desktop
    ServiceFactory<StorageService> storageServiceFactory =
        new ServiceFactory<StorageService>() {

          @Override
          public Class<StorageService> getServiceType() {
            return StorageService.class;
          }

          @Override
          public Optional<StorageService> getInstance() {
            return Optional.of(storageService);
          }
        };
    // register service
    Services.registerServiceFactory(storageServiceFactory);

    App.launch(App.class, args);
  }
}
