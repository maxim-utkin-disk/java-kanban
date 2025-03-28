package utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GlobalSettings {
    public static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String FILE_NAME = "task_list.csv";
    public static final String FILE_NAME_TEST = "task_list_test.csv";
    public static final String SAVE2FILE_HISTORY_PREFIX = "history:";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final int PORT = 8080;
}
