import java.util.Map;

class Example5 {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();  // HashMap cannot be resolved to a type
        map.put("Hello", "World");
        System.out.println(map.get("Hello"));
    }
}
