package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

public final class AvatarUtil {

    private static final Map<String, String> USER_AVATARS = Map.of(
            "ansir",  "resources/ansir.jpg",
            "fatima", "resources/fatima.jpg",
            "bilal",  "resources/bilal.jpg"
    );

    private AvatarUtil() {}

    public static BufferedImage loadBankLogo() {
        return loadResource("resources/bank_logo.png");
    }

    public static BufferedImage loadForUser(String username) {
        if (username == null) return null;
        String resource = USER_AVATARS.get(username.toLowerCase());
        return resource != null ? loadResource(resource) : null;
    }

    private static BufferedImage loadResource(String resource) {
        try (InputStream is = AvatarUtil.class.getClassLoader().getResourceAsStream(resource)) {
            if (is != null) return ImageIO.read(is);
        } catch (Exception ignored) {}

        String fileName = resource.substring(resource.lastIndexOf('/') + 1);
        for (String base : new String[]{"src/resources/", "resources/"}) {
            try {
                File file = new File(base + fileName);
                if (file.exists()) return ImageIO.read(file);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
