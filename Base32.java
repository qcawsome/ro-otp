public class Base32 {

    private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int[] BASE32_LOOKUP = new int[128];

    static {
        for (int i = 0; i < BASE32_LOOKUP.length; i++) BASE32_LOOKUP[i] = -1;
        for (int i = 0; i < BASE32_ALPHABET.length(); i++) {
            BASE32_LOOKUP[BASE32_ALPHABET.charAt(i)] = i;
        }
    }

    public byte[] decode(String base32) {
        // Remove any padding characters and convert to uppercase for standardization
        base32 = base32.toUpperCase().replace("=", "");

        int outputLength = base32.length() * 5 / 8;
        byte[] result = new byte[outputLength];
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;

        for (char c : base32.toCharArray()) {
            int val = BASE32_LOOKUP[c];
            if (val == -1) {
                throw new IllegalArgumentException("Invalid character in Base32 string: " + c);
            }

            buffer = (buffer << 5) | val;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return result;
    }
}