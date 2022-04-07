package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import util.DatabaseUtil;

public class UserDAO { //database access object
	
	
	public int login(String userID, String userPassword) { //로그인
		String SQL = "SELECT userPassword FROM USER WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);//SQL 인젝션 공격 방지
			pstmt.setString(1, userID); 
			rs = pstmt.executeQuery(); //위의 결과(비밀번호)를 rs에 담음, 조회(SELECT)문은 executeQuery()를 이용해  리서트셋을 통해 담아야 한다.
			
			if(rs.next()) { //결과가 존재하면 즉 해당 ID에 대한 비밀번호가 DB에 있으면 
				if(rs.getString(1).equals(userPassword)) { //실제 DB에 저장되어 있는 비밀번호와 사용자가 입력한 비밀번호가 일치하면
					return 1; //로그인 성공
				}
				else {
					return 0; //ID는 존재하나 비밀번호가 틀림
				}
			}
			return -1; //ID 없음
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			//자원 해제해주는 것이 GC 호출을 줄이기에 반드시 자원 해제 해주자. 
		}
		return -2;
	}
	
	public int registerCheck(String userID) { //아이디 중복 체크
		String SQL = "SELECT * FROM USER WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) { //아이디가 이미 존재  
				return 0; //이미 존재하는 회원
			}
			else if(userID.equals("")) { //빈칸 입력
				return 2;
			}
			else {
				return 1; //가입 가능한 회원(아이디만)
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -2; 
	}
	
	public int userNameCheck(String userName) { //닉네임 중복 체크
		String SQL = "SELECT * FROM USER WHERE userName = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userName); 
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) { //닉네임이 존재  
				return 0; //이미 존재하는 닉네임
			} else if(userName.equals("")) { //빈 칸으로 중복 확인
				return 2;
			}
			else {
				return 1; //사용 가능한 닉네임
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -2; 
	}
	
	public int register(String userID, String userPassword, String userName, String userDepartment, String userGender,
			String userProfile, String userEmail, String userEmailHash) { //회원가입
		String SQL = "INSERT INTO USER VALUES (?, ?, ?, ?, ?, ?, ?, ?, false)";
		//user테이블에 회원 정보를 insert 하는 쿼리문
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID); 
			pstmt.setString(2, userPassword);
			pstmt.setString(3, userName);
			pstmt.setString(4, userDepartment);
			pstmt.setString(5, userGender);
			pstmt.setString(6, userProfile);
			pstmt.setString(7, userEmail);
			pstmt.setString(8, userEmailHash);
			return pstmt.executeUpdate(); 
			
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; 
	}
	
	public boolean setUserEmail(String userID, String userNewEmail) {
		String SQL = "UPDATE USER SET userEmail = ? WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userNewEmail);
			pstmt.setString(2, userID);
			pstmt.executeUpdate(); //이메일이 인증된 회원으로 정보수정
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return false; //이메일 인증 안된 회원임
	}
	
	public boolean setUserEmailHash(String userID, String userNewEmailHash) {
		String SQL = "UPDATE USER SET userEmailHash = ? WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userNewEmailHash); 
			pstmt.setString(2, userID);
			pstmt.executeUpdate();
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return false; //이메일 인증 안된 회원임
	}
	
	public boolean setUserEmailChecked(String userID) { //이메일 인증을 할 수 있게 하고 인증 완료를 처리하게 함
		String SQL = "UPDATE USER SET userEmailChecked = true WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.executeUpdate(); 
			return true; //한번 인증을 한 사용자도 또 인증을 할 수 있게 true 반환
			
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return false; //이메일 인증 안된 회원임
	}
	
	//학우 닉네임으로 찾고 채팅방 입장을 위해 ID 반환
	public String getUserID(String userName) {
		String SQL = "SELECT userID FROM USER WHERE userName = ? AND userEmailChecked = TRUE";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userName);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}
			else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return null; 
	}
	
	//닉네임으로 학부 반환
	public String getUserDepartment(String userName) {
		String SQL = "SELECT userDepartment FROM USER WHERE userName = ? AND userEmailChecked = TRUE";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
			
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userName); 
			rs = pstmt.executeQuery();
				
			if(rs.next()) {
				return rs.getString(1);
			}
			else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return null; 
	}

	public UserDTO getUser(String userID) {
		UserDTO user = new UserDTO();
		String SQL = "SELECT * FROM USER WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) { //아이디가 존재  
				user.setUserID(userID);
				user.setUserPassword(rs.getString("userPassword"));
				user.setUserName(rs.getString("userName"));
				user.setUserDepartment(rs.getString("userDepartment"));
				user.setUserGender(rs.getString("userGender"));
				user.setUserProfile(rs.getString("userProfile"));
				user.setUserEmail(rs.getString("userEmail"));
				user.setUserEmailHash(rs.getString("userEmailHash"));
				user.setUserEmailChecked(rs.getBoolean("userEmailChecked"));
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return user;
	}
	
	public int profile(String userID, String userProfile) { //프로필 사진 변경
		String SQL = "UPDATE USER SET userProfile = ? WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userProfile);
			pstmt.setString(2, userID);
			return pstmt.executeUpdate(); //성공하면 1
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; 
	}
	
	public String getProfile(String userID) { //프로필 사진 출력
		String SQL = "SELECT userProfile FROM USER WHERE userID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID); 
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) { 
				if(rs.getString("userProfile").equals("")) { //회원가입 후 프로필 변경을 한번도 하지 않은 사용자일 경우
					return "http://localhost:8080/Hanseo/images/icon.jpg"; //기본프로필
				}
				return "http://localhost:8080/Hanseo/upload/" + rs.getString("userProfile");
			}	
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
			}
		return "http://localhost:8080/Hanseo/images/icon.jpg";
	}
}

