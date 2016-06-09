package no.hyper.memoryorm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 01.06.2016.
 */
public class ObjectHelper {

    private static String THIS = "this";

    /**
     * return the list of fields declared in the class without the `this` implicit field
     */
    public static List<Field> getDeclaredFields(Class classType) {
        Field[] all = classType.getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        for(int i = 0; i < all.length; i++) {
            if (all[i].getName().contains(THIS)) continue;
            fields.add(all[i]);
        }
        return fields;
    }

    /**
     * Return true or false if the field pass as parameter is a custom type
     */
    public static <T> boolean isCustomType(Class<T> classType) {
        try {
            if (classType.isPrimitive()) return false;
            Class.forName("java.lang." + classType.getSimpleName());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * return true if the field type is List, else false
     */
    public static boolean isAList(Field field) {
        return field.getType().getSimpleName().equals(List.class.getSimpleName());
    }

    /**
     * return the equivalent sql type of a java type
     * @return boolean, int and custom type return INTEGER. Everything else return TEXT
     */
    public static <T> String getEquivalentSqlType(Class<T> classType) {
        if (isCustomType(classType)) return "INTEGER";
        switch (classType.getSimpleName()) {
            case "boolean" :
            case "int": return "INTEGER";
            default : return "TEXT";
        }
    }

    /**
     * Return the the sql request part describing the columns of a table. To use with sql request acting on database
     * tables like CREATE.
     * <p>If one field is named "id", the key word "PRIMARY KEY" will be automatically added to the sql description of
     * the column</p>
     * @param fields: The fields used to create the columns
     * @param foreignKey: If not null, this value will add a foreign key value in the description
     * @return A sql string describing every columns needed in a table
     */
    public static String getEquivalentSqlContent(List<Field> fields, String foreignKey) {
        StringBuilder sb = new StringBuilder();
        for(Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.equals("id")){
                String meta = getEquivalentSqlType(field.getType()) +" PRIMARY KEY,";
                sb.append(fieldName + " " + meta);
            } else {
                sb.append(fieldName + " " + getEquivalentSqlType(field.getType()) + ",");
            }
        }
        if (foreignKey != null) {
            sb.append("rowId_" + foreignKey + " INTEGER");
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * return the class type of object contained by the list
     */
    public static <T> Class<T> getActualListType(Field list) {
        ParameterizedType listType = (ParameterizedType) list.getGenericType();
        return (Class<T>) listType.getActualTypeArguments()[0];
    }

}
