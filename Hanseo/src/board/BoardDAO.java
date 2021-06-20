package board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import util.DatabaseUtil;

public class BoardDAO {
	
	public int write(String userID, String boardTitle, String boardContent, String boardFile, String boardRealFile, String lectureType) { //게시글 등록
		String SQL = "INSERT INTO BOARD SELECT ?, IFNULL((SELECT MAX(boardID) + 1 FROM BOARD), 1), ?, ?, now(), 0, ?, ?, ?, 0";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, userID); //위의 쿼리문 첫번쨰 ? 대입
			pstmt.setString(2, boardTitle); //위의 쿼리문 두번쨰 ? 대입
			pstmt.setString(3, boardContent);
			pstmt.setString(4, boardFile);
			pstmt.setString(5, boardRealFile);
			pstmt.setString(6, lectureType);
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			// 1이 리턴되면 회원가입이 성공한 것이다.
			
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //데이터베이스 오류
	}
	
	public BoardDTO getBoard(String boardID) {
		BoardDTO board = new BoardDTO();
		String SQL = "SELECT * FROM BOARD WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; //특정한 SQL문을 실행한 이후에 나온 결과값을 처리
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, boardID); //사용자가 입력한 ID값을 위의 쿼리문의 첫번째 ?에 넣음
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				board.setUserID(rs.getString("userID"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardContent(rs.getString("boardContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				
				int boardTime = Integer.parseInt(rs.getString("boardDate").substring(11, 13));
				
				String timeType = "오전";
				if(boardTime >= 12) {
					timeType = "오후";
					boardTime -= 12;
				}
				
				board.setBoardDate(rs.getString("boardDate").substring(0,11) 
						+ " " + timeType + " " + boardTime + ":"
						+ rs.getString("boardDate").substring(14, 16) + "");
				
				board.setBoardHit(rs.getInt("boardHit"));
				board.setBoardFile(rs.getString("boardFile"));
				board.setBoardRealFile(rs.getString("boardRealFile"));
				board.setLectureType(rs.getString("lectureType"));
				board.setLikeCount(rs.getInt("likeCount"));
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return board;
	}
	
	public ArrayList<BoardDTO> getList(String lectureType, String searchType, String search, int pageNumber) {
		
		if(lectureType.equals("전체")) {
			lectureType = ""; //사용자가 전체로 검색을 했다면 공백으로 설정, 밑의 쿼리문상 공백은 concat 전체임
		}
		
		ArrayList<BoardDTO> boardList = null;
		String SQL = "";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; //특정한 SQL문을 실행한 이후에 나온 결과값을 처리
		
		try {
			if(searchType.equals("최신순")) {
				SQL = "SELECT * FROM BOARD WHERE lectureType LIKE ? AND CONCAT(boardTitle, boardContent) LIKE "
						+ "? ORDER BY boardID DESC LIMIT " + pageNumber * 10 + ", " + pageNumber * 10 + 11;

			} else if(searchType.equals("추천순")) {
				SQL = "SELECT * FROM BOARD WHERE lectureType LIKE ? AND CONCAT(boardTitle, boardContent) LIKE "
						+ "? ORDER BY likeCount DESC, boardID DESC LIMIT " + pageNumber * 10 + ", " + pageNumber * 10 + 11;
			}
			
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, "%" + lectureType + "%"); //LIKE를 쿼리문에 적용하려면 %를 사용하여야 한다.첫번쨰 ?에 대입
			pstmt.setString(2, "%" + search + "%"); //사용자가 검색한 문자열을 두번째 ?에 대입
			rs = pstmt.executeQuery(); 
			
			boardList = new ArrayList<BoardDTO>();
			
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				
				board.setUserID(rs.getString("userID"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardContent(rs.getString("boardContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				//board.setBoardDate(rs.getString("boardDate").substring(0, 11));
				
				int boardTime = Integer.parseInt(rs.getString("boardDate").substring(11, 13));
				
				String timeType = "오전";
				if(boardTime >= 12) {
					timeType = "오후";
					boardTime -= 12;
				}
				
				board.setBoardDate(rs.getString("boardDate").substring(0,11) 
						+ " " + timeType + " " + boardTime + ":"
						+ rs.getString("boardDate").substring(14, 16) + "");
				
				board.setBoardHit(rs.getInt("boardHit"));
				board.setBoardFile(rs.getString("boardFile"));
				board.setBoardRealFile(rs.getString("boardRealFile"));
				board.setLectureType(rs.getString("lectureType"));
				board.setLikeCount(rs.getInt("likeCount"));
				
				boardList.add(board);
			}
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return boardList;
	}
	
	//조회수 증가
	public int hit(String boardID) {
		String SQL = "UPDATE BOARD SET boardHit = boardHit + 1 WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, boardID); //위의 쿼리문 첫번쨰 ? 대입
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			// 1이 리턴되면 회원가입이 성공한 것이다.
			
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //데이터베이스 오류
	}
	
	public String getFile(String boardID) {
		String SQL = "SELECT boardFile FROM BOARD WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; //특정한 SQL문을 실행한 이후에 나온 결과값을 처리
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, boardID); //사용자가 입력한 ID값을 위의 쿼리문의 첫번째 ?에 넣음
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				return rs.getString("boardFile");
			}
			return "";
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return "";
	}
	
	public String getRealFile(String boardID) {
		String SQL = "SELECT boardRealFile FROM BOARD WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; //특정한 SQL문을 실행한 이후에 나온 결과값을 처리
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, boardID); //사용자가 입력한 ID값을 위의 쿼리문의 첫번째 ?에 넣음
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) {
				return rs.getString("boardRealFile");
			}
			return "";
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return "";
	}
	
	// 게시글 수정
	public int update(String boardID, String boardTitle, String boardContent, String boardFile, String boardRealFile, String lectureType) {
		String SQL = "UPDATE BOARD SET boardTitle = ?, boardContent = ?, boardFile = ?, boardRealFile = ?, lectureType = ? WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, boardTitle);
			pstmt.setString(2, boardContent);
			pstmt.setString(3, boardFile);
			pstmt.setString(4, boardRealFile);
			pstmt.setString(5, lectureType);
			pstmt.setInt(6, Integer.parseInt(boardID));
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			// 1이 리턴되면 회원가입이 성공한 것이다.
			
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //데이터베이스 오류
	}
	
	// 게시글 삭제
	public int delete(String boardID) {
		String SQL = "DELETE FROM BOARD WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setInt(1, Integer.parseInt(boardID));
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			// 1이 리턴되면 회원가입이 성공한 것이다.
			
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //데이터베이스 오류
	}
	
	public int like(String boardID) { //강의평가 게시글 번호를 매개변수로 받아와 추천수를 증가시키는 매소드
		String SQL = "UPDATE BOARD SET likeCount = likeCount + 1 WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setInt(1, Integer.parseInt(boardID)); //위의 쿼리문 첫번쨰 ?, 대입 DB의 eID는 인트형이라 캐스팅
			return pstmt.executeUpdate(); 
			
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //데이터베이스 오류
	}
	
	// 나의 활동의 나의 글
	public ArrayList<BoardDTO> getMyList(String userID) {
		
		ArrayList<BoardDTO> boardList = null;
		String SQL = "SELECT * FROM BOARD WHERE userID = ? ORDER BY boardID DESC";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; //특정한 SQL문을 실행한 이후에 나온 결과값을 처리
		
		try {
			
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, userID); 
			rs = pstmt.executeQuery(); 
			
			boardList = new ArrayList<BoardDTO>();
			
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				
				board.setUserID(rs.getString("userID"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardContent(rs.getString("boardContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				//board.setBoardDate(rs.getString("boardDate").substring(0, 11));
				
				int boardTime = Integer.parseInt(rs.getString("boardDate").substring(11, 13));
				
				String timeType = "오전";
				if(boardTime >= 12) {
					timeType = "오후";
					boardTime -= 12;
				}
				
				board.setBoardDate(rs.getString("boardDate").substring(0,11) 
						+ " " + timeType + " " + boardTime + ":"
						+ rs.getString("boardDate").substring(14, 16) + "");
				
				board.setBoardHit(rs.getInt("boardHit"));
				board.setBoardFile(rs.getString("boardFile"));
				board.setBoardRealFile(rs.getString("boardRealFile"));
				board.setLectureType(rs.getString("lectureType"));
				board.setLikeCount(rs.getInt("likeCount"));
				
				boardList.add(board);
			}
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return boardList;
	}
	
	public ArrayList<BoardDTO> getLikeList() {
		
		ArrayList<BoardDTO> boardList = null;
		String SQL = "SELECT * FROM BOARD WHERE likeCount != 0 ORDER BY likeCount DESC, boardID DESC";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; //특정한 SQL문을 실행한 이후에 나온 결과값을 처리
		
		try {
			
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			rs = pstmt.executeQuery(); 
			
			boardList = new ArrayList<BoardDTO>();
			
			while(rs.next()) {
				BoardDTO board = new BoardDTO();
				
				board.setUserID(rs.getString("userID"));
				board.setBoardID(rs.getInt("boardID"));
				board.setBoardTitle(rs.getString("boardTitle").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				board.setBoardContent(rs.getString("boardContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
				//board.setBoardDate(rs.getString("boardDate").substring(0, 11));
				
				int boardTime = Integer.parseInt(rs.getString("boardDate").substring(11, 13));
				
				String timeType = "오전";
				if(boardTime >= 12) {
					timeType = "오후";
					boardTime -= 12;
				}
				
				board.setBoardDate(rs.getString("boardDate").substring(0,11) 
						+ " " + timeType + " " + boardTime + ":"
						+ rs.getString("boardDate").substring(14, 16) + "");
				
				board.setBoardHit(rs.getInt("boardHit"));
				board.setBoardFile(rs.getString("boardFile"));
				board.setBoardRealFile(rs.getString("boardRealFile"));
				board.setLectureType(rs.getString("lectureType"));
				board.setLikeCount(rs.getInt("likeCount"));
				
				boardList.add(board);
			}
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return boardList;
	}
	
}
