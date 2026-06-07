package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public final class AvatarUtil {

    private static final String RESOURCE = "resources/ansir.jpg";

    private AvatarUtil() {}

    public static BufferedImage load() {
        try (InputStream is = AvatarUtil.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (is != null) return ImageIO.read(is);
        } catch (Exception ignored) {}

        for (String path : new String[]{"src/resources/ansir.jpg", "resources/ansir.jpg"}) {
            try {
                File file = new File(path);
                if (file.exists()) return ImageIO.read(file);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
