class Example6 {
    public static void main(String[] args) {
        int x = 10;
        int y = 20;
        if (x > y) {  // Unreachable code
            System.out.println("x is greater than y");
        } else {
            System.out.println("x is not greater than y");
        }
    }
}
