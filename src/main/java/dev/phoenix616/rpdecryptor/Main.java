package dev.phoenix616.rpdecryptor;

/*
 * ResourcePackDecryptor - ResourcePackDecryptor
 * Copyright (C) 2024 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.ZipFile;

public class Main {

    private static String NAME = null;
    private static String VERSION = null;
    private static Properties p = new Properties();

    public static void main(String[] args) {
        try {
            InputStream s = Main.class.getClassLoader().getResourceAsStream("app.properties");
            p.load(s);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        NAME = p.getProperty("application.name");
        VERSION = p.getProperty("application.version");

        System.out.print(
                NAME + " " + VERSION
                        + "\n"
                        + "Copyright (C) 2024 Max Lee aka Phoenix616 (max@themoep.de)\n"
                        + "    By using this program you agree to the terms of the AGPLv3\n"
                        + "    The full license text can be found here: https://phoenix616.dev/licenses/agpl-v3.txt\n"
                        + "    This program's source is available here: https://github.com/Phoenix616/ResourcePackDecryptor\n"
        );

        if (args.length == 0) {
            System.out.println("Usage: <inputfilze> [<outputfolder>]");
            return;
        }

        File input = new File(args[0]);
        if (input.isDirectory()) {
            System.out.println("Input is a directory!");
            return;
        }

        String outputName = input.getName().substring(0, input.getName().lastIndexOf('.'));
        if (args.length > 1) {
            outputName = args[1];
        }

        File output = new File(outputName);
        if (output.exists()) {
            if (!output.isDirectory()) {
                System.out.println("Output is not a directory!");
                return;
            }
        } else {
            output.mkdirs();
        }

        try (ZipFile zip = new ZipFile(input)) {
            zip.stream().forEach(entry -> {
                try {
                    Path path = Paths.get(output.getAbsolutePath(), entry.getName());
                    Files.createDirectories(path.getParent());
                    Files.copy(zip.getInputStream(entry), path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}