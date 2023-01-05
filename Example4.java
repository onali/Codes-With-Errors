import java.util.List;

class Example4 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();  // ArrayList cannot be resolved to a type
        list.add("Hello");
        list.add("World");
        for (String str : list) {
            System.out.println(str);
        }
    }
}
