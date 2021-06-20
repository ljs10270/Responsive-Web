package user;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		MultipartRequest multi = null;
		
		int fileMaxSize = 10 * 1024 * 1024;
		
		// 업로드 경로를 WAS의 /upload 폴더 만들어 경로 설정, WAS마다 경로 다를 수 있음
		String savePath = request.getRealPath("/upload").replaceAll("\\\\", "/");
		
		try {
			// 클라이언트로 부터 받은 파일을 위에 지정한 경로, 파일 최대 크기만큼 UTF-8로 인코딩하여  파일 이름이 겹치거나 하는 등 오류를 처리해주는 DefaultFileRenamePolicy() 적용 하여 서버에 업로드
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
	
		} catch (Exception e) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "파일 크기는 10MB를 넘을 수 없습니다.");
			response.sendRedirect("profileUpdate.jsp");
			return;
		}
		String userID = multi.getParameter("userID");
		
		HttpSession session = request.getSession();
		if(!userID.equals((String) session.getAttribute("userID"))) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "본인을 제외하고 접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		String fileName = "";
		File file = multi.getFile("userProfile");
		if(file != null) {
			// 파일의 확장자 확인
			String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			
			if(ext.equals("jpg") || ext.equals("png") || ext.equals("gif") || ext.equals("JPG") || ext.equals("PNG") || ext.equals("GIT")) {
				String prev = new UserDAO().getUser(userID).getUserProfile();
				File prevFile = new File(savePath + "/" + prev);
				
				//기존 프로필 파일이 존재하면 지워줌
				if(prevFile.exists()) {
					prevFile.delete();
				}
				fileName = file.getName(); //파일 이름 지정
			}
			else {
				//잘못 업로드 한 프로필 파일 지워줌
				if(file.exists()) {
					file.delete();
				}
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "jpg, png, gif 형식의 이미지 파일만 업로드 가능 합니다.");
				response.sendRedirect("profileUpdate.jsp");
				return;
			}
		}
		new UserDAO().profile(userID, fileName); //프로필 파일 업데이트
		session.setAttribute("messageType", "성공 메시지");
		session.setAttribute("messageContent", "프로필이 변경 되었습니다.");
		response.sendRedirect("profileUpdate.jsp");
		return;
	}

}
