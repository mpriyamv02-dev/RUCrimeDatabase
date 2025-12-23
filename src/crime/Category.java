package crime;

public enum Category {
    PROPERTY,
    VIOLENT,
    MISCHIEF,
    TRESPASS,
    OTHER;

    public static Category fromString(String categoryStr) { 
        if (categoryStr.contains("burglary") || categoryStr.contains("theft")) {
            return PROPERTY;
        } 
        else if (categoryStr.contains("harassment") || categoryStr.contains("violence") || 
            categoryStr.contains("assault")) {
            return VIOLENT;
        } 
        else if (categoryStr.contains("mischief")) {
            return MISCHIEF;
        } 
        else if (categoryStr.contains("trespass")) {
            return TRESPASS;
        } else {
            return OTHER;
        }

    }
}