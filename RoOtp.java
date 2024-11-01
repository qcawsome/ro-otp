import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.security.InvalidKeyException;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RoOtp {
    
    public static void main(String[] args) {
        // Check if there's an argument passed
        if (args.length == 0) {
            System.out.println("Please provide a string argument to type.");
            return;
        }

        String secretKey = args[0];
        try {
            // Generate TOTP with 6 digits
            final String inputString = generateTOTP(secretKey);
            Robot robot = new Robot();
            typeString(robot, inputString);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void typeString(Robot robot, String text) {
        for (char c : text.toCharArray()) {
            typeCharacter(robot, c);
            robot.delay(5); // Delay between keystrokes (in milliseconds)
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public static void typeCharacter(Robot robot, char c) {
        try {
            if (Character.isUpperCase(c)) {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
                robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
                robot.keyRelease(KeyEvent.VK_SHIFT);
            } else {
                robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
                robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Cannot type character: " + c);
        }
    }

    private static String generateTOTP(String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Time window in seconds (usually 30)
            int timeWindow = 30;
            long currentTime = Calendar.getInstance().getTimeInMillis() / 1000;
            long timeCounter = currentTime / timeWindow;

            // Decode the secret key
            Base32 base32 = new Base32();
            byte[] decodedKey = base32.decode(secretKey);

            SecretKeySpec signKey = new SecretKeySpec(decodedKey, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(longToBytes(timeCounter));

            // Extract OTP from hash
            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24) |
                    ((hash[offset + 1] & 0xFF) << 16) |
                    ((hash[offset + 2] & 0xFF) << 8) |
                    (hash[offset + 3] & 0xFF);

            // Generate OTP
            int otp = binary % 1000000;
            return String.format("%06d", otp); // Zero-padded to 6 digits
        } catch (Exception e) {
            return "Error generating OTP";
        }
    }

    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return buffer.array();
    }
}