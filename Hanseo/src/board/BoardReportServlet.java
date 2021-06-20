package board;

import java.io.IOException;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.Gmail;

@WebServlet("/BoardReportServlet")
public class BoardReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String rTitle = request.getParameter("rTitle");
		String rContent = request.getParameter("rContent");
		
		if(userID == null || userID.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "정상적인 접근이 아닙니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(rTitle == null || rTitle.equals("") || rTitle.trim().equals("") || rContent == null || rContent.equals("") || rContent.trim().equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "입력이 안된 사항이 있습니다.");
			response.sendRedirect("boardView.jsp");
			return;
		}
		
		//관리자에게 신고 메일을 보내기 위해 변수 선언
		String host = "http://localhost:8080/Hanseo/"; //홈페이지 주소
		String from = "ljs10270@gmail.com"; //관리자 구글 아이디
		String to = "ljs1027s@naver.com"; //신고 내용을 받을 관리자 이메일
		String emailTitle = "한서대 족보 공유 사이트에서 접수된 신고 메일입니다.";
		String emailContent = "신고자: " + userID +
							  "<br>제목: " + rTitle +
							  "<br>내용: " + rContent;
					
		//관리자에게 신고 메일을 보내기 위해 구글 smtp서버를 이용
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
		
		try { //구글 계정으로 Gmail 인증하고 관리자에게 신고 메일 보내기
			Authenticator auth = new Gmail();
			Session ses = Session.getInstance(p, auth);
			ses.setDebug(true);
			MimeMessage msg = new MimeMessage(ses); //MimeMessage 객체를 이용하여 실제로 메일을 보낼 수 있게 설정
			msg.setSubject(emailTitle); //메일 제목 설정
			Address fromAddr = new InternetAddress(from); //보내는사람(관리자) 구글 메일 주소 생성
			msg.setFrom(fromAddr); //보내는사람(관리자) 구글 메일 주소 설정
			Address toAddr = new InternetAddress(to); //신고 내용을 받을 관리자 메일 주소 생성
			msg.addRecipient(Message.RecipientType.TO, toAddr); //신고 내용을 받을 관리자 메일 주소 설정
			msg.setContent(emailContent, "text/html;charset=UTF-8"); //메일 내용 설정
			Transport.send(msg); //메일 전송
			
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		request.getSession().setAttribute("messageType", "성공 메시지");
		request.getSession().setAttribute("messageContent", "관리자에게 신고 내용이 접수 되었습니다.");
		response.sendRedirect("boardView.jsp");
		return;
	}

}
