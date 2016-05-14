package no.hyper.memoryorm;

import java.lang.reflect.Field;

/**
 * Created by Jean on 5/12/2016.
 */
class SQLiteRequestHelper {

    private static String getSQLPropertyType(Field property) {
        switch (property.getGenericType().toString()) {
            case "boolean" :
            case "int": return "INTEGER";
            default: return "TEXT";
        }
    }

    public static String testTableExistence(String table) {
        return "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "';";
    }

    public static <T> String createTable(Class<T> entityType, boolean autoIncrement) {
        StringBuilder sb = new StringBuilder();
        for(Field field : entityType.getDeclaredFields()) {
            String fieldName = field.getName();
            if (fieldName.equals("id")){
                String meta = getSQLPropertyType(field) +"PRIMARY KEY,";
                if (autoIncrement) {
                    meta = "INTEGER PRIMARY KEY AUTOINCREMENT,";
                }
                sb.append(fieldName + " " + meta);
            } else if (!fieldName.startsWith("$")) {
                sb.append(fieldName + " " + getSQLPropertyType(field) + ",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return "CREATE TABLE IF NOT EXISTS " + entityType.getSimpleName() + "(" + sb.toString() + ");";
    }

    public static String deleteTable(String name) {
        return "DROP TABLE IF EXISTS " + name + ";";
    }

    public static String fetchAll(String name) {
        return "SELECT * FROM " + name + ";";
    }
}
