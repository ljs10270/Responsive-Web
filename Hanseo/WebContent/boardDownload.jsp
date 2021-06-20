<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "board.BoardDAO"%>
<%@ page import = "board.BoardDTO"%>

<%@ page import = "java.io.*"%>
<%@ page import = "java.text.*"%>
<%@ page import = "java.lang.*"%>
<%@ page import = "java.util.*"%>
<%@ page import = "java.net.*"%>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>한서대학교 족보 공유 커뮤니티</title>
</head>
<body>
	<%
		request.setCharacterEncoding("UTF-8");
		String boardID = request.getParameter("boardID");
		
		if(boardID == null || boardID.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		// 실제 서버의 물리적인 경로 가져오기, 파일은 upload 폴더 만들어서 저장되도록 코딩함
		String root = request.getSession().getServletContext().getRealPath("/");
		String savePath = root + "upload";
		String fileName = "";
		String realFile = "";
		
		BoardDAO boardDAO = new BoardDAO();
		
		// 작성자가 업로드한 파일명 DB에서 가져오고
		fileName = boardDAO.getFile(boardID);
		realFile = boardDAO.getRealFile(boardID); //실제로 업로드 되어 있는 파일명
		
		//파일이 존재하지 않는 경우
		if(fileName.equals("") || realFile.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "첨부파일이 존재하지 않습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		InputStream in = null;
		OutputStream os = null;
		File file = null;
		boolean skip = false; //다운로드 중지
		String client = "";
		
		try {
			try {
				file = new File(savePath, realFile);
				in = new FileInputStream(file);
			} catch(FileNotFoundException e) {
				skip = true;
			}
			client = request.getHeader("User-Agent"); //사용자의 브라우저 환경 가져오기
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Description", "JSP Generated Data"); //내용서술자로 패킷 헤더에 JSP로부터 생성된 데이터라는 것을 헤더에 넣어 인식
			
			if(!skip) { //스킵이 안되어 다운로드가 가능한 경우 브라우저 별 처리
				if(client.indexOf("MSIE") != -1) {
					response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("KSC5601"), "ISO8859_1"));
				} else { //utf-8 인코딩
					fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					response.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
				}
				response.setHeader("Content-Length", "" + file.length()); //클라이언트한테 전송할 파일의 길이를 담음
				
				os = response.getOutputStream();
				byte b[] = new byte[(int)file.length()];
				int leng = 0;
				
				while ((leng = in.read(b)) > 0) {
					os.write(b, 0, leng);
				}
			} else { //데이터를 보낼 수 없는 경우
				response.setContentType("text/html; charset=UTF-8");
				out.println("<script>alert('파일을 찾을 수 없습니다.');history.back();</script>");
			}
			in.close();
			os.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	%>
</body>
</html>