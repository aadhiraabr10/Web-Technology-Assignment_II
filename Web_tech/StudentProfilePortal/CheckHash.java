import org.mindrot.jbcrypt.BCrypt;
public class CheckHash {
  public static void main(String[] args) {
    System.out.println(BCrypt.hashpw("admin123", BCrypt.gensalt(10)));
    System.out.println(BCrypt.hashpw("teach456", BCrypt.gensalt(10)));
  }
}
