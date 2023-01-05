class Example2 {
    public static void main(String[] args) {
        int[] numbers = new int[5];
        for (int i = 0; i <= numbers.length; i++) {  // Array index out of bounds
            System.out.println(numbers[i]);
        }
    }
}