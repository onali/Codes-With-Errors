import java.util.Arrays;

class ArrList7{
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5};
        Arrays.sort(numbers, (a, b) -> {  // Syntax error: unexpected token ->
            if (a < b) {
                return -1;
            } else if (a > b) {
                return 1;
            } else {
                return 0;
            }
        });
        for (int num : numbers) {
            System.out.println(num);
        }
    }
}
