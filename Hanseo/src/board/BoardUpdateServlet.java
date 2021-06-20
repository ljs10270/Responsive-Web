package board;

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

@WebServlet("/BoardUpdateServlet")
public class BoardUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		MultipartRequest multi = null;
		
		int fileMaxSize = 10 * 1024 * 1024;
		
		// 업로드 경로를 WAS의 /upload 폴더로 경로 설정, WAS마다 경로 다를 수 있음
		String savePath = request.getRealPath("/upload").replaceAll("\\\\", "/");
		
		try {
			// 클라이언트로 부터 받은 파일을 위에 지정한 경로, 파일 최대 크기만큼 UTF-8로 인코딩하여  파일 이름이 겹치거나 하는 등 오류를 처리해주는 DefaultFileRenamePolicy() 적용 하여 서버에 업로드
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
	
		} catch (Exception e) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "파일 크기는 10MB를 넘을 수 없습니다.");
			response.sendRedirect("boardUpdate.jsp");
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
		
		String boardID = multi.getParameter("boardID");
		if(boardID == null || boardID.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO board = boardDAO.getBoard(boardID);
		if(!userID.equals(board.getUserID())) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "작성자 본인 이외에 접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		String boardTitle = multi.getParameter("boardTitle");
		String boardContent = multi.getParameter("boardContent");
		String lectureType = multi.getParameter("lectureType");
		
		if(boardTitle == null || boardTitle.equals("") || boardTitle.trim().equals("") || boardContent == null || boardContent.equals("") || boardContent.trim().equals("")
				|| lectureType == null || lectureType.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "빈 칸 없이 작성해 주세요.");
			response.sendRedirect("boardUpdate.jsp?boardID=" + boardID);
			return;
		}
		
		String boardFile = "";
		String boardRealFile = "";
		
		File file = multi.getFile("boardFile");
		if(file != null) { //게시물 수정시 새롭게 첨부 파일이 업로드 된 경우
			boardFile = multi.getOriginalFileName("boardFile");
			boardRealFile = file.getName(); //프로필 이미지 파일과 중복이 되면 upload 폴더 인식 못함 즉, 중복 보안을 위해 리얼파일로 옮기면서 파일명 달라짐
			
			// 기존 첨부파일 제거
			String prev = boardDAO.getRealFile(boardID);
			File prevFile = new File(savePath + "/" + prev);
			if(prevFile.exists()) {
				prevFile.delete();
			}
		} else { //게시물 수정시 첨부파일 업로드를 하지 않았다면
			//기존에 DB에 등록된 첨부파일 그대로 사용
			boardFile = boardDAO.getFile(boardID);
			boardRealFile = boardDAO.getRealFile(boardID);
		}
		
		boardDAO.update(boardID, boardTitle, boardContent, boardFile, boardRealFile, lectureType);
		session.setAttribute("messageType", "성공 메시지");
		session.setAttribute("messageContent", "게시물이 수정 되었습니다.");
		response.sendRedirect("boardShow.jsp?boardID=" + boardID);
		return;
	}

}
