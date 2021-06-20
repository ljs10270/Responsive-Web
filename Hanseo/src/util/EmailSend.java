package util;

import javax.mail.Transport; // 회원가입 인증메일을 보내기 위해 import

import javax.mail.Message; // 회원가입 인증메일을 보내기 위해 import
import javax.mail.Address; // 회원가입 인증메일을 보내기 위해 import
import javax.mail.internet.InternetAddress; // 회원가입 인증메일을 보내기 위해 import
import javax.mail.internet.MimeMessage;  // 회원가입 인증메일을 보내기 위해  import
import javax.mail.Session; // 회원가입 인증메일을 보내기 위해  import
import javax.mail.Authenticator; // 회원가입 인증메일을 보내기 위해  import
import java.util.Properties; //구글 SMTP 서버를 이용하기 위해  import 
import util.Gmail;
import util.SHA256;
import user.UserDAO;
import user.UserDTO;

public class EmailSend {

	public boolean EmailSendAction(String userID) {
		UserDAO userDAO = new UserDAO();
		
		//인증이 안된 사용자에게 메일을 보내기 위해 변수 선언
		String host = "http://localhost:8080/Hanseo/"; //홈페이지 주소 
		String from = "ljs10270@gmail.com"; //관리자 구글 아이디
		UserDTO user = new UserDAO().getUser(userID);
		String to = user.getUserEmail(); //이메일 받는사람(사용자) 이메일을 가져옴
		String emailTitle = "한서대학교 족보 공유 커뮤니티 회원가입을 위한 인증 메일입니다";
		String emailContent = "" + new SHA256().getSHA256(to);
					
		//인증 이메일을 보내기 위해 구글 smtp서버를 이용
		Properties p = new Properties();
		p.put("mail.smtp.user", from); //smtp 서버를 이용하는 유저는 관리자다
		p.put("mail.smtp.host", "smtp.googlemail.com"); //구글에서 제공하는 smtp서버
		p.put("mail.smtp.port", "465"); //포트는 465로 구글에서 정해져 있음
		p.put("mail.smtp.starttls.enable", "true"); //tls 사용가능으로 설정
		p.put("mail.smtp.auth", "true"); //smtp 인증 
		p.put("mail.smtp.sdebug", "true"); //디버그
		p.put("mail.smtp.socketFactory.port", "465");
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback", "false");
		
		try { //구글 계정으로 Gmail 인증하고 회원에게 인증메일 보내기
			Authenticator auth = new Gmail();
			Session ses = Session.getInstance(p, auth);
			ses.setDebug(true);
			MimeMessage msg = new MimeMessage(ses); //MimeMessage 객체를 이용하여 실제로 메일을 보낼 수 있게 설정
			msg.setSubject(emailTitle); //메일 제목 설정
			Address fromAddr = new InternetAddress(from); //보내는사람(관리자) 메일 주소 생성
			msg.setFrom(fromAddr); //보내는사람(관리자) 메일 주소 설정
			Address toAddr = new InternetAddress(to); //받는사람(회원) 메일 주소 생성
			msg.addRecipient(Message.RecipientType.TO, toAddr); //받는사람(회원) 메일 주소 설정
			msg.setContent(emailContent, "text/html;charset=UTF-8"); //메일 내용 설정
			Transport.send(msg); //메일 전송
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
