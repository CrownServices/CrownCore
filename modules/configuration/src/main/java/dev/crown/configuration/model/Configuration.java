package dev.crown.configuration.model;

import dev.crown.configuration.annotation.ConfigComment;
import dev.crown.configuration.annotation.ConfigKey;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Configuration {

    public static <T> T load(File file, Class<T> configurationClass) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(configurationClass, "configurationClass");
        if (!file.exists()) return null;
        try (InputStream input = new FileInputStream(file)) {
            LoaderOptions options = new LoaderOptions();
            options.setProcessComments(true);
            options.setWarnOnDuplicateKeys(true);
            Yaml yaml = new Yaml(new Constructor(configurationClass, options));
            return yaml.loadAs(input, configurationClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from file: " + file, e);
        }
    }

    public static <T> T loadFromResource(String resource, Class<T> configurationClass) {
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(configurationClass, "configurationClass");
        InputStream input = configurationClass.getClassLoader().getResourceAsStream(resource);
        if (input == null) {
            throw new IllegalArgumentException("Resource not found: " + resource);
        }
        try (input) {
            LoaderOptions options = new LoaderOptions();
            options.setProcessComments(true);
            options.setWarnOnDuplicateKeys(true);
            Yaml yaml = new Yaml(new Constructor(configurationClass, options));
            return yaml.loadAs(input, configurationClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from resource: " + resource, e);
        }
    }

    public static void save(File file, Object configuration) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(configuration, "configuration");

        // Backup anlegen
        if (file.exists()) {
            try {
                Files.copy(file.toPath(), file.toPath().resolveSibling(file.getName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Warnung: Backup konnte nicht erstellt werden: " + e.getMessage());
            }
        }

        try (Writer writer = new FileWriter(file)) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setIndent(4);
            options.setPrettyFlow(true);

            Representer representer = new Representer(options) {
                private Map<String, Object> representJavaBeanProperties(Object javaBean) {
                    Map<String, Object> values = new LinkedHashMap<>();
                    Class<?> beanClass = javaBean.getClass();
                    for (Field field : beanClass.getDeclaredFields()) {
                        field.setAccessible(true);
                        String key = field.getName();
                        if (field.isAnnotationPresent(ConfigKey.class)) {
                            key = field.getAnnotation(ConfigKey.class).value();
                        }
                        Object value;
                        try {
                            value = field.get(javaBean);
                        } catch (IllegalAccessException e) {
                            continue;
                        }
                        values.put(key, value);
                    }
                    return values;
                }
            };

            String yamlString = toYamlWithComments(configuration, representer, options);
            writer.write(yamlString);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration to file: " + file, e);
        }
    }

    private static String toYamlWithComments(Object configuration, Representer representer, DumperOptions options) {
        StringWriter tempWriter = new StringWriter();
        Yaml yaml = new Yaml(representer, options);
        Map<String, String[]> comments = new LinkedHashMap<>();
        Map<String, Object> values = new LinkedHashMap<>();
        Class<?> beanClass = configuration.getClass();

        for (Field field : beanClass.getDeclaredFields()) {
            field.setAccessible(true);
            String key = field.getName();
            if (field.isAnnotationPresent(ConfigKey.class)) {
                key = field.getAnnotation(ConfigKey.class).value();
            }
            if (field.isAnnotationPresent(ConfigComment.class)) {
                comments.put(key, field.getAnnotation(ConfigComment.class).value());
            }
            Object value;
            try {
                value = field.get(configuration);
            } catch (IllegalAccessException e) {
                continue;
            }
            values.put(key, value);
        }

        yaml.dump(values, tempWriter);
        String[] lines = tempWriter.toString().split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains(":")) {
                String key = trimmed.split(":", 2)[0].replace("\"", "");
                if (comments.containsKey(key)) {
                    for (String comment : comments.get(key)) {
                        result.append("# ").append(comment).append("\n");
                    }
                }
            }
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
