public class main {
public static void main(String[] args) {
    IdandPasswords idandPasswords = new IdandPasswords();
    
Login loginPage = new Login(idandPasswords.getloginInfo());
}
}