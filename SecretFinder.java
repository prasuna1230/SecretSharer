import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SecretFinder {
    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("test_case.json")));
            JSONObject data = new JSONObject(content);

            JSONObject config = data.getJSONObject("keys");
            int total = config.getInt("n");
            int required = config.getInt("k");

            List<Node> values = new ArrayList<>();
            for (String id : data.keySet()) {
                if (!id.equals("keys")) {
                    JSONObject entry = data.getJSONObject(id);
                    int xVal = Integer.parseInt(id);
                    String base = entry.getString("base");
                    String val = entry.getString("value");
                    BigInteger yVal = convertBase(val, base);
                    values.add(new Node(xVal, yVal));
                }
            }

            List<Node> subset = values.subList(0, required);
            BigInteger result = interpolate(subset);
            System.out.println("The secret is: " + result);

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Processing Error: " + e.getMessage());
        }
    }

    private static BigInteger convertBase(String val, String base) {
        return new BigInteger(val, Integer.parseInt(base));
    }

    private static BigInteger interpolate(List<Node> inputs) {
        BigInteger sum = BigInteger.ZERO;
        int size = inputs.size();

        for (int i = 0; i < size; i++) {
            BigInteger xi = BigInteger.valueOf(inputs.get(i).x);
            BigInteger yi = inputs.get(i).y;
            BigInteger top = BigInteger.ONE;
            BigInteger bottom = BigInteger.ONE;

            for (int j = 0; j < size; j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(inputs.get(j).x);
                    top = top.multiply(xj.negate());
                    bottom = bottom.multiply(xi.subtract(xj));
                }
            }

            BigInteger part = yi.multiply(top).divide(bottom);
            sum = sum.add(part);
        }

        return sum;
    }

    static class Node {
        int x;
        BigInteger y;

        Node(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
